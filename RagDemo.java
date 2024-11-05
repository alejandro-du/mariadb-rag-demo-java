///usr/bin/env ./jbang-wrapper/jbang --quiet "$0" "$@" ; exit $?

//DEPS com.fasterxml.jackson.core:jackson-databind:2.18.1
//DEPS com.konghq:unirest-java:3.14.5
//DEPS org.mariadb.jdbc:mariadb-java-client:3.5.0
//DEPS org.sql2o:sql2o:1.8.0
//DEPS org.slf4j:slf4j-simple:2.0.16

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.Unirest;
import org.sql2o.*;
import java.util.stream.Collectors;

public class RagDemo {

	static {
		Unirest.config().socketTimeout(120000).connectTimeout(120000);
	}

	public static void main(String[] args) throws Exception {
		var input = System.console().readLine("I'm looking for: ");

		System.out.println("\nFinding closest products...");
		var context = getContext(input);
		System.out.println("-------------------------------------------------------------");
		System.out.println(context);
		System.out.println("-------------------------------------------------------------\n");

		System.out.println("Generating response...");
		var prompt = buildPrompt(input, context);
		var response = getResponse(prompt);
		System.out.println(response);
	}

	private static String getContext(String input) throws Exception {
		var requestBody = """
				{ "model": "bert-cpp-minilm-v6", "input": %s }
				""".formatted(new ObjectMapper().writeValueAsString(input));

		var response = Unirest.post("http://localhost:8080/v1/embeddings")
				.header("Content-Type", "application/json")
				.body(requestBody)
				.asString().getBody();

		var connection = new Sql2o(
				"jdbc:mariadb://127.0.0.1:3306/demo", "root", "password").open();

		var table = connection.createQuery("""
				SELECT id, CONCAT(
					"Product: ", title, ". Stars: ", stars, ". Price: $", price, ". Category: ", category_name,
					". Best seller: ", CASE WHEN is_best_seller THEN "Yes" ELSE "No" END
				) AS description
				FROM products
				WHERE embedding IS NOT NULL
				ORDER BY VEC_Distance(embedding, VEC_FromText(JSON_EXTRACT(:response, '$.data[0].embedding')))
				LIMIT 10
				""")
				.addParameter("response", response)
				.executeAndFetchTable();

		return table.rows().stream()
				.map(row -> row.getString("description"))
				.collect(Collectors.joining("\n\n"));
	}

	private static String buildPrompt(String input, Object context) {
		return """
				You are a sales assistant. I'm looking for %s.

				Using the following information, recommend me a product in one single paragraph:

				%s
				""".formatted(input, context);
	}

	private static String getResponse(String prompt) throws Exception {
		var requestBody = """
				{ "model": "phi-2", "messages": [{"role": "user", "content": %s, "temperature": 0.4}] }
				""".formatted(new ObjectMapper().writeValueAsString(prompt));

		var response = Unirest.post("http://localhost:8080/v1/chat/completions")
				.header("Content-Type", "application/json")
				.body(requestBody)
				.asString().getBody();

		var mapper = new ObjectMapper();
		var jsonNode = mapper.readTree(response);
		return jsonNode.path("choices").get(0).path("message").path("content").asText();
	}

}

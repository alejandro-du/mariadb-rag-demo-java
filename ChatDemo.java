///usr/bin/env ./jbang-wrapper/jbang --quiet "$0" "$@" ; exit $?

//DEPS com.fasterxml.jackson.core:jackson-databind:2.18.1
//DEPS com.konghq:unirest-java:3.14.5
//DEPS org.mariadb.jdbc:mariadb-java-client:3.5.0
//DEPS org.sql2o:sql2o:1.8.0
//DEPS org.slf4j:slf4j-simple:2.0.16

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.Unirest;
import org.sql2o.*;
import java.util.stream.Collectors;

public class ChatDemo {

	private static String API_EMBEDDINGS_URL = "https://api.openai.com/v1/embeddings";
	private static String API_EMBEDDINGS_MODEL = "text-embedding-3-large";
	private static String API_CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
	private static String API_CHAT_MODEL = "gpt-4o-mini";
	private static String API_KEY = System.getenv("OPENAI_API_KEY");

	static {
		Unirest.config().socketTimeout(10 * 60 * 1000);
	}

	public static void main(String[] args) throws JsonProcessingException {
		var input = System.console().readLine("I'm looking for: ");

		System.out.println("Finding closest products...");
		var context = getContext(input);

		System.out.println("Generating response...");
		var prompt = buildPrompt(input, context);
		var response = getResponse(prompt);
		System.out.println(response);
	}

	private static String getContext(String input) throws JsonProcessingException {
		var requestBody = """
				{
					"model": %s,
					"input": %s
				}
				""".formatted(new ObjectMapper().writeValueAsString(API_EMBEDDINGS_MODEL), new ObjectMapper().writeValueAsString(input));

		var response = Unirest.post(API_EMBEDDINGS_URL)
				.header("Content-Type", "application/json")
				.body(requestBody)
				.header("Authorization", "Bearer " + API_KEY)
				.asString().getBody();

		try (var connection = new Sql2o(
				"jdbc:mariadb://127.0.0.1:3306/demo", "root", "password").open()) {

			var table = connection.createQuery("""
					SELECT id, CONCAT(
						ROW_NUMBER() OVER (), ". ",
						product_name, " (",
						"Category: ", category_name, ", ", root_category_name,
						" - Price: €", final_price,
						" - Rating: ", rating,
						"). ", description
					) AS product_summary
					FROM products
					WHERE embedding IS NOT NULL
					ORDER BY VEC_DISTANCE_COSINE(embedding, VEC_FromText(JSON_EXTRACT(:response, '$.data[0].embedding')))
					LIMIT 10
					""")
					.addParameter("response", response)
					.executeAndFetchTable();

			return table.rows().stream()
					.map(row -> row.getString("product_summary"))
					.collect(Collectors.joining("\n\n"));
		}
	}

	private static String buildPrompt(String input, Object context) {
		return """
				You are an expert sales assistant. Help me choose the best product for my needs.
				Recommend only one product from the list below and explain why it’s the best choice in one short paragraph.

				I'm looking for %s.

				Available products:

				%s
				""".formatted(input, context);
	}

	private static String getResponse(String prompt) throws JsonProcessingException {
		var mapper = new ObjectMapper();
		var requestBody = """
				{
					"model": %s,
					"messages": [
						{"role": "user", "content": %s}
					],
					"temperature": 0.1,
					"max_tokens": 150
				}
				""".formatted(mapper.writeValueAsString(API_CHAT_MODEL), mapper.writeValueAsString(prompt));

		var response = Unirest.post(API_CHAT_COMPLETIONS_URL)
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + API_KEY)
				.body(requestBody)
				.asString().getBody();

		var jsonNode = mapper.readTree(response);
		return jsonNode.path("choices").get(0).path("message").path("content").asText();
	}

}

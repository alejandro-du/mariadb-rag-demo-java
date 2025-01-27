///usr/bin/env ./jbang-wrapper/jbang --quiet "$0" "$@" ; exit $?

//DEPS com.fasterxml.jackson.core:jackson-databind:2.18.1
//DEPS org.jsoup:jsoup:1.18.3
//DEPS com.konghq:unirest-java:3.14.5
//DEPS org.mariadb.jdbc:mariadb-java-client:3.5.0
//DEPS org.sql2o:sql2o:1.8.0
//DEPS org.slf4j:slf4j-simple:2.0.16

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.Unirest;
import org.sql2o.*;
import org.sql2o.data.*;

public class ComputeVectors {

	private static String API_EMBEDDINGS_URL = "https://api.openai.com/v1/embeddings";
	private static String API_EMBEDDINGS_MODEL = "text-embedding-3-large";
	private static String API_KEY = System.getenv("OPENAI_API_KEY");

	static {
		Unirest.config().socketTimeout(2 * 60 * 1000);
	}

	public static void main(String[] args) throws Exception {
		try (var connection = new Sql2o(
				"jdbc:mariadb://127.0.0.1:3306/demo", "root", "password").open()) {

			var table = connection.createQuery("""
					SELECT id, CONCAT(
						product_name, " (",
						"Category: ", category_name, ", ", root_category_name,
						" - Price: €", final_price,
						" - Rating: ", rating,
						"). ", description, "."
					) AS product_summary
					FROM products
					WHERE embedding IS NULL
					""").executeAndFetchTableLazy();

			for (Row row : table.rows()) {
				var id = row.getString("id");
				var productSummary = row.getString("product_summary");

				var requestBody = """
						{
							"model": %s,
							"input": %s
						}
						""".formatted(new ObjectMapper().writeValueAsString(API_EMBEDDINGS_MODEL), new ObjectMapper().writeValueAsString(productSummary));

				var response = Unirest.post(API_EMBEDDINGS_URL)
						.header("Content-Type", "application/json")
						.body(requestBody)
						.header("Authorization", "Bearer " + API_KEY)
						.asString().getBody();

				connection.createQuery("""
						UPDATE products
						SET embedding = VEC_FromText(JSON_EXTRACT(:response, '$.data[0].embedding'))
						WHERE id = :id
						""")
						.addParameter("response", response)
						.addParameter("id", id)
						.executeUpdate();

				System.out.println("Updated embedding for product ID: " + id);
			}
		}

		System.out.println("All embeddings updated!");
	}
}

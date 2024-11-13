///usr/bin/env ./jbang-wrapper/jbang --quiet "$0" "$@" ; exit $?

//DEPS com.fasterxml.jackson.core:jackson-databind:2.18.1
//DEPS com.konghq:unirest-java:3.14.5
//DEPS org.mariadb.jdbc:mariadb-java-client:3.5.0
//DEPS org.sql2o:sql2o:1.8.0
//DEPS org.slf4j:slf4j-simple:2.0.16

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.Unirest;
import org.sql2o.*;
import org.sql2o.data.*;

public class UpdateVectors {

	public static void main(String[] args) throws Exception {
		try (var connection = new Sql2o(
				"jdbc:mariadb://127.0.0.1:3306/demo", "root", "password").open()) {

			var table = connection.createQuery("""
					SELECT id, CONCAT(
						"Product: ", title, ". Stars: ", stars, ". Price: $", price, ". Category: ", category_name,
						". Best seller: ", CASE WHEN is_best_seller THEN "Yes" ELSE "No" END
					) AS description
					FROM products
					WHERE embedding IS NULL
					""").executeAndFetchTableLazy();

			for (Row row : table.rows()) {
				var id = row.getString("id");
				var description = row.getString("description");

				var requestBody = """
						{
							"model": "bert-cpp-minilm-v6",
							"input": %s
						}
						""".formatted(new ObjectMapper().writeValueAsString(description));

				var response = Unirest.post("http://localhost:8080/v1/embeddings")
						.header("Content-Type", "application/json")
						.body(requestBody)
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

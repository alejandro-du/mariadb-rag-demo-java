# Demo: RAG with MariaDB (Java)

This demo shows how to build a Retrieval-Augmented Generation (RAG) application
using [MariaDB](https://mariadb.com/), [LocalAI](https://localai.io/), and [Java](https://en.wikipedia.org/wiki/Java_(programming_language)).

**Note:** This demo uses a _preview version_ of MariaDB which includes SQL syntax that
will likely change in the next GA (stable) version.

## Prerequisites

You only need [Docker](https://www.docker.com/) installed and running on your computer to run this demo.

## Setup

Start the LocalAI and MariaDB services (see the **docker-compose.yml** file):

```shell
docker compose up -d
```

Download the dataset:
https://www.kaggle.com/datasets/asaniczka/amazon-canada-products-2023-2-1m-products

Move to the directory where you downloaded the dataset and create a _slice_ of it. For example 50k products:

```shell
head -n 50001 ~/Downloads/amz_ca_total_products_data_processed.csv > ~/Downloads/slice.csv
```

Copy the file to the MariaDB Docker container:

```shell
docker cp ~/Downloads/slice.csv mariadb:/slice.csv
```

Connect to the MariaDB server:

```shell
docker exec -it mariadb mariadb -u root -p'password' demo
```

Load the data from the CSV file into the MariaDB database:

```sql
LOAD DATA LOCAL INFILE '/slice.csv'
INTO TABLE products
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(
    asin,
    title,
    img_url,
    product_url,
    stars,
    reviews,
    price,
    list_price,
    category_name,
    is_best_seller,
    bought_in_last_month
);
```

Exit the MariaDB client:

```shell
exit
```

Calculate the vector embeddings:

```shell
./UpdateVectors.java
```

Be patient. This might take a lot of time depending on your hardware and the size of the slice that you took.

## Run the demo

Before you run the demo double-check the models downloaded successfully:

```shell
docker logs -f local-ai
```

Start the demo:

```shell
./RagDemo.java
```

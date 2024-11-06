# Demo: RAG with MariaDB

This demo demonstrates how to build a Retrieval-Augmented Generation (RAG) application
using MariaDB and LocalAI.

**Note:** This demo uses a _preview version_ of MariaDB which includes SQL syntax that
will likely change in the next GA (stable) version.

## Prerequisites
- Docker

## Setup

Start the LocalAI and MariaDB services:
```shell
docker compose up -d
```

Check the model downloads progress:
```
docker logs -f local-ai
```

Download the dataset:
https://www.kaggle.com/datasets/asaniczka/amazon-canada-products-2023-2-1m-products

Create a slice of the dataset. For example 50k products:
```shell
head -n 50001 ~/Datasets/amz_ca_total_products_data_processed.csv > ~/Datasets/slice.csv
```

Connect to the MariaDB server:
```shell
docker exec -it mariadb mariadb -u root -p'password' demo
```

Load the data from the CSV file into the MariaDB database:
```sql
LOAD DATA LOCAL INFILE '/Users/alejandro/Datasets/slice.csv'
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

Calculate the vector embeddings:
```shell
cd rag-demo
./UpdateVectors.java
```

## Run the demo

Start the demo:
```shell
./RagDemo.java
```

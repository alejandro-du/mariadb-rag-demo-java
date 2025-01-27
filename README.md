# Demo: RAG with MariaDB (Java)

This demo shows how to build a Retrieval-Augmented Generation (RAG) application
using [MariaDB](https://mariadb.com/), [LocalAI](https://localai.io/), and [Java](https://en.wikipedia.org/wiki/Java_(programming_language))  (with no AI frameworks for learning purposes).

**Note:** This demo uses an _RC version_ of MariaDB, which includes SQL syntax that might change in the next GA (stable) version.

## Prerequisites

You only need [Docker](https://www.docker.com/) installed and running on your computer to run this demo.

## Setup

Start the LocalAI and MariaDB services (see the **docker-compose.yml** file):

```shell
docker compose up -d
```

This also creates the database schema and loads a [data set with around 1000 Walmart products](https://github.com/luminati-io/Walmart-dataset-samples/blob/main/walmart-products.csv).

Wait until the AI models have been downloaded successfully:

```shell
docker logs -f local-ai
```

Wait until you see the message _LocalAI API is listening!_ in the log.

## Calculate the vector embeddings

To calculate the vector embeddings for all the products in the database, run:

```shell
./ComputeVectors.java
```

Be patient. This might take some time depending on your hardware.

## Run the chat demo

To run the chat demo, execute the following:

```shell
./ChatDemo.java
```

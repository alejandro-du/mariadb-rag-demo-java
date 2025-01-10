# Demo: RAG with MariaDB (Java)

This demo shows how to build a Retrieval-Augmented Generation (RAG) application
using [MariaDB](https://mariadb.com/), [LocalAI](https://localai.io/), and [Java](https://en.wikipedia.org/wiki/Java_(programming_language)).

**Note:** This demo uses an _RC version_ of MariaDB which includes SQL syntax that
might change in the next GA (stable) version.

## Prerequisites

You only need [Docker](https://www.docker.com/) installed and running on your computer to run this demo.

## Setup

Start the LocalAI and MariaDB services (see the **docker-compose.yml** file):

```shell
docker compose up -d
```

This also creates the database schema and loads a [data set with around 1000 Walmart products](https://github.com/luminati-io/Walmart-dataset-samples/blob/main/walmart-products.csv).

Calculate the vector embeddings:

```shell
./UpdateVectors.java
```

Be patient. This might take some time depending on your hardware.

## Run the demo

Before you run the demo double-check the models downloaded successfully:

```shell
docker logs -f local-ai
```

Start the demo:

```shell
./RagDemo.java
```

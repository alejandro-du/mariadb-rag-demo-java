# Demo: RAG with MariaDB (Java)

This demo shows how to build a Retrieval-Augmented Generation (RAG) application
using [MariaDB](https://mariadb.com/), [OpenAI API](https://platform.openai.com/docs/overview), and [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) (with no AI frameworks for learning purposes).

**Note:** This demo uses an _RC version_ of MariaDB, which includes SQL syntax that might change in the next GA (stable) version.

## Prerequisites

* [Docker](https://www.docker.com/)
* [OpenAI API key](https://openai.com/index/openai-api/)

## Setup

1. Set the `OPENAI_API_KEY` environment variable to your OpenAI API key. For example (Linux/MacOS):

```shell
export OPENAI_API_KEY=sk-example1234567890abcdef1234567890abcdef
```

2. Start MariaDB (see the **docker-compose.yml** file):

```shell
docker compose up -d
```

3. Check that MariaDB started successfully:

```shell
docker logs mariadb
```

## Calculate the vector embeddings

To calculate the vector embeddings for all the products in the database, run:

```shell
./ComputeVectors.java
```

## Run the chat demo

To run the chat demo, execute the following:

```shell
./ChatDemo.java
```

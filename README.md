# arXiv MCP Server

This is a Model Context Protocol (MCP) server implementation for the [arXiv](https://arxiv.org/) API. It allows AI models to search for papers, retrieve paper details, and access PDF content from arXiv.

## Features

- **Search Papers**: Search for pre-prints using arXiv's query syntax. Supports filtering, sorting, and pagination.
- **Get Paper Details**: Retrieve detailed metadata (title, summary, authors, categories) for specific papers.
- **Get PDF**: Download the PDF content of a paper.

## Tools and Resources

### Tools
- `search_papers`: Search for papers on arXiv.
    - `query`: Search query (e.g., `all:electron`, `ti:learning`).
    - `max_results`: Maximum number of results to return (default: 5).
    - `sort_by`: Sort criteria (`relevance`, `lastUpdatedDate`, `submittedDate`).
    - `sort_order`: Sort order (`ascending`, `descending`).
- `get_paper_details`: Get details for a list of paper IDs.

### Resources
- `arxiv://papers/{id}/abstract`: Get the abstract of the paper.
- `arxiv://papers/{id}/metadata`: Get full metadata for a paper as JSON.
- `https://arxiv.org/pdf/{id}`: Get the PDF content of a paper (Base64 encoded).
- `arxiv://taxonomy`: Get the list of arXiv subject categories and their codes.

### Prompts
- `summarize_paper`: Summarize a specific paper by its ID.
- `construct_search_query`: Helper to build a complex arXiv API search query.
    - `topic`: Topic or keywords.
    - `author`: Author name.
    - `category`: Category code (e.g. `cs.AI`).
    - `year`: Year (e.g. `2024`).

## Prerequisites

- JDK 17 or later
- Maven (wrapper included)

## Building the Server

To build the project and generate the executable jar:

```bash
./mvnw clean package -DskipTests
```

This will produce the runner jar at `target/quarkus-app/quarkus-run.jar`.

## Running the Server

To run the server locally using the built jar:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## Running with MCP Inspector

You can test the server using the [MCP Inspector](https://github.com/modelcontextprotocol/inspector).

1.  Start the server in development mode:
    ```bash
    ./mvnw quarkus:dev
    ```
    The server will listen on port `8080` (Streamable HTTP).

2.  In a separate terminal, launch the MCP Inspector:
    ```bash
    npx @modelcontextprotocol/inspector http://localhost:8080/mcp
    ```

3.  Open the URL provided by the inspector in your browser to interact with the tools and resources.

## Installation for Gemini CLI

To use this server with the [Gemini CLI](https://geminicli.com/) (or other valid MCP clients), you need to configure it to use the `stdio` transport (but it's also possible to use the Streamable HTTP transport, but you have to deploy the MCP server or run it locally).

1.  Build the project as described in the "Building the Server" section.
2.  Add the server configuration to your MCP config file (e.g., `~/.gemini/mcp-servers.json`):

```json
{
  "mcpServers": {
    "arxiv": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/your/project/target/quarkus-app/quarkus-run.jar"
      ]
    }
  }
}
```

> **Note:** Replace `/absolute/path/to/your/project` with the actual path to your project directory.

## License

This project is licensed under the Apache License, Version 2.0.

## Disclaimer

This is not an official Google project.

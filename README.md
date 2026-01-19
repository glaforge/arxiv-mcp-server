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

## Quick Start with JBang (Recommended)

The easiest way to use this MCP server is via [JBang](https://jbang.dev/). It handles the JVM and dependencies automatically, so you don't need to build the project locally.

### 1. Install JBang
If you don't have JBang installed, you can install it via Homebrew:
```bash
brew install jbangdev/tap/jbang
```
(For other platforms, see the [JBang installation guide](https://jbang.dev/download/)).

### 2. Trust the Source
Since you are running a script from the internet, JBang requires you to trust the source. Run this command once:
```bash
jbang trust https://github.com/glaforge/arxiv-mcp-server/releases/latest/
```

### 3. Configure your Agent CLI

You can point your agent (Gemini CLI, Claude Code, etc.) at the JBang alias. This will automatically download and run the latest release.

#### Configuration Snippet
Add this to your `mcp-servers.json` or equivalent config file:

```json
{
  "mcpServers": {
    "arxiv": {
      "command": "jbang",
      "args": ["run", "arxiv-mcp@glaforge"]
    }
  }
}
```

---

## Local Development and Building

If you want to build and run the server locally, follow these steps.

### Prerequisites

- JDK 21 or later
- Maven (wrapper included)

### Building the Server

The project is configured to produce an **Uber-Jar** for easy distribution.

```bash
./mvnw clean package -DskipTests
```

This will produce the runner jar at `target/arxiv-mcp-server-runner.jar`.

### Running the Server

To run the built server:

```bash
java -jar target/arxiv-mcp-server-runner.jar
```

### Running with MCP Inspector

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

## Manual Configuration (Local Build)

If you prefer to run the version you built locally:

```json
{
  "mcpServers": {
    "arxiv": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/your/project/target/arxiv-mcp-server-runner.jar"
      ]
    }
  }
}
```

> **Note:** Replace `/absolute/path/to/your/project` with the actual path to your project directory.

### Development Mode (Live Reload)

While in development mode (`./mvnw quarkus:dev`), you can point your coding agent at your live running server for instant updates:

```json
{
  "mcpServers": {
    "arxiv": {
      "httpUrl": "http://localhost:8080/mcp"
    }
  }
}
```

## License

This project is licensed under the Apache License, Version 2.0.

## Disclaimer

This is not an official Google project.

# Deploying to Cloud Run (No-Build Method)

This project uses the Cloud Run **"Deploy from Source without Build"** method. This allows for extremely fast deployments by uploading a locally built Uber JAR directly to a Google-managed Java runtime.

## Benefits
- **Speed:** Bypasses Cloud Build and Buildpacks, reducing deployment time significantly.
- **Security:** Uses the managed `google-22` stack which receives automatic OS and JVM security patches from Google.
- **Simplicity:** No Dockerfile required for the production environment.

## Prerequisites
- Google Cloud SDK (`gcloud`) installed and authenticated.
- The `beta` component installed: `gcloud components install beta`.
- Project configured in gcloud: `gcloud config set project YOUR_PROJECT_ID`.

## Deployment Steps

### 1. Build the Uber JAR locally
The application must be packaged as a single self-contained JAR.
```bash
./mvnw package -DskipTests
```

### 2. Prepare the deployment artifact
To keep the upload lean, we copy only the runner JAR into a temporary directory.
```bash
mkdir -p target/deploy-tmp
cp target/arxiv-mcp-server-runner.jar target/deploy-tmp/
```

### 3. Deploy to Cloud Run
Run the following command to upload the JAR and start the service.

```bash
gcloud beta run deploy arxiv-mcp-server \
    --source target/deploy-tmp/ \
    --no-build \
    --base-image us-central1-docker.pkg.dev/serverless-runtimes/google-22/runtimes/java21 \
    --command "java" \
    --args="-jar,arxiv-mcp-server-runner.jar" \
    --allow-unauthenticated \
    --region us-central1
```

## Configuration Notes
- **Port:** The application is configured in `src/main/resources/application.properties` to listen on port `8080` (`quarkus.http.port=8080`), which is the default for Cloud Run.
- **Base Image:** We use the `google-22` Java 21 runtime. Note that Java 21 is currently not supported on the `google-24` stack (Ubuntu 24.04) yet, so `google-22` (Ubuntu 22.04) is the correct choice.
- **CORS:** CORS support is enabled via `quarkus.http.cors.enabled=true`.
- **Stdio Transport:** The Stdio transport is explicitly disabled in the `prod` profile (`%prod.quarkus.mcp.server.stdio.enabled=false`). This is because Cloud Run closes `System.in` immediately, which would otherwise cause the Quarkus MCP extension to trigger a graceful shutdown. HTTP transport remains active for the MCP Inspector and other clients.


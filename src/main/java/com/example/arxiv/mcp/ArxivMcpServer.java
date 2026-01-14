/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.arxiv.mcp;

import com.example.arxiv.model.Feed;
import com.example.arxiv.model.SortBy;
import com.example.arxiv.model.SortOrder;
import com.example.arxiv.service.ArxivClient;
import com.example.arxiv.service.PdfClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import io.quarkus.logging.Log;
import io.quarkiverse.mcp.server.BlobResourceContents;
import io.quarkiverse.mcp.server.Prompt;
import io.quarkiverse.mcp.server.PromptArg;
import io.quarkiverse.mcp.server.PromptMessage;
import io.quarkiverse.mcp.server.Resource;
import io.quarkiverse.mcp.server.ResourceTemplate;
import io.quarkiverse.mcp.server.ResourceTemplateArg;
import io.quarkiverse.mcp.server.TextResourceContents;
import io.quarkiverse.mcp.server.Tool;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

public class ArxivMcpServer {

    @Inject
    @RestClient
    ArxivClient arxivClient;

    @Inject
    @RestClient
    PdfClient pdfClient;

    private final XmlMapper xmlMapper = new XmlMapper();

    @Tool(
        description = "Search for papers on arXiv",
        name = "search_papers")
    public Feed searchPapers(String query, int maxResults, SortBy sortBy, SortOrder sortOrder) {
        Log.info("searchPapers called with query: " + query + ", maxResults: " + maxResults + ", sortBy: " + sortBy + ", sortOrder: " + sortOrder);

        return performSearch(query, null, 0, maxResults == 0 ? 5 : maxResults,
            sortBy == null ? null : sortBy.name(),
            sortOrder == null ? null : sortOrder.name());
    }

    @Tool(
        description = "Get details for specific arXiv papers by ID",
        name = "get_paper_details")
    public Feed getPaperDetails(List<String> ids) {
        Log.info("getPaperDetails called with ids: " + ids);

        String idList = String.join(",", ids);
        return performSearch(null, idList, 0, ids.size(), null, null);
    }

    @ResourceTemplate(
        uriTemplate = "arxiv://papers/{id}/abstract",
        description = "The abstract of the arXiv paper")
    public TextResourceContents getAbstract(@ResourceTemplateArg String id) {
        Log.info("getAbstract called with id: " + id);

        Feed feed = performSearch(null, id, 0, 1, null, null);
        if (feed.entries != null && !feed.entries.isEmpty()) {
            return TextResourceContents.create("arxiv://papers/" + id + "/abstract",
                feed.entries.get(0).summary);
        }
        throw new RuntimeException("Paper not found: " + id);
    }

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @ResourceTemplate(
        uriTemplate = "arxiv://papers/{id}/metadata",
        description = "The full metadata of the arXiv paper", mimeType = "application/json")
    public TextResourceContents getMetadata(@ResourceTemplateArg String id) {
        Log.info("getMetadata called with id: " + id);

        Feed feed = performSearch(null, id, 0, 1, null, null);
        if (feed.entries != null && !feed.entries.isEmpty()) {
            try {
                return TextResourceContents.create("arxiv://papers/" + id + "/metadata",
                    jsonMapper.writeValueAsString(feed.entries.get(0)));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize paper metadata", e);
            }
        }
        throw new RuntimeException("Paper not found: " + id);
    }

    @ResourceTemplate(
        uriTemplate = "https://arxiv.org/pdf/{id}",
        description = "The PDF of the arXiv paper encoded in base64",
        mimeType = "application/pdf")
    public BlobResourceContents getPdf(@ResourceTemplateArg String id) {
        Log.info("getPdf called with id: " + id);

        try (InputStream is = pdfClient.getPdf(id)) {
            byte[] bytes = is.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return new BlobResourceContents("https://arxiv.org/pdf/" + id, base64, "application/pdf");
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch PDF for " + id, e);
        }
    }

    @Prompt(
        name = "summarize_paper",
        description = "Summarize the given paper")
    public PromptMessage summarizePaper(String id) {
        Log.info("summarizePaper called with id: " + id);

        Feed feed = performSearch(null, id, 0, 1, null, null);
        if (feed.entries != null && !feed.entries.isEmpty()) {
            String summary = feed.entries.get(0).summary;
            return PromptMessage.withUserRole(
                String.format("""
                    Please summarize this paper abstract (ID: %s):

                    %s""", id, summary));
        }
        return PromptMessage.withUserRole("Error: Paper not found");
    }

    @Resource(
        uri = "arxiv://taxonomy",
        description = "List of arXiv categories and their codes",
        mimeType = "text/markdown")
    public TextResourceContents getTaxonomy() {
        Log.info("getTaxonomy called");

        return TextResourceContents.create("arxiv://taxonomy",
            """
            # arXiv Category Taxonomy

            ## Physics
            *   **Astrophysics** (astro-ph)
            *   **Condensed Matter** (cond-mat)
            *   **General Relativity and Quantum Cosmology** (gr-qc)
            *   **High Energy Physics** (hep-ex, hep-lat, hep-ph, hep-th)
            *   **Mathematical Physics** (math-ph)
            *   **Nonlinear Sciences** (nlin)
            *   **Nuclear** (nucl-ex, nucl-th)
            *   **Physics** (physics)
            *   **Quantum Physics** (quant-ph)

            ## Mathematics (math)
            *   Includes: Algebraic Geometry, Analysis, Combinatorics, Probability, Statistics, etc.

            ## Computer Science (cs)
            *   **Artificial Intelligence** (cs.AI)
            *   **Computation and Language** (cs.CL)
            *   **Computer Vision** (cs.CV)
            *   **Machine Learning** (cs.LG)
            *   **Robotics** (cs.RO)
            *   **Software Engineering** (cs.SE)
            *   ... and many more.

            ## Quantitative Biology (q-bio)
            *   Biomolecules, Genomics, Neuroscience, etc.

            ## Quantitative Finance (q-fin)
            *   Computational Finance, Economics, etc.

            ## Statistics (stat)
            *   Machine Learning (stat.ML), etc.

            ## Electrical Engineering and Systems Science (eess)
            *   Audio/Speech (eess.AS), Image/Video (eess.IV), Signal Processing (eess.SP), etc.

            ## Economics (econ)
            *   Econometrics (econ.EM)
            """);
    }

    @Prompt(
        name = "construct_search_query",
        description = "Helper to construct an arXiv search query")
    public PromptMessage constructSearchQuery(
        @PromptArg(description = "Topic or keywords") String topic,
        @PromptArg(description = "Author name") String author,
        @PromptArg(description = "Category code (e.g. cs.AI)") String category,
        @PromptArg(description = "Year (e.g. 2024)") String year) {
            Log.info("constructSearchQuery called with topic: " + topic + ", author: " + author + ", category: " + category + ", year: " + year);

        StringBuilder query = new StringBuilder();
        if (topic != null && !topic.isEmpty()) {
            query.append("all:").append(topic);
        }
        if (author != null && !author.isEmpty()) {
            if (query.length() > 0) query.append(" AND ");
            query.append("au:").append(author);
        }
        if (category != null && !category.isEmpty()) {
            if (query.length() > 0) query.append(" AND ");
            query.append("cat:").append(category);
        }
        if (year != null && !year.isEmpty()) {
            if (query.length() > 0) query.append(" AND ");
            query.append("submittedDate:[").append(year).append("01010000 TO ").append(year).append("12312359]");
        }

        return PromptMessage.withUserRole(
            String.format("""
                Here is a constructed search query for the arXiv API based on your criteria:

                `%s`

                 You can use this query with the `search_papers` tool.
                """, query.toString()));
    }

    private Feed performSearch(String searchQuery, String idList, int start, int maxResults, String sortBy, String sortOrder) {
        String result = arxivClient.search(searchQuery, idList, start, maxResults, sortBy, sortOrder);
        try {
            return xmlMapper.readValue(result, Feed.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse ArXiv response", e);
        }
    }
}

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
import io.quarkiverse.mcp.server.BlobResourceContents;
import io.quarkiverse.mcp.server.Prompt;
import io.quarkiverse.mcp.server.PromptMessage;
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

    private final com.fasterxml.jackson.dataformat.xml.XmlMapper xmlMapper = new com.fasterxml.jackson.dataformat.xml.XmlMapper();

    @Tool(description = "Search for papers on arXiv")
    public Feed search_papers(String query, int max_results, SortBy sort_by, SortOrder sort_order) {
        return performSearch(query, null, 0, max_results == 0 ? 5 : max_results,
            sort_by == null ? null : sort_by.name(),
            sort_order == null ? null : sort_order.name());
    }

    @Tool(description = "Get details for specific papers by ID")
    public Feed get_paper_details(List<String> ids) {
        String idList = String.join(",", ids);
        return performSearch(null, idList, 0, ids.size(), null, null);
    }

    @ResourceTemplate(uriTemplate = "arxiv://papers/{id}/abstract", description = "The abstract of the paper")
    public TextResourceContents getAbstract(@ResourceTemplateArg String id) {
        Feed feed = performSearch(null, id, 0, 1, null, null);
        if (feed.entries != null && !feed.entries.isEmpty()) {
            return TextResourceContents.create("arxiv://papers/" + id + "/abstract", feed.entries.get(0).summary);
        }
        throw new RuntimeException("Paper not found: " + id);
    }

    private final com.fasterxml.jackson.databind.ObjectMapper jsonMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    @ResourceTemplate(uriTemplate = "arxiv://papers/{id}/metadata", description = "The full metadata of the paper", mimeType = "application/json")
    public TextResourceContents getMetadata(@ResourceTemplateArg String id) {
        Feed feed = performSearch(null, id, 0, 1, null, null);
        if (feed.entries != null && !feed.entries.isEmpty()) {
            try {
                return TextResourceContents.create("arxiv://papers/" + id + "/metadata", jsonMapper.writeValueAsString(feed.entries.get(0)));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize paper metadata", e);
            }
        }
        throw new RuntimeException("Paper not found: " + id);
    }


    @ResourceTemplate(uriTemplate = "arxiv://papers/{id}/pdf", description = "The PDF of the paper", mimeType = "application/pdf")
    public BlobResourceContents getPdf(@ResourceTemplateArg String id) {
        try (InputStream is = pdfClient.getPdf(id)) {
            byte[] bytes = is.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return BlobResourceContents.create("arxiv://papers/" + id + "/pdf", base64);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch PDF for " + id, e);
        }
    }

    @Prompt(name = "summarize_paper", description = "Summarize the given paper")
    public PromptMessage summarize_paper(String id) {
        Feed feed = performSearch(null, id, 0, 1, null, null);
        if (feed.entries != null && !feed.entries.isEmpty()) {
            String summary = feed.entries.get(0).summary;
            return PromptMessage.withUserRole("Please summarize this paper abstract:\n\n" + summary);
        }
        return PromptMessage.withUserRole("Error: Paper not found");
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

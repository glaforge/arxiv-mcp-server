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
package io.github.glaforge.mcp.arxiv.mcp;

import io.quarkiverse.mcp.server.PromptMessage;
import io.quarkiverse.mcp.server.TextResourceContents;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ArxivExtraFeaturesTest {

    @Inject
    ArxivMcpServer server;

    @Test
    public void testTaxonomyResource() {
        TextResourceContents content = server.getTaxonomy();
        assertNotNull(content);
        assertNotNull(content.text());
        assertTrue(content.text().contains("# arXiv Category Taxonomy"));
        assertTrue(content.text().contains("Physics"));
        assertTrue(content.text().contains("Computer Science"));
        assertTrue(content.text().contains("cs.AI"));
    }

    @Test
    public void testConstructSearchQueryPrompt() {
        PromptMessage message = server.constructSearchQuery(
            "deep learning", // topic
            "LeCun",         // author
            "cs.AI",        // category
            "2023"          // year
        );

        assertNotNull(message);
        String text = message.content().asText().text();
        assertTrue(text.contains("all:deep learning"));
        assertTrue(text.contains("au:LeCun"));
        assertTrue(text.contains("cat:cs.AI"));
        assertTrue(text.contains("submittedDate:[202301010000 TO 202312312359]"));
    }

    @Test
    public void testConstructSearchQueryPromptPartial() {
        PromptMessage message = server.constructSearchQuery(
            "transformers", // topic
            null,
            null,
            null
        );

        assertNotNull(message);
        String text = message.content().asText().text();
        assertTrue(text.contains("all:transformers"));
        // ensure other fields are not present
        assertTrue(!text.contains("au:"));
        assertTrue(!text.contains("cat:"));
    }
}

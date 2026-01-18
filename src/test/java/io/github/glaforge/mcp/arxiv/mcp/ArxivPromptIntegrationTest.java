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
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ArxivPromptIntegrationTest {

    @Inject
    ArxivMcpServer server;

    @Test
    public void testSummarizePaperPrompt() {
        String paperId = "2601.05230"; // Valid paper ID
        PromptMessage promptMessage = server.summarizePaper(paperId);

        assertNotNull(promptMessage);
        String text = promptMessage.content().asText().text();
        assertTrue(text.contains(paperId));
        assertTrue(text.toLowerCase().contains("summarize"));
    }
}

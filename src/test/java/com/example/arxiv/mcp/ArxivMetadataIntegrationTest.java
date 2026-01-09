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

import io.quarkus.test.junit.QuarkusTest;
import io.quarkiverse.mcp.server.TextResourceContents;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ArxivMetadataIntegrationTest {

    @Inject
    ArxivMcpServer server;

    @Test
    public void testGetMetadataJson() {
        String id = "2601.00844v1"; // ID from user report
        TextResourceContents contents = server.getMetadata(id);

        assertNotNull(contents);
        assertNotNull(contents.text());
        System.out.println("Metadata JSON: " + contents.text());

        // rudimentary JSON check
        assertTrue(contents.text().trim().startsWith("{"), "Output should be JSON starting with '{'");
        assertTrue(contents.text().contains("\"id\""), "Output should contain 'id' field");
    }
}

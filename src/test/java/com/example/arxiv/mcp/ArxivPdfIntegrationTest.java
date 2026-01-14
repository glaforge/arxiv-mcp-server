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
import io.quarkiverse.mcp.server.BlobResourceContents;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ArxivPdfIntegrationTest {

    @Inject
    ArxivMcpServer server;

    @Test
    public void testGetPdf() {
        String id = "2601.00844v1"; // ID from user report
        BlobResourceContents contents = server.getPdf(id);

        assertNotNull(contents);
        assertEquals("application/pdf", contents.mimeType());
        // Base64 encoded content should not be empty
        assertNotNull(contents.blob());
        assertTrue(contents.blob().length() > 0);
        System.out.println("PDF Blob length: " + contents.blob().length());
    }
}

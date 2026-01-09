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
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import com.example.arxiv.model.Feed;
import com.example.arxiv.model.SortBy;
import com.example.arxiv.model.SortOrder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class ArxivSearchIntegrationTest {

    @Inject
    ArxivMcpServer server;

    @Test
    public void testSearchPapersWithEmptySort() {
        // Reproducing the parameters from the user report
        String query = "JEPA world model";
        int maxResults = 5;
        SortBy sortBy = null;
        SortOrder sortOrder = null;

        Feed feed = server.searchPapers(query, maxResults, sortBy, sortOrder);

        assertNotNull(feed);
        System.out.println("Found " + feed.entries.size() + " entries");
    }
}

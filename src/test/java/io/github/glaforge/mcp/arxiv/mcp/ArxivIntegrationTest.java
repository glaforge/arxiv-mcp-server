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

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import io.github.glaforge.mcp.arxiv.model.Feed;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
public class ArxivIntegrationTest {

    @Inject
    ArxivMcpServer server;

    @Test
    public void testGetPaperDetails() {
        List<String> ids = Collections.singletonList("2601.05230");
        Feed feed = server.getPaperDetails(ids);
        assertNotNull(feed);
        assertNotNull(feed.entries);
        assertFalse(feed.entries.isEmpty());
        System.out.println("Title: " + feed.entries.get(0).title);
    }
}

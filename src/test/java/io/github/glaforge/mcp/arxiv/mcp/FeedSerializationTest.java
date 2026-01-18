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

import io.github.glaforge.mcp.arxiv.model.Feed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FeedSerializationTest {

    @Test
    public void testXmlDeserializationAndJsonSerialization() throws Exception {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <feed xmlns="http://www.w3.org/2005/Atom">
                  <link href="http://arxiv.org/api/query?search_query=&amp;id_list=2601.05230&amp;start=0&amp;max_results=1" rel="self" type="application/atom+xml"/>
                  <title type="html">ArXiv Query: search_query=&amp;id_list=2601.05230&amp;start=0&amp;max_results=1</title>
                  <id>http://arxiv.org/api/sQyK35Uop+U7oFtaXnv3eTva7qE</id>
                  <updated>2026-01-09T21:28:44Z</updated>
                  <opensearch:totalResults xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/">1</opensearch:totalResults>
                  <opensearch:startIndex xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/">0</opensearch:startIndex>
                  <opensearch:itemsPerPage xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/">1</opensearch:itemsPerPage>
                  <entry>
                    <id>http://arxiv.org/abs/2601.05230v1</id>
                    <updated>2026-01-08T18:00:23Z</updated>
                    <published>2026-01-08T18:00:23Z</published>
                    <title>Learning Latent Dynamics for Autonomous Systems</title>
                    <summary>This paper proposes a new method.</summary>
                    <author>
                      <name>John Doe</name>
                    </author>
                    <link href="http://arxiv.org/abs/2601.05230v1" rel="alternate" type="text/html"/>
                    <link title="pdf" href="http://arxiv.org/pdf/2601.05230v1" rel="related" type="application/pdf"/>
                    <category term="cs.RO" scheme="http://arxiv.org/schemas/atom"/>
                  </entry>
                </feed>
                """;

        ObjectMapper xmlMapper = new XmlMapper();
        Feed feed = xmlMapper.readValue(xml, Feed.class);
        assertNotNull(feed);
        assertNotNull(feed.entries);
        assertNotNull(feed.entries.get(0).title);

        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(feed);
        System.out.println(json);
        assertNotNull(json);
    }
}

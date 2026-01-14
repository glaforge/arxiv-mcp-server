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
package com.example.arxiv.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Entry {
    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public String id;

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public String updated;

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public String published;

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public String title;

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public String summary;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "author", namespace = "http://www.w3.org/2005/Atom")
    public List<Author> authors;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link", namespace = "http://www.w3.org/2005/Atom")
    public List<Link> links;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "category", namespace = "http://www.w3.org/2005/Atom")
    public List<Category> categories;

    @JacksonXmlProperty(localName = "primary_category", namespace = "http://arxiv.org/schemas/atom")
    public Category primaryCategory;

    @JacksonXmlProperty(localName = "comment", namespace = "http://arxiv.org/schemas/atom")
    public String comment;

    @JacksonXmlProperty(localName = "journal_ref", namespace = "http://arxiv.org/schemas/atom")
    public String journalRef;

    @JacksonXmlProperty(localName = "doi", namespace = "http://arxiv.org/schemas/atom")
    public String doi;
}

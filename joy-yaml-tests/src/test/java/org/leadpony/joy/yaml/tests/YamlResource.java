/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.leadpony.joy.yaml.tests;

import java.io.InputStream;

/**
 * YAML documents provided as resources.
 *
 * @author leadpony
 */
public enum YamlResource {
    PERSON_SCHEMA("/org/json-schema/person.schema.yaml"),
    INVOICE("/org/yaml/invoice.yaml"),
    TWO_DOCUMENTS("/org/yaml/two-documents.yaml"),
    PETSTORE("/org/openapis/petstore.yaml"),
    NULL_KEY("null-key.yaml");

    private final String name;

    YamlResource(String name) {
        this.name = name;
    }

    InputStream getYamlAsStream() {
        return getClass().getResourceAsStream(name);
    }

    InputStream getJsonAsStream() {
        String jsonName = this.name.replaceAll("\\.yaml", ".json");
        return getClass().getResourceAsStream(jsonName);
    }
}

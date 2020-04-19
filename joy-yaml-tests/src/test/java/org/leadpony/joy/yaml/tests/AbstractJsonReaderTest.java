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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;

/**
 * @author leadpony
 */
public abstract class AbstractJsonReaderTest {

    private static JsonWriterFactory writerFactory;

    @BeforeAll
    public static void setUpOnce() {
        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
        writerFactory = Json.createWriterFactory(config);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/org/yaml/invoice.yml"
    })
    public void readShouldReadJsonStructureAsExpected(String name) {
        InputStream in = getClass().getResourceAsStream(name);

        JsonStructure actual = null;
        try (JsonReader reader = createReader(in)) {
            actual = reader.read();
        }

        assertThat(actual).isNotNull();

        String jsonName = name.replaceAll("\\.yml", ".json");
        String expected = readExpectedJson(jsonName);

        assertThat(formatAsJson(actual)).isEqualTo(expected);
    }

    protected abstract JsonReader createReader(InputStream in);

    private static String formatAsJson(JsonStructure value) {
        StringWriter target = new StringWriter();
        try (JsonWriter writer = writerFactory.createWriter(target)) {
            writer.write(value);
        }
        return target.toString();
    }

    private String readExpectedJson(String name) {
        StringBuilder builder = new StringBuilder();
        try (InputStream in = getClass().getResourceAsStream(name);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return builder.toString();
    }
}

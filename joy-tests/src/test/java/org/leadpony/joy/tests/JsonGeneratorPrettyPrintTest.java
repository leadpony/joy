/*
 * Copyright 2019-2020 the Joy Authors.
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
package org.leadpony.joy.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonGeneratorFactory;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.leadpony.joy.api.JsonGenerator;
import org.leadpony.jsonp.testsuite.tests.JsonResource;

/**
 * @author leadpony
 */
public class JsonGeneratorPrettyPrintTest {

    public static Stream<Arguments> provideArguments() {
        return Stream.of(1, 2, 4, 8)
            .flatMap(indentSize -> {
                return Stream.of(JsonResource.values())
                    .map(resource -> Arguments.of(indentSize, resource));
            });
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    public void writeShouldGenerateJsonIndentedWithSpaces(int indentSize, JsonResource resource) {
        JsonValue json = readJsonFrom(resource);

        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
        config.put(JsonGenerator.INDENTATION_SIZE, indentSize);

        String actual = generateJson(json, config);
        String expected = resource.getJsonIndentedWithSpacesAsString(indentSize);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(JsonResource.class)
    public void writeShouldGenerateJsonIndentedWithTab(JsonResource resource) {
        JsonValue json = readJsonFrom(resource);

        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
        config.put(JsonGenerator.TAB_INDENTATION, Boolean.TRUE);

        String actual = generateJson(json, config);
        String expected = resource.getJsonIndentedWithTabAsString();

        assertThat(actual).isEqualTo(expected);
    }

    private static JsonValue readJsonFrom(JsonResource resource) {
        try (JsonReader reader = Json.createReader(resource.createReader())) {
            return reader.readValue();
        }
    }

    private static String generateJson(JsonValue json, Map<String, ?> config) {
        JsonGeneratorFactory factory = Json.createGeneratorFactory(config);
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = (JsonGenerator) factory.createGenerator(writer)) {
            generator.write(json);
        }
        return writer.toString();
    }
}

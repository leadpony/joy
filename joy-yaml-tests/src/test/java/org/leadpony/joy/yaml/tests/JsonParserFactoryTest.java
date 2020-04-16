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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import jakarta.json.stream.JsonParser.Event;

/**
 * @author leadpony
 */
public class JsonParserFactoryTest {

    private JsonParserFactory factory;

    @BeforeEach
    public void setUp() {
        factory = Json.createParserFactory(Collections.emptyMap());
    }

    @ParameterizedTest
    @EnumSource(SimpleYamlTestCase.class)
    public void createParserShouldCreateParserFromReader(SimpleYamlTestCase test) {
        StringReader source = new StringReader(test.json);
        var actual = new ArrayList<Event>();

        try (JsonParser parser = factory.createParser(source)) {
            while (parser.hasNext()) {
                actual.add(parser.next());
            }
        }

        assertThat(actual).containsExactly(test.events);
    }
}

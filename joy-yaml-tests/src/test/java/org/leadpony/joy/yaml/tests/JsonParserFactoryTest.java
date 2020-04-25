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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;

/**
 * @author leadpony
 */
public class JsonParserFactoryTest {

    private JsonParserFactory factory;

    @BeforeEach
    public void setUp() {
        factory = Json.createParserFactory(Collections.emptyMap());
    }

    @Nested
    public class InputStreamTest extends AbstractJsonParserTest {

        @Override
        protected JsonParser createParser(InputStream in) {
            return factory.createParser(in);
        }
    }

    @Nested
    public class InputStreamAndCharsetTest extends AbstractJsonParserTest {

        @Override
        protected JsonParser createParser(InputStream in) {
            Charset charset = StandardCharsets.UTF_8;
            return factory.createParser(in, charset);
        }
    }

    @Nested
    public class ReaderTest extends AbstractJsonParserTest {

        @Override
        protected JsonParser createParser(InputStream in) {
            Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            return factory.createParser(reader);
        }

        @Override
        protected JsonParser createParser(String json) {
            Reader reader = new StringReader(json);
            return factory.createParser(reader);
        }
}
}

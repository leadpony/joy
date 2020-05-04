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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;

/**
 * @author leadpony
 */
public class JsonReaderFactoryTest {

    private JsonReaderFactory factory;

    @BeforeEach
    public void setUp() {
        factory = Json.createReaderFactory(Collections.emptyMap());
    }

    @Nested
    public class InputStreamTest extends JsonReaderTest.BaseTest {

        @Override
        protected JsonReader createReader(InputStream in) {
            return factory.createReader(in);
        }
    }

    @Nested
    public class InputStreamAndCharsetTest extends JsonReaderTest.BaseTest {

        @Override
        protected JsonReader createReader(InputStream in) {
            Charset charset = StandardCharsets.UTF_8;
            return factory.createReader(in, charset);
        }
    }

    @Nested
    public class ReaderTest extends JsonReaderTest.BaseTest {

        @Override
        protected JsonReader createReader(InputStream in) {
            var source = new InputStreamReader(in, StandardCharsets.UTF_8);
            return factory.createReader(source);
        }
    }
}

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
package org.leadpony.joy.core;

import static org.leadpony.joy.core.Preconditions.requireNonNull;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;

/**
 * A skeletal implementation of {@link JsonParserFactory}.
 *
 * @author leadpony
 */
public abstract class AbstractJsonParserFactory extends ConfigurableFactory implements JsonParserFactory {

    private static final String[] SUPPORTED_PROPERTIES = {
        org.leadpony.joy.api.JsonParser.VALUE_STREAM
    };

    final boolean valueStream;

    protected AbstractJsonParserFactory(Map<String, ?> properties) {
        this(properties, SUPPORTED_PROPERTIES);
    }

    protected AbstractJsonParserFactory(Map<String, ?> properties, String[] supported) {
        super(properties, supported);
        this.valueStream = containsProperty(org.leadpony.joy.api.JsonParser.VALUE_STREAM);
    }

    @Override
    public JsonParser createParser(JsonObject obj) {
        requireNonNull(obj, "obj");
        return new JsonValueParser(obj);
    }

    @Override
    public JsonParser createParser(JsonArray array) {
        requireNonNull(array, "array");
        return new JsonValueParser(array);
    }

    protected Reader createStreamReader(InputStream in) {
        return StreamReaders.createStreamReader(in);
    }
}

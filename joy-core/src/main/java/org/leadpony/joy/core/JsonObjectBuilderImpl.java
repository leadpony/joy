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
package org.leadpony.joy.core;

import static org.leadpony.joy.core.Requirements.requireNonNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

/**
 * An implementation of {@link JsonObjectBuilder}.
 *
 * @author leadpony
 */
class JsonObjectBuilderImpl implements JsonObjectBuilder {

    private Map<String, JsonValue> properties;

    JsonObjectBuilderImpl() {
    }

    JsonObjectBuilderImpl(JsonObject object) {
        this.properties = new LinkedHashMap<>(object);
    }

    JsonObjectBuilderImpl(Map<String, ?> map) {
        Map<String, JsonValue> properties = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (value instanceof Optional) {
                Optional<?> optional = (Optional<?>) value;
                if (optional.isPresent()) {
                    properties.put(key, JsonValues.valueOf(optional.get()));
                }
            } else {
                properties.put(key, JsonValues.valueOf(value));
            }
        }
        this.properties = properties;
    }

    @Override
    public JsonObjectBuilder add(String name, JsonValue value) {
        requireNonNull(name, "name");
        requireNonNull(value, "value");
        return put(name, value);
    }

    @Override
    public JsonObjectBuilder add(String name, String value) {
        requireNonNull(name, "name");
        requireNonNull(value, "value");
        return put(name, JsonValues.valueOf(value));
    }

    @Override
    public JsonObjectBuilder add(String name, BigInteger value) {
        requireNonNull(name, "name");
        requireNonNull(value, "value");
        return put(name, JsonValues.valueOf(value));
    }

    @Override
    public JsonObjectBuilder add(String name, BigDecimal value) {
        requireNonNull(name, "name");
        requireNonNull(value, "value");
        return put(name, JsonValues.valueOf(value));
    }

    @Override
    public JsonObjectBuilder add(String name, int value) {
        requireNonNull(name, "name");
        return put(name, JsonValues.valueOf(value));
    }

    @Override
    public JsonObjectBuilder add(String name, long value) {
        requireNonNull(name, "name");
        return put(name, JsonValues.valueOf(value));
    }

    @Override
    public JsonObjectBuilder add(String name, double value) {
        requireNonNull(name, "name");
        return put(name, JsonValues.valueOf(value));
    }

    @Override
    public JsonObjectBuilder add(String name, boolean value) {
        requireNonNull(name, "name");
        return put(name, JsonValues.valueOf(value));
    }

    @Override
    public JsonObjectBuilder addNull(String name) {
        requireNonNull(name, "name");
        return put(name, JsonValue.NULL);
    }

    @Override
    public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
        requireNonNull(name, "name");
        requireNonNull(builder, "builder");
        return put(name, builder.build());
    }

    @Override
    public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
        requireNonNull(name, "name");
        requireNonNull(builder, "builder");
        return put(name, builder.build());
    }

    @Override
    public JsonObjectBuilder addAll(JsonObjectBuilder builder) {
        requireNonNull(builder, "builder");
        requireProperties().putAll(builder.build());
        return this;
    }

    @Override
    public JsonObjectBuilder remove(String name) {
        requireNonNull(name, "name");
        requireProperties().remove(name);
        return this;
    }

    @Override
    public JsonObject build() {
        if (properties == null) {
            return JsonValue.EMPTY_JSON_OBJECT;
        }
        JsonObject object = new JsonObjectImpl(Collections.unmodifiableMap(properties));
        properties = null;
        return object;
    }

    private Map<String, JsonValue> requireProperties() {
        if (properties == null) {
            properties = new LinkedHashMap<>();
        }
        return properties;
    }

    private JsonObjectBuilder put(String name, JsonValue value) {
        requireProperties().put(name, value);
        return this;
    }
}

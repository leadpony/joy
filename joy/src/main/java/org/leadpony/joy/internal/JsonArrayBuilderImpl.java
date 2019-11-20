/*
 * Copyright 2019 the Joy Authors.
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
package org.leadpony.joy.internal;

import static org.leadpony.joy.internal.Requirements.requireNonNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * An implementation of {@link JsonArrayBuilder}.
 *
 * @author leadpony
 */
final class JsonArrayBuilderImpl implements JsonArrayBuilder {

    private List<JsonValue> items;

    JsonArrayBuilderImpl() {
    }

    JsonArrayBuilderImpl(JsonArray array) {
        this.items = new ArrayList<>(array);
    }

    JsonArrayBuilderImpl(Collection<?> collection) {
        List<JsonValue> items = new ArrayList<>();
        for (Object value : collection) {
            if (value instanceof Optional) {
                Optional<?> optional = (Optional<?>) value;
                if (optional.isPresent()) {
                    items.add(JsonValues.valueOf(optional.get()));
                }
            } else {
                items.add(JsonValues.valueOf(value));
            }
        }
        this.items = items;
    }

    @Override
    public JsonArrayBuilder add(JsonValue value) {
        requireNonNull(value, "value");
        return append(value);
    }

    @Override
    public JsonArrayBuilder add(String value) {
        requireNonNull(value, "value");
        return append(JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(BigDecimal value) {
        requireNonNull(value, "value");
        return append(JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(BigInteger value) {
        requireNonNull(value, "value");
        return append(JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(int value) {
        return append(JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(long value) {
        return append(JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(double value) {
        return append(JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(boolean value) {
        return append(JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder addNull() {
        return append(JsonValue.NULL);
    }

    @Override
    public JsonArrayBuilder add(JsonObjectBuilder builder) {
        requireNonNull(builder, "builder");
        return append(builder.build());
    }

    @Override
    public JsonArrayBuilder add(JsonArrayBuilder builder) {
        requireNonNull(builder, "builder");
        return append(builder.build());
    }

    @Override
    public JsonArrayBuilder addAll(JsonArrayBuilder builder) {
        requireNonNull(builder, "builder");
        requireItems().addAll(builder.build());
        return this;
    }

    @Override
    public JsonArrayBuilder add(int index, JsonValue value) {
        requireNonNull(value, "value");
        return insert(index, value);
    }

    @Override
    public JsonArrayBuilder add(int index, String value) {
        requireNonNull(value, "value");
        return insert(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(int index, BigDecimal value) {
        requireNonNull(value, "value");
        return insert(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(int index, BigInteger value) {
        requireNonNull(value, "value");
        return insert(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(int index, int value) {
        return insert(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(int index, long value) {
        return insert(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(int index, double value) {
        return insert(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder add(int index, boolean value) {
        return insert(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder addNull(int index) {
        return insert(index, JsonValue.NULL);
    }

    @Override
    public JsonArrayBuilder add(int index, JsonObjectBuilder builder) {
        requireNonNull(builder, "builder");
        return insert(index, builder.build());
    }

    @Override
    public JsonArrayBuilder add(int index, JsonArrayBuilder builder) {
        requireNonNull(builder, "builder");
        return insert(index, builder.build());
    }

    @Override
    public JsonArrayBuilder set(int index, JsonValue value) {
        requireNonNull(value, "value");
        return replace(index, value);
    }

    @Override
    public JsonArrayBuilder set(int index, String value) {
        requireNonNull(value, "value");
        return replace(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder set(int index, BigDecimal value) {
        requireNonNull(value, "value");
        return replace(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder set(int index, BigInteger value) {
        requireNonNull(value, "value");
        return replace(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder set(int index, int value) {
        return replace(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder set(int index, long value) {
        return replace(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder set(int index, double value) {
        return replace(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder set(int index, boolean value) {
        return replace(index, JsonValues.valueOf(value));
    }

    @Override
    public JsonArrayBuilder setNull(int index) {
        return replace(index, JsonValue.NULL);
    }

    @Override
    public JsonArrayBuilder set(int index, JsonObjectBuilder builder) {
        requireNonNull(builder, "builder");
        return replace(index, builder.build());
    }

    @Override
    public JsonArrayBuilder set(int index, JsonArrayBuilder builder) {
        requireNonNull(builder, "builder");
        return replace(index, builder.build());
    }

    @Override
    public JsonArrayBuilder remove(int index) {
        if (items == null) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        items.remove(index);
        return this;
    }

    @Override
    public JsonArray build() {
        if (items == null) {
            return JsonValue.EMPTY_JSON_ARRAY;
        }
        JsonArray array = new JsonArrayImpl(Collections.unmodifiableList(items));
        items = null;
        return array;
    }

    private List<JsonValue> requireItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    private JsonArrayBuilder append(JsonValue value) {
        requireItems().add(value);
        return this;
    }

    private JsonArrayBuilder insert(int index, JsonValue value) {
        requireItems().add(index, value);
        return this;
    }

    private JsonArrayBuilder replace(int index, JsonValue value) {
        requireItems().set(index, value);
        return this;
    }
}

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
import java.util.Collections;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * An implementation of {@link JsonArrayBuilder}.
 *
 * @author leadpony
 */
class JsonArrayBuilderImpl implements JsonArrayBuilder {

    private List<JsonValue> items;

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
    public JsonArray build() {
        if (items == null) {
            return JsonValue.EMPTY_JSON_ARRAY;
        }
        JsonArray array = new JsonArrayImpl(Collections.unmodifiableList(items));
        items = null;
        return array;
    }

    private JsonArrayBuilder append(JsonValue value) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(value);
        return this;
    }
}

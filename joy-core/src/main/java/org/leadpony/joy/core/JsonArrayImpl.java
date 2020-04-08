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

import java.util.AbstractList;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

/**
 * An implementation of {@link JsonArray}.
 *
 * @author leadpony
 */
class JsonArrayImpl extends AbstractList<JsonValue> implements JsonArray {

    private final List<JsonValue> items;

    JsonArrayImpl(List<JsonValue> items) {
        this.items = items;
    }

    /* As a JsonValue */

    @Override
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }

    @Override
    public JsonArray asJsonArray() {
        return this;
    }

    /* As a JsonStructure */

    @Override
    public JsonValue getValue(String jsonPointer) {
        requireNonNull(jsonPointer, "jsonPointer");
        return JsonPointerImpl.parse(jsonPointer).getValue(this);
    }

    /* As a JsonArray */

    @Override
    public JsonObject getJsonObject(int index) {
        return (JsonObject) get(index);
    }

    @Override
    public JsonArray getJsonArray(int index) {
        return (JsonArray) get(index);
    }

    @Override
    public JsonNumber getJsonNumber(int index) {
        return (JsonNumber) get(index);
    }

    @Override
    public JsonString getJsonString(int index) {
        return (JsonString) get(index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
        return (List<T>) items;
    }

    @Override
    public String getString(int index) {
        return getJsonString(index).getString();
    }

    @Override
    public String getString(int index, String defaultValue) {
        JsonValue value = get(index);
        if (value.getValueType() == ValueType.STRING) {
            return ((JsonString) value).getString();
        } else {
            return defaultValue;
        }
    }

    @Override
    public int getInt(int index) {
        return getJsonNumber(index).intValue();
    }

    @Override
    public int getInt(int index, int defaultValue) {
        JsonValue value = get(index);
        if (value.getValueType() == ValueType.NUMBER) {
            return ((JsonNumber) value).intValue();
        } else {
            return defaultValue;
        }
    }

    @Override
    public boolean getBoolean(int index) {
        JsonValue value = get(index);
        if (value == JsonValue.TRUE) {
            return true;
        } else if (value == JsonValue.FALSE) {
            return false;
        }
        throw new ClassCastException();
    }

    @Override
    public boolean getBoolean(int index, boolean defaultValue) {
        JsonValue value = get(index);
        if (value == JsonValue.TRUE) {
            return true;
        } else if (value == JsonValue.FALSE) {
            return false;
        }
        return defaultValue;
    }

    @Override
    public boolean isNull(int index) {
        return get(index) == JsonValue.NULL;
    }

    /* As a List */

    @Override
    public JsonValue get(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public String toString() {
        try (SimpleJsonGenerator g = new SimpleJsonGenerator()) {
            g.write(this);
            return g.toString();
        }
    }
}

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

import java.util.Map;

import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

/**
 * The implementation class of {@link JsonMergePatch}.
 *
 * @author leadpony
 */
final class JsonMergePatchImpl implements JsonMergePatch {

    private final JsonValue value;

    private static final JsonMergePatch EMPTY_OBJECT_PATCH = new JsonMergePatchImpl(JsonValue.EMPTY_JSON_OBJECT);

    static JsonMergePatch of(JsonValue value) {
        if (value == JsonValue.EMPTY_JSON_OBJECT) {
            return EMPTY_OBJECT_PATCH;
        }
        return new JsonMergePatchImpl(value);
    }

    static JsonMergePatch between(JsonValue source, JsonValue target) {
        JsonValue value = diff(source, target);
        return of(value);
    }

    private JsonMergePatchImpl(JsonValue value) {
        this.value = value;
    }

    @Override
    public JsonValue apply(JsonValue target) {
        requireNonNull(target, "target");
        return mergePatch(target, value);
    }

    @Override
    public JsonValue toJsonValue() {
        return value;
    }

    private static JsonValue mergePatch(JsonValue target, JsonValue patch) {
        if (patch.getValueType() == ValueType.OBJECT) {
            if (target.getValueType() == ValueType.OBJECT) {
                return mergePatch((JsonObject) target, (JsonObject) patch);
            } else {
                return removeNull((JsonObject) patch);
            }
        } else {
            return patch;
        }
    }

    private static JsonObject mergePatch(JsonObject target, JsonObject patch) {
        JsonObjectBuilder builder = new JsonObjectBuilderImpl(target);
        for (Map.Entry<String, JsonValue> entry : patch.entrySet()) {
            final String key = entry.getKey();
            JsonValue value = entry.getValue();
            if (value == JsonValue.NULL) {
                if (target.containsKey(key)) {
                    builder.remove(key);
                }
            } else if (target.containsKey(key)) {
                builder.add(key, mergePatch(target.get(key), value));
            } else {
                if (value.getValueType() == ValueType.OBJECT) {
                    value = removeNull((JsonObject) value);
                }
                builder.add(key, value);
            }
        }
        return builder.build();
    }

    /**
     * Removes any property whose value is null from the object.
     *
     * @param object the object to modify.
     * @return modified object.
     */
    private static JsonObject removeNull(JsonObject object) {
        JsonObjectBuilder builder = new JsonObjectBuilderImpl();
        object.forEach((k, v) -> {
            if (v.getValueType() == ValueType.OBJECT) {
                builder.add(k, removeNull((JsonObject) v));
            } else if (v != JsonValue.NULL) {
                builder.add(k, v);
            }
        });
        return builder.build();
    }

    private static JsonValue diff(JsonValue source, JsonValue target) {
        if (source.getValueType() == ValueType.OBJECT
            && target.getValueType() == ValueType.OBJECT) {
            return diff((JsonObject) source, (JsonObject) target);
        } else {
            return target;
        }
    }

    private static JsonObject diff(JsonObject source, JsonObject target) {
        JsonObjectBuilder builder = new JsonObjectBuilderImpl();

        target.forEach((k, v) -> {
            if (source.containsKey(k)) {
                final JsonValue v0 = source.get(k);
                if (!v0.equals(v)) {
                    builder.add(k, diff(v0, v));
                }
            } else {
                builder.add(k, v);
            }
        });

        source.forEach((k, v) -> {
            if (!target.containsKey(k)) {
                builder.addNull(k);
            }
        });

        return builder.build();
    }
}

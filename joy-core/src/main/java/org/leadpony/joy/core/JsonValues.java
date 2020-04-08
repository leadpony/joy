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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

/**
 * @author leadpony
 */
final class JsonValues {

    static JsonNumber valueOf(int value) {
        if (value == 0) {
            return IntJsonNumber.ZERO;
        } else if (value == 1) {
            return IntJsonNumber.ONE;
        } else {
            return new IntJsonNumber(value);
        }
    }

    static JsonNumber valueOf(long value) {
        if (value == 0) {
            return IntJsonNumber.ZERO;
        } else if (value == 1) {
            return IntJsonNumber.ONE;
        } else {
            return new LongJsonNumber(value);
        }
    }

    static JsonNumber valueOf(double value) {
        return new BigDecimalJsonNumber(BigDecimal.valueOf(value));
    }

    static JsonNumber valueOf(BigDecimal value) {
        return new BigDecimalJsonNumber(value);
    }

    static JsonNumber valueOf(BigInteger value) {
        return new BigIntegerJsonNumber(value);
    }

    static JsonString valueOf(String value) {
        if (value.isEmpty()) {
            return JsonStringImpl.EMPTY;
        } else {
            return new JsonStringImpl(value);
        }
    }

    static JsonString valueOf(char[] value, int offset, int count) {
        if (count == 0) {
            return JsonStringImpl.EMPTY;
        } else {
            return new JsonStringImpl(new String(value, offset, count));
        }
    }

    static JsonValue valueOf(boolean value) {
        return value ? JsonValue.TRUE : JsonValue.FALSE;
    }

    static JsonValue valueOf(Object value) {
        if (value == null) {
            return JsonValue.NULL;
        } else if (value instanceof JsonValue) {
            return (JsonValue) value;
        } else if (value instanceof Boolean) {
            return valueOf(((Boolean) value).booleanValue());
        } else if (value instanceof Integer) {
            return valueOf(((Integer) value).intValue());
        } else if (value instanceof Long) {
            return valueOf(((Long) value).longValue());
        } else if (value instanceof Float) {
            return valueOf(((Float) value).doubleValue());
        } else if (value instanceof Double) {
            return valueOf(((Double) value).doubleValue());
        } else if (value instanceof BigInteger) {
            return valueOf((BigInteger) value);
        } else if (value instanceof BigDecimal) {
            return valueOf((BigDecimal) value);
        } else if (value instanceof String) {
            return valueOf((String) value);
        } else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            return new JsonArrayBuilderImpl(collection).build();
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>) value;
            return new JsonObjectBuilderImpl(map).build();
        } else if (value instanceof JsonArrayBuilder) {
            return ((JsonArrayBuilder) value).build();
        } else if (value instanceof JsonObjectBuilder) {
            return ((JsonObjectBuilder) value).build();
        }
        throw new IllegalArgumentException(
            Message.JSON_VALUE_UNSUPPORTED_TYPE.with(value.getClass().getName()));
    }

    static boolean isStructure(JsonValue value) {
        ValueType type = value.getValueType();
        return type == ValueType.ARRAY || type == ValueType.OBJECT;
    }

    private JsonValues() {
    }
}

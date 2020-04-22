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
 * A utility class operating on instances of {@link JsonValue}.
 *
 * @author leadpony
 */
public final class JsonValues {

    /**
     * Creates a JSON number from an integer.
     *
     * @param value the original value from which JSON value will be created.
     * @return the newly created JSON number.
     */
    public static JsonNumber valueOf(int value) {
        if (value == 0) {
            return IntJsonNumber.ZERO;
        } else if (value == 1) {
            return IntJsonNumber.ONE;
        } else {
            return new IntJsonNumber(value);
        }
    }

    /**
     * Creates a JSON number from a long integer.
     *
     * @param value the original value from which JSON value will be created.
     * @return the newly created JSON number.
     */
    public static JsonNumber valueOf(long value) {
        if (value == 0) {
            return IntJsonNumber.ZERO;
        } else if (value == 1) {
            return IntJsonNumber.ONE;
        } else {
            return new LongJsonNumber(value);
        }
    }

    /**
     * Creates a JSON number from a floating point number.
     *
     * @param value the original value from which JSON value will be created.
     * @return the newly created JSON number.
     */
    public static JsonNumber valueOf(double value) {
        return new BigDecimalJsonNumber(BigDecimal.valueOf(value));
    }

    /**
     * Creates a JSON number from a BigDecimal.
     *
     * @param value the original value from which JSON value will be created.
     * @return the newly created JSON number.
     */
    public static JsonNumber valueOf(BigDecimal value) {
        return new BigDecimalJsonNumber(value);
    }

    /**
     * Creates a JSON number from a BigInteger.
     *
     * @param value the original value from which JSON value will be created.
     * @return the newly created JSON number.
     */
    public static JsonNumber valueOf(BigInteger value) {
        return new BigIntegerJsonNumber(value);
    }

    /**
     * Creates a JSON string from a Java string.
     *
     * @param value the original value from which JSON value will be created.
     * @return the newly created JSON string.
     */
    public static JsonString valueOf(String value) {
        if (value.isEmpty()) {
            return JsonStringImpl.EMPTY;
        } else {
            return new JsonStringImpl(value);
        }
    }

    /**
     * Creates a JSON string from an array of characters.
     *
     * @param value  the array of characters.
     * @param offset the offset in the array.
     * @param count  the number of characters.
     * @return the newly created JSON string.
     */
    public static JsonString valueOf(char[] value, int offset, int count) {
        if (count == 0) {
            return JsonStringImpl.EMPTY;
        } else {
            return new JsonStringImpl(new String(value, offset, count));
        }
    }

    /**
     * Creates a JSON value from a boolean value.
     *
     * @param value the original value from which JSON value will be created.
     * @return the newly created JSON value.
     */
    public static JsonValue valueOf(boolean value) {
        return value ? JsonValue.TRUE : JsonValue.FALSE;
    }

    /**
     * Creates a JSON value from an object.
     *
     * @param value the original value from which JSON value will be created.
     * @return the newly created JSON value.
     */
    public static JsonValue valueOf(Object value) {
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
        throw new IllegalArgumentException(Message.thatObjectCannotBeConvertedToJsonValue(value));
    }

    /**
     * Tests whether the specified JSON value is a JSON structure or not. A JSON
     * structure is a JSON array or a JSON object.
     *
     * @param value the JSON value to be tested.
     * @return {@code true} if the specified value if a JSON structure,
     *         {@code false} otherwise.
     */
    public static boolean isStructure(JsonValue value) {
        ValueType type = value.getValueType();
        return type == ValueType.ARRAY || type == ValueType.OBJECT;
    }

    private JsonValues() {
    }
}

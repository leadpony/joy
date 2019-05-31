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

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

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

    static boolean isStructure(JsonValue value) {
        ValueType type = value.getValueType();
        return type == ValueType.ARRAY || type == ValueType.OBJECT;
    }

    private JsonValues() {
    }
}

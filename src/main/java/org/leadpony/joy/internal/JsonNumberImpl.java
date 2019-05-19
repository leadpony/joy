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

import java.math.BigInteger;

import javax.json.JsonNumber;

/**
 * The implementation of {@link JsonNumber}.
 *
 * @author leadpony
 */
abstract class JsonNumberImpl implements JsonNumber {

    @Override
    public ValueType getValueType() {
        return ValueType.NUMBER;
    }

    @Override
    public boolean isIntegral() {
        return bigDecimalValue().scale() == 0;
    }

    @Override
    public int intValue() {
        return bigDecimalValue().intValue();
    }

    @Override
    public int intValueExact() {
        return bigDecimalValue().intValueExact();
    }

    @Override
    public long longValue() {
        return bigDecimalValue().longValue();
    }

    @Override
    public long longValueExact() {
        return bigDecimalValue().longValueExact();
    }

    public BigInteger bigIntegerValue() {
        return bigDecimalValue().toBigInteger();
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return bigDecimalValue().toBigIntegerExact();
    }

    @Override
    public double doubleValue() {
        return bigDecimalValue().doubleValue();
    }

    @Override
    public Number numberValue() {
        return bigDecimalValue();
    }

    @Override
    public String toString() {
        return bigDecimalValue().toString();
    }

    @Override
    public int hashCode() {
        return bigDecimalValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JsonNumber)) {
            return false;
        }
        JsonNumber other = (JsonNumber) obj;
        return bigDecimalValue().equals(other.bigDecimalValue());
    }
}

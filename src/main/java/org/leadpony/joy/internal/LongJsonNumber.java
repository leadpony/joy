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

/**
 * A JSON number which holds a long integer.
 *
 * @author leadpony
 */
class LongJsonNumber extends JsonNumberImpl {

    private final long value;

    LongJsonNumber(long value) {
        this.value = value;
    }

    @Override
    public boolean isIntegral() {
        return true;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public int intValueExact() {
        return Math.toIntExact(value);
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public long longValueExact() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return BigDecimal.valueOf(value);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}

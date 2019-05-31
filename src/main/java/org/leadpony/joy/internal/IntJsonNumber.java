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

import javax.json.JsonNumber;

/**
 * A JSON number which holds an integer.
 *
 * @author leadpony
 */
class IntJsonNumber extends JsonNumberImpl {

    private final int value;

    IntJsonNumber(int value) {
        this.value = value;
    }

    @Override
    public boolean isIntegral() {
        return true;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public int intValueExact() {
        return value;
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
        return Integer.toString(value);
    }

    static final JsonNumber ZERO = new JsonNumberImpl() {

        @Override
        public BigDecimal bigDecimalValue() {
            return BigDecimal.ZERO;
        }

        @Override
        public boolean isIntegral() {
            return true;
        }

        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public int intValueExact() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }

        @Override
        public long longValueExact() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0.0;
        }

        @Override
        public String toString() {
            return "0";
        }
    };

    static final JsonNumber ONE = new JsonNumberImpl() {

        @Override
        public BigDecimal bigDecimalValue() {
            return BigDecimal.ONE;
        }

        @Override
        public boolean isIntegral() {
            return true;
        }

        @Override
        public int intValue() {
            return 1;
        }

        @Override
        public int intValueExact() {
            return 1;
        }

        @Override
        public long longValue() {
            return 1;
        }

        @Override
        public long longValueExact() {
            return 1;
        }

        @Override
        public double doubleValue() {
            return 1.0;
        }

        @Override
        public String toString() {
            return "1";
        }
    };
}

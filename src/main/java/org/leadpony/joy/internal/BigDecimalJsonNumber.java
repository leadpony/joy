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

class BigDecimalJsonNumber extends JsonNumberImpl {

    private final BigDecimal value;

    BigDecimalJsonNumber(BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return value;
    }
}

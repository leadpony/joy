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

import javax.json.JsonString;

/**
 * @author leadpony
 */
class JsonStringImpl implements JsonString {

    static final JsonString EMPTY = new JsonStringImpl("") {
        @Override
        public String toString() {
            return "\"\"";
        }
    };

    private final String value;

    JsonStringImpl(String value) {
        this.value = value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public CharSequence getChars() {
        return value;
    }

    @Override
    public int hashCode() {
        return getString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof JsonString)) {
            return false;
        }
        JsonString other = (JsonString) obj;
        return getString().equals(other.getString());
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("\"");
        String value = this.value;
        final int length = value.length();
        int lastStart = 0;
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c < 0x20 || c == '"' || c == '\\') {
                if (lastStart < i) {
                    b.append(value, lastStart, i);
                }
                switch (c) {
                case '"': // quotation mark
                    b.append('\\').append('"');
                    break;
                case '\\': // reverse solidus
                    b.append('\\').append('\\');
                    break;
                default:
                    b.append(JsonChar.escape(c));
                    break;
                }
                lastStart = i + 1;
            }
        }
        if (lastStart < length) {
            b.append(value, lastStart, length);
        }
        return b.append('"').toString();
    }
}

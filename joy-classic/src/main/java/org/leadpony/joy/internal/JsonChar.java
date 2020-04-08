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

import java.util.AbstractSet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Special characters in JSON.
 *
 * @author leadpony
 */
enum JsonChar {
    CLOSING_CURLY_BRACKET('}'),
    CLOSING_SQURE_BRACKET(']'),
    COLON(':'),
    COMMA(',');

    @SuppressWarnings("unused")
    private final char c;
    private final String s;

    JsonChar(char c) {
        this.c = c;
        this.s = "'" + Character.toString(c) + "'";
    }

    @Override
    public String toString() {
        return s;
    }

    private static final String[] ESCAPED = {
        "\\u0000",
        "\\u0001",
        "\\u0002",
        "\\u0003",
        "\\u0004",
        "\\u0005",
        "\\u0006",
        "\\u0007",
        "\\b", // backspace
        "\\t", // tab
        "\\n", // line feed
        "\\u000b",
        "\\f", // form feed
        "\\r", // carriage return
        "\\u000e",
        "\\u000f",
        "\\u0010",
        "\\u0011",
        "\\u0012",
        "\\u0013",
        "\\u0014",
        "\\u0015",
        "\\u0016",
        "\\u0017",
        "\\u0018",
        "\\u0019",
        "\\u001a",
        "\\u001b",
        "\\u001c",
        "\\u001d",
        "\\u001e",
        "\\u001f"
    };

    static String escape(char c) {
        return ESCAPED[c];
    }

    static String toString(char c) {
        StringBuilder b = new StringBuilder("'");
        if (c < 0x20) {
            b.append(escape(c));
        } else {
            b.append(c);
        }
        return b.append("'").toString();
    }

    static Set<JsonChar> of(JsonChar... chars) {
        return new JsonCharSet(chars);
    }

    /**
     * A set of JSON characters.
     *
     * @author leadpony
     */
    private static class JsonCharSet extends AbstractSet<JsonChar> {

        private final EnumSet<JsonChar> chars;
        private final String stringified;

        JsonCharSet(JsonChar... chars) {
            this.chars = EnumSet.noneOf(JsonChar.class);
            for (JsonChar c : chars) {
                this.chars.add(c);
            }
            this.stringified = this.chars.stream()
                .map(JsonChar::toString)
                .collect(Collectors.joining(",", "[", "]"));
        }

        @Override
        public Iterator<JsonChar> iterator() {
            return chars.iterator();
        }

        @Override
        public int size() {
            return chars.size();
        }

        @Override
        public String toString() {
            return stringified;
        }
    }
}

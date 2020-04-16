/*
 * Copyright 2020 the original author or authors.
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

package org.leadpony.joy.yaml;

import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.events.ScalarEvent;

import jakarta.json.stream.JsonParser;

/**
 * YAML event types.
 *
 * @author leadpony
 */
enum EventType {
    TRUE(JsonParser.Event.VALUE_TRUE),
    FALSE(JsonParser.Event.VALUE_FALSE),
    NULL(JsonParser.Event.VALUE_NULL),
    STRING(JsonParser.Event.VALUE_STRING),
    NUMBER(JsonParser.Event.VALUE_NUMBER),
    INTEGER(JsonParser.Event.VALUE_NUMBER),
    KEY_NAME(JsonParser.Event.KEY_NAME),
    SEQUENCE_START(JsonParser.Event.START_ARRAY),
    SEQUENCE_END(JsonParser.Event.END_ARRAY),
    MAPPING_START(JsonParser.Event.START_OBJECT),
    MAPPING_END(JsonParser.Event.END_OBJECT);

    private static final EventType DEFAULT_TYPE = EventType.STRING;

    private final JsonParser.Event jsonEvent;

    EventType(JsonParser.Event jsonEvent) {
        this.jsonEvent = jsonEvent;
    }

    /**
     * Returns the corresponding JSON parser event.
     *
     * @return the JSON parser event for this type.
     */
    final JsonParser.Event toJsonEvent() {
        return jsonEvent;
    }

    /**
     * Maps a scalar event to one of event types.
     *
     * @param event the scalar event to map.
     * @return the mapped event type.
     */
    static EventType of(ScalarEvent event) {

        if (isQuoted(event)) {
            return EventType.STRING;
        }

        String value = event.getValue();
        if (value.isEmpty()) {
            return EventType.STRING;
        } else {
            switch (value.charAt(0)) {
            case 't':
                return value.equals("true") ? EventType.TRUE : DEFAULT_TYPE;
            case 'f':
                return value.equals("false") ? EventType.FALSE : DEFAULT_TYPE;
            case 'n':
                return value.equals("null") ? EventType.NULL : DEFAULT_TYPE;
            case '-':
                return testNegativeNumber(value);
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return testPositiveNumber(value);
            default:
                return DEFAULT_TYPE;
            }
        }
    }

    private static boolean isQuoted(ScalarEvent event) {
        ScalarStyle style = event.getScalarStyle();
        return style == ScalarStyle.SINGLE_QUOTED || style == ScalarStyle.DOUBLE_QUOTED;
    }

    private static EventType testPositiveNumber(String value) {
        return testNumber(value, 0);
    }

    private static EventType testNegativeNumber(String value) {
        if (value.length() >= 2) {
            if (isDigit(value.charAt(1))) {
                return testNumber(value, 1);
            }
        }
        return DEFAULT_TYPE;
    }

    private static EventType testNumber(String value, int index) {
        final int length = value.length();
        char c = value.charAt(index++);

        if (c == '0') {
            if (index < length) {
                c = value.charAt(index++);
                if (isDigit(c)) {
                    return DEFAULT_TYPE;
                }
            } else {
                return EventType.INTEGER;
            }
        } else { // c is from 1 to 9
            do {
                if (index < length) {
                    c = value.charAt(index++);
                } else {
                    return EventType.INTEGER;
                }
            } while (isDigit(c));
        }

        // not a digit

        if (c == '.') {
            do {
                if (index < length) {
                    c = value.charAt(index++);
                } else {
                    return EventType.NUMBER;
                }
             } while (isDigit(c));
        }

        if (c == 'e' || c == 'E') {
            if (index < length) {
                c = value.charAt(index++);
                if (c == '-' || c == '+') {
                    if (index < length) {
                        c = value.charAt(index++);
                    } else {
                        return DEFAULT_TYPE;
                    }
                }
                int exponent = 0;
                while (isDigit(c)) {
                    ++exponent;
                    if (index < length) {
                        c = value.charAt(index++);
                    } else {
                        return (exponent > 0) ? EventType.NUMBER : DEFAULT_TYPE;
                    }
                }
            }
        }

        return DEFAULT_TYPE;
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }
}

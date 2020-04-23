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

package org.leadpony.joy.yaml.tests;

import jakarta.json.stream.JsonParser.Event;

/**
 * @author leadpony
 */
public enum SimpleYamlTestCase {
    EMPTY(""),
    TRUE("true", Event.VALUE_TRUE),
    FALSE("false", Event.VALUE_FALSE),
    NULL("null", Event.VALUE_NULL),

    INTEGER_ZERO("0", Event.VALUE_NUMBER),
    INTEGER_NEGATIVE_ZERO("-0", Event.VALUE_NUMBER),
    INTEGER_POSITIVE("3", Event.VALUE_NUMBER),
    INTEGER_NEGATIVE("-19", Event.VALUE_NUMBER),

    NUMBER_ZERO("0.", Event.VALUE_NUMBER),
    NUMBER_NEGATIVE_ZERO("-0.0", Event.VALUE_NUMBER),
    NUMBER_SCIENTIFIC("12e03", Event.VALUE_NUMBER),
    NUMBER_NEGATIVE_SCIENTIFIC("-2E+05", Event.VALUE_NUMBER),

    SEQUENCE_EMPTY("[]",
            Event.START_ARRAY,
            Event.END_ARRAY),
    SEQUENCE_SINGLE_ITEM("[1]",
            Event.START_ARRAY,
            Event.VALUE_NUMBER,
            Event.END_ARRAY),
    SEQUENCE_MULTIPLE_ITEMS("[1, 2]",
            Event.START_ARRAY,
            Event.VALUE_NUMBER,
            Event.VALUE_NUMBER,
            Event.END_ARRAY),
    SEQUENCE_OF_MAPPINGS("[{}, {}]",
            Event.START_ARRAY,
            Event.START_OBJECT,
            Event.END_OBJECT,
            Event.START_OBJECT,
            Event.END_OBJECT,
            Event.END_ARRAY),
    SEQUENCE_MISSING_ITEM("-",
            Event.START_ARRAY,
            Event.VALUE_NULL,
            Event.END_ARRAY),

    MAPPING_EMPTY("{}",
            Event.START_OBJECT,
            Event.END_OBJECT),
    MAPPING_SINGLE_PROPERTY("{a: 1}",
            Event.START_OBJECT,
            Event.KEY_NAME,
            Event.VALUE_NUMBER,
            Event.END_OBJECT
            ),
    MAPPING_MULTIPLE_PROPERTIES("{a: 1, b: 2}",
            Event.START_OBJECT,
            Event.KEY_NAME,
            Event.VALUE_NUMBER,
            Event.KEY_NAME,
            Event.VALUE_NUMBER,
            Event.END_OBJECT
            ),
    MAPPING_MISSING_PROPERTY_VALUE("{a:}",
            Event.START_OBJECT,
            Event.KEY_NAME,
            Event.VALUE_NULL,
            Event.END_OBJECT
            ),
    MAPPING_PROPERTY_KEY_ONLY("{a}",
            Event.START_OBJECT,
            Event.KEY_NAME,
            Event.VALUE_NULL,
            Event.END_OBJECT
            );

    final String json;
    final Event events[];

    SimpleYamlTestCase(String json, Event... events) {
        this.json = json;
        this.events = events;
    }
}

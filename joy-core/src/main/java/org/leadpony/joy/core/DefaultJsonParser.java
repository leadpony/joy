/*
 * Copyright 2018-2020 the original author or authors.
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

import java.io.IOException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;

import jakarta.json.JsonException;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;

/**
 * An extended {@link JsonParser} interface with default implementation.
 *
 * @author leadpony
 */
public interface DefaultJsonParser extends JsonParser {

    @Override
    default void close() {
    }

    @Override
    default Stream<JsonValue> getArrayStream() {
        if (getCurrentEvent() != Event.START_ARRAY) {
            throw newIllegalStateException("getArrayStream()");
        }
        return JsonStreams.arrayStream(this);
    }

    @Override
    default Stream<Entry<String, JsonValue>> getObjectStream() {
        if (getCurrentEvent() != Event.START_OBJECT) {
            throw newIllegalStateException("getObjectStream()");
        }
        return JsonStreams.objectStream(this);
    }

    @Override
    default Stream<JsonValue> getValueStream() {
        if (isInCollection()) {
            throw newIllegalStateException("getValueStream()");
        }
        return JsonStreams.valueStream(this);
    }

    @Override
    default void skipArray() {
        if (!isInArray()) {
            return;
        }
        int depth = 1;
        while (hasNext()) {
            Event event = next();
            if (event == Event.END_ARRAY) {
                if (--depth == 0) {
                    break;
                }
            } else if (event == Event.START_ARRAY) {
                ++depth;
            }
        }
    }

    @Override
    default void skipObject() {
        if (!isInObject()) {
            return;
        }
        int depth = 1;
        while (hasNext()) {
            Event event = next();
            if (event == Event.END_OBJECT) {
                if (--depth == 0) {
                    break;
                }
            } else if (event == Event.START_OBJECT) {
                ++depth;
            }
        }
    }

    /* */

    Event getCurrentEvent();

    default boolean isInCollection() {
        return isInArray() || isInObject();
    }

    boolean isInArray();

    boolean isInObject();

    /* provides parsing exceptions */

    default JsonException newJsonException(Message message, IOException e) {
        return new JsonException(message.toString(), e);
    }

    default IllegalStateException newIllegalStateException(String method) {
        String message = Message.PARSER_ILLEGAL_STATE.with(method, getCurrentEvent());
        return new IllegalStateException(message);
    }

    default JsonParsingException newUnexpectedEndException(Set<Event> expected) {
        JsonLocation location = getLocation();
        String message = Message.PARSER_UNEXPECTED_EOI_FOR_EVENTS.with(location, expected);
        return new JsonParsingException(message, location);
    }
}

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

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;

/**
 * A skeletal implementation of {@link JsonParser} providing default implementation.
 *
 * @author leadpony
 */
public abstract class AbstractJsonParser implements JsonParser {

    public static final String MAX_INT_AS_STRING = "2147483647";
    public static final String MIN_INT_AS_STRING = "-2147483648";

    public static final String MAX_LONG_AS_STRING = "9223372036854775807";
    public static final String MIN_LONG_AS_STRING = "-9223372036854775808";

    /**
     * Constructs this parser.
     */
    protected AbstractJsonParser() {
    }

    @Override
    public JsonObject getObject() {
        if (getCurrentEvent() != Event.START_OBJECT) {
            throw newIllegalStateException("getObject()");
        }
        return getValueAsObject();
    }

    @Override
    public JsonValue getValue() {
        Event event = getCurrentEvent();
        if (event == null) {
            throw newIllegalStateException("getValue()");
        }
        switch (event) {
        case VALUE_TRUE:
            return JsonValue.TRUE;
        case VALUE_FALSE:
            return JsonValue.FALSE;
        case VALUE_NULL:
            return JsonValue.NULL;
        case VALUE_STRING:
        case KEY_NAME:
            return getValueAsString();
        case VALUE_NUMBER:
            return getValueAsNumber();
        case START_ARRAY:
            return getValueAsArray();
        case START_OBJECT:
            return getValueAsObject();
        case END_ARRAY:
        case END_OBJECT:
        default:
            throw newIllegalStateException("getValue()");
        }
    }
    @Override
    public JsonArray getArray() {
        if (getCurrentEvent() != Event.START_ARRAY) {
            throw newIllegalStateException("getArray()");
        }
        return getValueAsArray();
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        if (getCurrentEvent() != Event.START_ARRAY) {
            throw newIllegalStateException("getArrayStream()");
        }
        return JsonStreams.arrayStream(this);
    }

    @Override
    public Stream<Entry<String, JsonValue>> getObjectStream() {
        if (getCurrentEvent() != Event.START_OBJECT) {
            throw newIllegalStateException("getObjectStream()");
        }
        return JsonStreams.objectStream(this);
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        if (isInCollection()) {
            throw newIllegalStateException("getValueStream()");
        }
        return JsonStreams.valueStream(this);
    }

    @Override
    public void skipArray() {
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
    public void skipObject() {
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

    @Override
    public void close() {
    }

    /* */

    protected abstract Event getCurrentEvent();

    protected boolean isInCollection() {
        return isInArray() || isInObject();
    }

    protected abstract boolean isInArray();

    protected abstract boolean isInObject();

    protected JsonString getValueAsString() {
        throw new UnsupportedOperationException("Not implemented");
    }

    protected JsonNumber getValueAsNumber() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the current value as a JSON array.
     *
     * @return the JSON array.
     */
    protected final JsonArray getValueAsArray() {
        JsonArrayBuilder builder = new JsonArrayBuilderImpl();
        while (hasNext()) {
            Event event = next();
            if (event == Event.END_ARRAY) {
                return builder.build();
            }
            builder.add(getValue());
        }
        throw newUnexpectedEndException(ParserEventSet.VALUES_OR_END_ARRAY);
    }

    /**
     * Returns the current value as a JSON object.
     *
     * @return the JSON object.
     */
    protected final JsonObject getValueAsObject() {
        JsonObjectBuilder builder = new JsonObjectBuilderImpl();
        while (hasNext()) {
            Event event = next();
            if (event == Event.END_OBJECT) {
                return builder.build();
            }
            String keyName = getString();
            if (hasNext()) {
                next();
                builder.add(keyName, getValue());
            } else {
                throw newUnexpectedEndException(':');
            }
        }
        throw newUnexpectedEndException(ParserEventSet.KEY_NAME_OR_END_OBJECT);
    }

    /* provides parsing exceptions */

    protected JsonException newJsonException(Message message, IOException e) {
        return new JsonException(message.toString(), e);
    }

    protected IllegalStateException newIllegalStateException(String method) {
        String message = Message.PARSER_ILLEGAL_STATE.with(method, getCurrentEvent());
        return new IllegalStateException(message);
    }

    protected JsonParsingException newUnexpectedEndException(Set<Event> expected) {
        JsonLocation location = getLocation();
        String message = Message.PARSER_UNEXPECTED_EOI_FOR_EVENTS.with(location, expected);
        return new JsonParsingException(message, location);
    }

    protected JsonParsingException newUnexpectedEndException(Object expected) {
        JsonLocation location = getLocation();
        String message = Message.PARSER_UNEXPECTED_EOI_FOR_CHAR.with(location, expected);
        return new JsonParsingException(message, location);
    }
}

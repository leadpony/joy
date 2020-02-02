/*
 * Copyright 2019-2020 the Joy Authors.
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

import java.util.Set;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import jakarta.json.stream.JsonParsingException;

/**
 * An implementation of {@link JsonReader}.
 *
 * @author leadpony
 */
class JsonReaderImpl implements JsonReader {

    private final JsonParser parser;
    private boolean alreadyRead;
    private boolean alreadyClosed;

    JsonReaderImpl(JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public JsonStructure read() {
        checkState();
        alreadyRead = true;
        if (parser.hasNext()) {
            Event event = parser.next();
            switch (event) {
            case START_ARRAY:
                return parser.getArray();
            case START_OBJECT:
                return parser.getObject();
            default:
                break;
            }
        }
        throw newUnexpectedEndException(ParserEventSet.START_STRUCTURE);
    }

    @Override
    public JsonArray readArray() {
        checkState();
        alreadyRead = true;
        if (parser.hasNext()) {
            Event event = parser.next();
            if (event == Event.START_ARRAY) {
                return parser.getArray();
            }
        }
        throw newUnexpectedEndException(ParserEventSet.START_ARRAY);
    }

    @Override
    public JsonObject readObject() {
        checkState();
        alreadyRead = true;
        if (parser.hasNext()) {
            Event event = parser.next();
            if (event == Event.START_OBJECT) {
                return parser.getObject();
            }
        }
        throw newUnexpectedEndException(ParserEventSet.START_OBJECT);
    }

    @Override
    public JsonValue readValue() {
        checkState();
        alreadyRead = true;
        if (parser.hasNext()) {
            parser.next();
            return parser.getValue();
        }
        throw newUnexpectedEndException(
                ParserEventSet.VALUES);
    }

    @Override
    public void close() {
        if (alreadyClosed) {
            return;
        }
        alreadyClosed = true;
        parser.close();
    }

    private void checkState() {
        if (alreadyRead || alreadyClosed) {
            throw new IllegalStateException();
        }
    }

    private JsonParsingException newUnexpectedEndException(Set<Event> expected) {
        JsonLocation location = parser.getLocation();
        return new JsonParsingException(
                Message.PARSER_UNEXPECTED_EOI_FOR_EVENTS.with(location, expected),
                location);
    }
}

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

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.json.JsonException;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;

/**
 * A skeletal implementation of {@link JsonParser}.
 *
 * @author leadpony
 */
abstract class AbstractJsonParser implements JsonParser {

    private Event currentEvent;

    protected final Event getCurrentEvent() {
        return currentEvent;
    }

    protected final void setCurrentEvent(Event event) {
        this.currentEvent = event;
    }

    /* As a JsonParser */

    @Override
    public void close() {
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        if (getCurrentEvent() != Event.START_ARRAY) {
            throw newIllegalStateException("getArrayStream()");
        }
        return StreamSupport.stream(new JsonArraySpliterator(), false);
    }

    @Override
    public Stream<Entry<String, JsonValue>> getObjectStream() {
        if (getCurrentEvent() != Event.START_OBJECT) {
            throw newIllegalStateException("getObjectStream()");
        }
        return StreamSupport.stream(new JsonObjectSpliterator(), false);
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        if (isInCollection()) {
            throw newIllegalStateException("getValueStream()");
        }
        return StreamSupport.stream(new JsonValueSpliterator(), false);
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

    /* As a AbstractJsonParser */

    abstract boolean isInCollection();

    abstract boolean isInArray();

    abstract boolean isInObject();

    static JsonException newJsonException(Message message, IOException e) {
        return new JsonException(message.toString(), e);
    }

    IllegalStateException newIllegalStateException(String method) {
        String message = Message.PARSER_ILLEGAL_STATE.with(method, getCurrentEvent());
        return new IllegalStateException(message);
    }

    JsonParsingException newUnexpectedEndException(Set<Event> expected) {
        JsonLocation location = getLocation();
        String message = Message.PARSER_UNEXPECTED_EOI_FOR_EVENTS.with(location, expected);
        return new JsonParsingException(message, location);
    }

    /**
     * A skeletal implementation of {@link Spliterator}.
     * @author leadpony
     *
     * @param <T>
     */
    abstract static class AbstractSpliterator<T> extends Spliterators.AbstractSpliterator<T> {

        protected AbstractSpliterator() {
            super(Long.MAX_VALUE, Spliterator.ORDERED);
        }

        @Override
        public Spliterator<T> trySplit() {
            // this spliterator cannot be split
            return null;
        }
    }

    /**
     * A spliterator for JSON array.
     *
     * @author leadpony
     */
    class JsonArraySpliterator extends AbstractSpliterator<JsonValue> {

        @Override
        public boolean tryAdvance(Consumer<? super JsonValue> action) {
            if (hasNext() && next() != Event.END_ARRAY) {
                action.accept(getValue());
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * A spliterator for JSON object.
     *
     * @author leadpony
     */
    class JsonObjectSpliterator extends AbstractSpliterator<Map.Entry<String, JsonValue>> {

        @Override
        public boolean tryAdvance(Consumer<? super Map.Entry<String, JsonValue>> action) {
            if (!hasNext()) {
                return false;
            }
            Event event = next();
            if (event == Event.END_OBJECT) {
                return false;
            } else if (event == Event.KEY_NAME) {
                String key = getString();
                if (hasNext()) {
                    next();
                    JsonValue value = getValue();
                    action.accept(new AbstractMap.SimpleImmutableEntry<>(key, value));
                    return true;
                } else {
                    throw newUnexpectedEndException(ParserEventSet.VALUES);
                }
            } else {
                // This never happen.
                throw new IllegalStateException();
            }
        }
    }

    /**
     * A spliterator for JSON value.
     *
     * @author leadpony
     */
    class JsonValueSpliterator extends AbstractSpliterator<JsonValue> {

        @Override
        public boolean tryAdvance(Consumer<? super JsonValue> action) {
            if (hasNext()) {
                next();
                action.accept(getValue());
                return true;
            } else {
                return false;
            }
        }
    }
}

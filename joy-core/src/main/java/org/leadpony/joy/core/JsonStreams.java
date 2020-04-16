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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;
import jakarta.json.stream.JsonParser.Event;

/**
 * A helper class for generating streams of JSON values.
 *
 * @author leadpony
 */
final class JsonStreams {

    static Stream<JsonValue> arrayStream(JsonParser parser) {
        Spliterator<JsonValue> spliterator = new AbstractSpliterator<JsonValue>() {
            @Override
            public boolean tryAdvance(Consumer<? super JsonValue> action) {
                if (parser.hasNext() && parser.next() != Event.END_ARRAY) {
                    action.accept(parser.getValue());
                    return true;
                } else {
                    return false;
                }
            }
        };
        return StreamSupport.stream(spliterator, false);
    }

    static Stream<Entry<String, JsonValue>> objectStream(JsonParser parser) {
        Spliterator<Entry<String, JsonValue>> spliterator = new AbstractSpliterator<Entry<String, JsonValue>>() {

            @Override
            public boolean tryAdvance(Consumer<? super Map.Entry<String, JsonValue>> action) {
                if (!parser.hasNext()) {
                    return false;
                }
                Event event = parser.next();
                if (event == Event.END_OBJECT) {
                    return false;
                } else if (event == Event.KEY_NAME) {
                    String key = parser.getString();
                    if (parser.hasNext()) {
                        parser.next();
                        JsonValue value = parser.getValue();
                        action.accept(new AbstractMap.SimpleImmutableEntry<>(key, value));
                        return true;
                    } else {
                        JsonLocation location = parser.getLocation();
                        String message = Message.PARSER_UNEXPECTED_EOI_FOR_EVENTS.with(location, ParserEventSet.VALUES);
                        throw new JsonParsingException(message, location);
                    }
                } else {
                    // This never happen.
                    throw new IllegalStateException();
                }
            }
        };
        return StreamSupport.stream(spliterator, false);
    }

    static Stream<JsonValue> valueStream(JsonParser parser) {
        Spliterator<JsonValue> spliterator = new AbstractSpliterator<JsonValue>() {

            @Override
            public boolean tryAdvance(Consumer<? super JsonValue> action) {
                if (parser.hasNext()) {
                    parser.next();
                    action.accept(parser.getValue());
                    return true;
                } else {
                    return false;
                }
            }
        };
        return StreamSupport.stream(spliterator, false);
    }

    private JsonStreams() {
    }

    /**
     * A skeletal implementation of {@link Spliterator}.
     * @author leadpony
     *
     * @param <T> the type of stream element.
     */
    private abstract static class AbstractSpliterator<T> extends Spliterators.AbstractSpliterator<T> {

        protected AbstractSpliterator() {
            super(Long.MAX_VALUE, Spliterator.ORDERED);
        }

        @Override
        public Spliterator<T> trySplit() {
            // this spliterator cannot be split
            return null;
        }
    }
}

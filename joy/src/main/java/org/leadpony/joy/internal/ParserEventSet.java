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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.stream.JsonParser.Event;

/**
 * @author leadpony
 *
 */
final class ParserEventSet extends AbstractSet<Event> {

    static final Set<Event> VALUES = of(
            Event.START_ARRAY,
            Event.START_OBJECT,
            Event.VALUE_STRING,
            Event.VALUE_NUMBER,
            Event.VALUE_TRUE,
            Event.VALUE_FALSE,
            Event.VALUE_NULL);

    static final Set<Event> START_ARRAY = of(Event.START_ARRAY);

    static final Set<Event> START_OBJECT = of(Event.START_OBJECT);

    static final Set<Event> START_STRUCTURE = of(
            Event.START_ARRAY,
            Event.START_OBJECT);

    static final Set<Event> VALUES_OR_END_ARRAY = of(
            Event.START_ARRAY,
            Event.START_OBJECT,
            Event.VALUE_STRING,
            Event.VALUE_NUMBER,
            Event.VALUE_TRUE,
            Event.VALUE_FALSE,
            Event.VALUE_NULL,
            Event.END_ARRAY);

    static final Set<Event> KEY_NAME_OR_END_OBJECT = of(
            Event.KEY_NAME,
            Event.END_OBJECT);

    static Set<Event> of(Event... events) {
        return new ParserEventSet(events);
    }

    private final Set<Event> events;
    private final String stringified;

    private ParserEventSet(Event... events) {
        EnumSet<Event> set = EnumSet.noneOf(Event.class);
        set.addAll(Arrays.asList(events));
        this.events = set;
        this.stringified = stringify(set);
    }

    @Override
    public Iterator<Event> iterator() {
        return events.iterator();
    }

    @Override
    public int size() {
        return events.size();
    }

    @Override
    public String toString() {
        return stringified;
    }

    private String stringify(EnumSet<Event> events) {
        return events.stream()
                .map(Event::toString)
                .collect(Collectors.joining(",", "[", "]"));
    }
}

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

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.leadpony.joy.core.BasicJsonLocation;
import org.leadpony.joy.core.JsonValues;
import org.leadpony.joy.core.AbstractJsonParser;
import org.leadpony.joy.core.Message;
import org.snakeyaml.engine.v2.events.ScalarEvent;
import org.snakeyaml.engine.v2.exceptions.Mark;

import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;

/**
 * @author leadpony
 */
final class YamlParser extends AbstractJsonParser implements ParserContext {

    private final Closeable closeable;
    private boolean alreadyClosed;

    private final Iterator<org.snakeyaml.engine.v2.events.Event> iterator;
    private boolean finished;

    /*
     * Current parser state. This never be {@code null}.
     */
    private ParserState state;
    /*
     *  Stack of parser states except the current state.
     */
    private final Deque<ParserState> stateStack = new ArrayDeque<>();

    private int sequenceDepth;
    private int mappingDepth;

    private org.snakeyaml.engine.v2.events.Event nextYamlEvent;
    private org.snakeyaml.engine.v2.events.Event yamlEvent;
    private EventType eventType;

    private JsonLocation location = BasicJsonLocation.INITIAL;

    YamlParser(Iterator<org.snakeyaml.engine.v2.events.Event> iterator, Closeable closeable) {
        this.iterator = iterator;
        this.closeable = closeable;
        this.state = ParserState.INITIAL;
    }

    @Override
    public boolean hasNext() {
        if (alreadyClosed) {
            return false;
        }
        if (!finished && nextYamlEvent == null) {
            nextYamlEvent = state.fetchEvent(iterator);
            if (nextYamlEvent == null) {
                finished = true;
            }
        }
        return !finished;
    }

    @Override
    public Event next() {
        if (hasNext()) {
            clearLocation();
            this.yamlEvent = this.nextYamlEvent;
            this.nextYamlEvent = null;
            this.eventType = state.processEvent(yamlEvent, this);
            return this.eventType.toJsonEvent();
        } else {
            throw new NoSuchElementException(Message.PARSER_NO_EVENTS.toString());
        }
    }

    @Override
    public String getString() {
        JsonParser.Event event = getCurrentEvent();
        if (event != Event.KEY_NAME && event != Event.VALUE_STRING && event != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getString()");
        }
        return getCurrentValue();
    }

    @Override
    public boolean isIntegralNumber() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("isIntegralNumber()");
        }
        return eventType == EventType.INTEGER;
    }

    @Override
    public int getInt() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getInt()");
        }

        String value = getCurrentValue();
        if (eventType == EventType.INTEGER && canRetrieveStrictInt(value)) {
            return Integer.parseInt(value);
        } else {
            return new BigDecimal(value).intValue();
        }
    }

    @Override
    public long getLong() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getLong()");
        }

        String value = getCurrentValue();
        if (eventType == EventType.INTEGER && canRetrieveStrictLong(value)) {
            return Long.parseLong(value);
        } else {
            return new BigDecimal(value).longValue();
        }
    }

    @Override
    public BigDecimal getBigDecimal() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getBigDecimal()");
        }
        return new BigDecimal(getCurrentValue());
    }

    @Override
    public JsonLocation getLocation() {
        if (location == null) {
            location = yamlEvent.getEndMark()
                    .map(YamlParser::markToLocation)
                    .orElse(BasicJsonLocation.UNKNOWN);
        }
        return location;
    }

    @Override
    public void close() {
        if (alreadyClosed) {
            return;
        }

        alreadyClosed = true;

        try {
            closeable.close();
        } catch (IOException e) {
            throw newJsonException(Message.PARSER_IO_ERROR_WHILE_CLOSING, e);
        }
    }

    /* As a DefaultJsonParser */

    @Override
    protected Event getCurrentEvent() {
        return eventType.toJsonEvent();
    }

    @Override
    protected boolean isInArray() {
        return sequenceDepth > 0;
    }

    @Override
    protected boolean isInObject() {
        return mappingDepth > 0;
    }

    @Override
    protected JsonString getValueAsString() {
        return JsonValues.valueOf(getCurrentValue());
    }

    @Override
    protected JsonNumber getValueAsNumber() {
        final String value = getCurrentValue();
        if (isIntegralNumber()) {
            if (canRetrieveStrictInt(value)) {
                return JsonValues.valueOf(Integer.parseInt(value));
            } else if (canRetrieveStrictLong(value)) {
                return JsonValues.valueOf(Long.parseLong(value));
            } else {
                return JsonValues.valueOf(new BigInteger(value));
            }
        } else {
            return JsonValues.valueOf(new BigDecimal(value));
        }
    }

    /* As a ParserContext */

    @Override
    public void setState(ParserState state) {
        this.state = state;
    }

    @Override
    public void beginSequence() {
        this.stateStack.addLast(this.state);
        this.state = ParserState.SEQUENCE;
        ++sequenceDepth;
    }

    @Override
    public void endSequence() {
        this.state = stateStack.removeLast();
        --sequenceDepth;
    }

    @Override
    public void beginMapping() {
        this.stateStack.addLast(this.state);
        this.state = ParserState.MAPPING_KEY;
        ++mappingDepth;
    }

    @Override
    public void endMapping() {
        this.state = stateStack.removeLast();
        --mappingDepth;
    }

    /* helpers */

    private String getCurrentValue() {
        return ((ScalarEvent) this.yamlEvent).getValue();
    }

    private boolean canRetrieveStrictInt(String value) {
        final int length = value.length();
        if (value.startsWith("-")) {
            if (length < 11) {
                return true;
            } else if (length == 11) {
                return value.compareTo(MIN_INT_AS_STRING) <= 0;
            }
        } else {
            if (length < 10) {
                return true;
            } else if (length == 10) {
                return value.compareTo(MAX_INT_AS_STRING) <= 0;
            }
        }
        return false;
    }

    private boolean canRetrieveStrictLong(String value) {
        final int length = value.length();
        if (value.startsWith("-")) {
            if (length < 20) {
                return true;
            } else if (length == 20) {
                return value.compareTo(MIN_LONG_AS_STRING) <= 0;
            }
        } else {
            if (length < 19) {
                return true;
            } else if (length == 19) {
                return value.compareTo(MAX_LONG_AS_STRING) <= 0;
            }
        }
        return false;
    }

    private void clearLocation() {
        this.location = null;
    }

    private static JsonLocation markToLocation(Mark mark) {
        return new BasicJsonLocation(mark.getLine(), mark.getColumn(), mark.getIndex());
    }
}

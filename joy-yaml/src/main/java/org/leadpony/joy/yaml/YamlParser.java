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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.leadpony.joy.core.DefaultJsonParser;
import org.leadpony.joy.core.Message;
import org.snakeyaml.engine.v2.events.ScalarEvent;

import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;

/**
 * @author leadpony
 */
final class YamlParser implements DefaultJsonParser, ParserContext {

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
        return ((ScalarEvent) yamlEvent).getValue();
    }

    @Override
    public boolean isIntegralNumber() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("isIntegralNumber()");
        }
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getInt() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getInt()");
        }
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getLong() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getLong()");
        }
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getBigDecimal()");
        }
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonLocation getLocation() {
        // TODO Auto-generated method stub
        return null;
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
    public Event getCurrentEvent() {
        return eventType.toJsonEvent();
    }

    @Override
    public boolean isInArray() {
        return sequenceDepth > 0;
    }

    @Override
    public boolean isInObject() {
        return mappingDepth > 0;
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
}

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

import java.util.Iterator;
import java.util.Optional;

import org.leadpony.joy.core.Message;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.events.ScalarEvent;
import org.snakeyaml.engine.v2.exceptions.Mark;
import org.snakeyaml.engine.v2.exceptions.ParserException;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

import jakarta.json.JsonException;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParsingException;

/**
 * @author leadpony
 */
enum ParserState {
    INITIAL {
        @Override
        protected Event doFetchEvent(Iterator<Event> it) {
            if (it.hasNext()) {
                Event event = it.next();
                requireEvent(event, Event.ID.StreamStart);
                if (it.hasNext()) {
                    event = it.next();
                    if (event.getEventId() == Event.ID.StreamEnd) {
                        return null;
                    }
                    requireEvent(event, Event.ID.DocumentStart);
                    if (it.hasNext()) {
                        event = it.next();
                        if (event.getEventId() == Event.ID.DocumentEnd) {
                            return null;
                        }
                        return event;
                    }
                }
            }
            return null;
        }

        @Override
        EventType processEvent(Event event, ParserContext context) {
            context.setState(FINAL);
            return super.processEvent(event, context);
        }
    },

    FINAL {
        @Override
        protected Event doFetchEvent(Iterator<Event> it) {
            if (it.hasNext()) {
                requireEvent(it.next(), Event.ID.DocumentEnd);
                if (it.hasNext()) {
                    requireEvent(it.next(), Event.ID.StreamEnd);
                    if (it.hasNext()) {
                        throw newParsingException(it.next().getStartMark());
                    }
                }
            }
            return null;
        }
    },

    SEQUENCE {
        @Override
        EventType processEvent(Event event, ParserContext context) {
            if (event.getEventId() == Event.ID.SequenceEnd) {
                context.endSequence();
                return EventType.SEQUENCE_END;
            } else {
                return super.processEvent(event, context);
            }
        }
    },

    MAPPING_KEY {
        @Override
        EventType processEvent(Event event, ParserContext context) {
            switch (event.getEventId()) {
            case Scalar:
                context.setState(MAPPING_VALUE);
                return EventType.KEY_NAME;
            case MappingEnd:
                context.endMapping();
                return EventType.MAPPING_END;
            default:
                // TOOD
                throw new IllegalStateException();
            }
        }
    },

    MAPPING_VALUE {
        @Override
        EventType processEvent(Event event, ParserContext context) {
            context.setState(MAPPING_KEY);
            return super.processEvent(event, context);
        }
    };

    /**
     * Fetches the next YAML event from the iterator.
     *
     * @param it the iterator of YAML events.
     * @return the fetched YAML event or {@code null} if no event was found.
     */
    final Event fetchEvent(Iterator<Event> it) {
        try {
            return doFetchEvent(it);
        } catch (ParserException e) {
            throw new JsonParsingException(
                    e.getMessage(),
                    e,
                    JsonLocations.at(e.getProblemMark()));
        } catch (YamlEngineException e) {
            throw new JsonException(Message.PARSER_IO_ERROR_WHILE_READING.toString(), e);
        }
    }

    EventType processEvent(Event event, ParserContext context) {
        switch (event.getEventId()) {
        case SequenceStart:
            context.beginSequence();
            return EventType.SEQUENCE_START;
        case MappingStart:
            context.beginMapping();
            return EventType.MAPPING_START;
        case Scalar:
            return EventType.of((ScalarEvent) event);
        case SequenceEnd:
        case MappingEnd:
        case StreamStart:
        case StreamEnd:
        case DocumentStart:
        case DocumentEnd:
        default:
            throw new IllegalStateException();
        }
    }

    protected Event doFetchEvent(Iterator<Event> it) {
        if (it.hasNext()) {
            return it.next();
        } else {
            return null;
        }
    }

    protected static Event requireEvent(Event event, Event.ID eventId) {
        if (event.getEventId() != eventId) {
            // TODO
            throw new IllegalStateException();
        }
        return event;
    }

    protected static JsonParsingException newParsingException(Event event) {
        return newParsingException(event.getStartMark());
    }

    protected static JsonParsingException newParsingException(Optional<Mark> mark) {
        JsonLocation location = JsonLocations.at(mark);
        int c = mark.map(m -> m.getBuffer()[m.getPointer()]).orElse((int) ' ');
        StringBuilder sb = new StringBuilder();
        sb.append("'").append((char) c).append("'");
        String message = Message.PARSER_UNEXPECTED_CHAR.with(location, sb.toString());
        return new JsonParsingException(message, location);
    }
}

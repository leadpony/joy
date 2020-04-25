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

import org.leadpony.joy.core.BasicJsonLocation;
import org.leadpony.joy.core.Message;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.events.ScalarEvent;
import org.snakeyaml.engine.v2.exceptions.Mark;
import org.snakeyaml.engine.v2.exceptions.MarkedYamlEngineException;
import org.snakeyaml.engine.v2.exceptions.ReaderException;
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
                Event event = it.next();
                if (event.getEventId() == Event.ID.DocumentEnd) {
                    if (it.hasNext()) {
                        event = it.next();
                        switch (event.getEventId()) {
                        case StreamEnd:
                        case DocumentStart:
                            return null;
                        default:
                            throw newUnexpectedEventException(event);
                        }
                    }
                } else {
                    throw newUnexpectedEventException(event);
                }
            }
            throw new IllegalStateException(LocalMessage.thatRequiredEventIsMissing());
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
            JsonLocation location;
            switch (event.getEventId()) {
            case Scalar:
                context.setState(MAPPING_VALUE);
                return EventType.KEY_NAME;
            case MappingEnd:
                context.endMapping();
                return EventType.MAPPING_END;
            case SequenceStart:
                location = locate(event);
                throw newParsingException(
                        LocalMessage.thatPropertyKeyMustNotBeArray(location),
                        location);
            case MappingStart:
                location = locate(event);
                throw newParsingException(
                        LocalMessage.thatPropertyKeyMustNotBeObject(location),
                        location);
            default:
                // This never happen.
                throw newIllegalStateException(event);
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
        } catch (MarkedYamlEngineException e) {
            throw new JsonParsingException(
                    e.getMessage(),
                    e,
                    JsonLocations.at(e.getProblemMark()));
        } catch (ReaderException e) {
            throw new JsonParsingException(
                    e.getMessage(),
                    e,
                    new BasicJsonLocation(e.getPosition()));
        } catch (YamlEngineException e) {
            throw new JsonException(Message.thatIOErrorOccurredWhileParserWasReading(), e);
        }
    }

    EventType processEvent(Event event, ParserContext context) {
        JsonLocation location;
        switch (event.getEventId()) {
        case SequenceStart:
            context.beginSequence();
            return EventType.SEQUENCE_START;
        case MappingStart:
            context.beginMapping();
            return EventType.MAPPING_START;
        case Scalar:
            return EventType.of((ScalarEvent) event);
        case Alias:
            location = locate(event);
            throw newParsingException(
                LocalMessage.thatAliasIsNotSupported(location),
                location);
        case SequenceEnd:
        case MappingEnd:
        case StreamStart:
        case StreamEnd:
        case DocumentStart:
        case DocumentEnd:
        default:
            throw newIllegalStateException(event);
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
            throw newIllegalStateException(event);
        }
        return event;
    }

    protected static JsonParsingException newParsingException(String message, JsonLocation location) {
        return new JsonParsingException(message, location);
    }

    protected static IllegalStateException newIllegalStateException(Event event) {
        JsonLocation location = locate(event);
        String message = LocalMessage.thatParserDetectedUnexpectedEvent(location, event);
        return new IllegalStateException(message);
    }

    protected static JsonParsingException newUnexpectedEventException(Event event) {
        JsonLocation location = locate(event);
        String message = event.getStartMark().map(
                mark -> Message.thatUnexpectedCharWasFound(location, firstCharOf(mark))
        ).orElseGet(
                () -> LocalMessage.thatParserDetectedUnexpectedEvent(location, event)
        );
        return newParsingException(message, location);
    }

    /**
     * Returns the location of the specified event.
     *
     * @param event the event to locate
     * @return the found location.
     */
    protected static JsonLocation locate(Event event) {
        return JsonLocations.at(event.getStartMark());
    }

    protected static char firstCharOf(Mark mark) {
        return (char) mark.getBuffer()[mark.getPointer()];
    }
}

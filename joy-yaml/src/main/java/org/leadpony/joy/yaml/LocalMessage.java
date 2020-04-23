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

import static org.leadpony.joy.core.Preconditions.requireNonNull;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.snakeyaml.engine.v2.events.Event;

import jakarta.json.stream.JsonLocation;

/**
 * Messages for YAML processors.
 *
 * @author leadpony
 */
final class LocalMessage {

    private static final String BUNDLE_NAME = LocalMessage.class.getPackage().getName() + ".messages";

    static String thatPropertyKeyMustNotBeArray(JsonLocation location) {
        requireNonNull(location, "location");
        return format("PropertyKeyMustNotBeArray", at(location));
    }

    static String thatPropertyKeyMustNotBeObject(JsonLocation location) {
        requireNonNull(location, "location");
        return format("PropertyKeyMustNotBeObject", at(location));
    }

    static String thatAliasIsNotSupported(JsonLocation location) {
        requireNonNull(location, "location");
        return format("AliasIsNotSupported", at(location));
    }

    static String thatParserDetectedUnexpectedEvent(JsonLocation location, Event event) {
        requireNonNull(location, "location");
        requireNonNull(event, "event");
        return format("ParserDetectedUnexpectedEvent", at(location), event.getEventId());
    }

    /* helpers */

    private static String format(String name, Object... arguments) {
        String pattern = getPattern(name);
        return MessageFormat.format(pattern, arguments);
    }

    private static String getPattern(String name) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);
        return bundle.getString(name);
    }

    private static String at(JsonLocation location) {
        return format("location",
                location.getLineNumber(),
                location.getColumnNumber(),
                location.getStreamOffset());
    }

    private LocalMessage() {
    }
}

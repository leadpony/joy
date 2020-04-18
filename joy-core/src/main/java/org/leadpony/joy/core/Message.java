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
package org.leadpony.joy.core;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import jakarta.json.stream.JsonLocation;

/**
 * @author leadpony
 */
public enum Message {
    PARSER_ILLEGAL_STATE,
    PARSER_UNEXPECTED_CHAR,
    PARSER_UNEXPECTED_CHAR_FOR,
    PARSER_UNEXPECTED_EOI,
    PARSER_UNEXPECTED_EOI_FOR_CHAR,
    PARSER_UNEXPECTED_EOI_FOR_EVENTS,
    PARSER_NO_EVENTS,
    PARSER_IO_ERROR_WHILE_READING,
    PARSER_IO_ERROR_WHILE_CLOSING,
    LOCATION,

    GENERATOR_ILLEGAL_CALL_FIRST,
    GENERATOR_ILLEGAL_CALL_AFTER_ARRAY_START,
    GENERATOR_ILLEGAL_CALL_AFTER_ARRAY_ITEM,
    GENERATOR_ILLEGAL_CALL_AFTER_OBJECT_START,
    GENERATOR_ILLEGAL_CALL_AFTER_PROPERTY_KEY,
    GENERATOR_ILLEGAL_CALL_AFTER_PROPERTY_VALUE,
    GENERATOR_ILLEGAL_CALL_AFTER_END,
    GENERATOR_NOT_COMPLETED,
    GENERATOR_IO_ERROR_WHILE_WRITING_OR_FLUSHING,
    GENERATOR_IO_ERROR_WHILE_CLOSING,

    WRITER_ALREADY_WRITTEN,
    WRITER_ALREADY_CLOSED,

    POINTER_MISSING_SLASH,
    POINTER_NO_SUCH_VALUE,
    POINTER_CANNOT_ADD,
    POINTER_CANNOT_REMOVE_ALL,
    POINTER_CANNOT_REPLACE_ALL,
    POINTER_ILLEGAL_VALUE_TYPE,

    PATCH_ILLEGAL_MOVE_OPERATION,
    PATCH_TEST_FAILED,
    PATCH_TYPE_MISMATCH,
    PATCH_NO_OPERATION,
    PATCH_UNKNOWN_OPERATION,
    PATCH_MALFORMED_OPERATION,

    JSON_VALUE_UNSUPPORTED_TYPE;

    private static final String BUNDLE_NAME = Message.class.getPackage().getName() + ".messages";

    @Override
    public String toString() {
        return getPattern();
    }

    public String with(Object... args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = stringify(args[i]);
        }
        String pattern = getPattern();
        return MessageFormat.format(pattern, args);
    }

    private static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(BUNDLE_NAME);
    }

    private String getPattern() {
        return getBundle().getString(name());
    }

    private static String stringify(Object object) {
        if (object == null) {
            return "null";
        } else if (object instanceof JsonLocation) {
            return stringify((JsonLocation) object);
        } else if (object instanceof Character) {
            return JsonChar.toString((char) object);
        }
        return object.toString();
    }

    private static String stringify(JsonLocation location) {
        return MessageFormat.format(LOCATION.getPattern(),
                location.getLineNumber(),
                location.getColumnNumber(),
                location.getStreamOffset());
    }
}

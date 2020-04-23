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

import static org.leadpony.joy.core.Preconditions.requireNonNull;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Set;

import jakarta.json.JsonPointer;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser.Event;

/**
 * Messages for JSON processors.
 *
 * @author leadpony
 */
public final class Message {

    private static final String BUNDLE_NAME = Message.class.getPackage().getName() + ".messages";

    /*
     * Messages for JSON parser
     */

    public static String thatParserIsInIllegalState(String method, Event event) {
        requireNonNull(method, "method");
        return format("ParserIsInIllegalState", method, nullable(event));
    }

    public static String thatUnexpectedCharWasFound(JsonLocation location, String actual) {
        requireNonNull(location, "location");
        requireNonNull(actual, "actual");
        return format("UnexpectedCharWasFound", at(location), actual);
    }

    public static String thatUnexpectedCharWasFoundFor(JsonLocation location, String actual, Object expected) {
        requireNonNull(location, "location");
        requireNonNull(actual, "actual");
        requireNonNull(expected, "expected");
        if (expected instanceof Character) {
            expected = JsonChar.toString((char) expected);
        }
        return format("UnexpectedCharWasFoundFor", at(location), actual, expected);
    }

    public static String thatUnexpectedEndOfInputWasReached(JsonLocation location) {
        requireNonNull(location, "location");
        return format("UnexpectedEndOfInputWasReached", at(location));
    }

    public static String thatUnexpectedEndOfInputWasReachedBeforeChar(JsonLocation location, char expected) {
        requireNonNull(location, "location");
        requireNonNull(expected, "expected");
        String encoded = JsonChar.toString(expected);
        return format("UnexpectedEndOfInputWasReachedBeforeChar", at(location), encoded);
    }

    public static String thatUnexpectedEndOfInputWasReachedBeforeChar(JsonLocation location, Object expected) {
        requireNonNull(location, "location");
        requireNonNull(expected, "expected");
        if (expected instanceof Character) {
            expected = JsonChar.toString((char) expected);
        }
        return format("UnexpectedEndOfInputWasReachedBeforeChar", at(location), expected);
    }

    public static String thatUnexpectedEndOfInputWasReachedBeforeEvents(JsonLocation location, Set<Event> expected) {
        requireNonNull(location, "location");
        requireNonNull(expected, "expected");
        return format("UnexpectedEndOfInputWasReachedBeforeEvents", at(location), expected);
    }

    public static String thatNoMoreParserEventsWereFound() {
        return format("NoMoreParserEventsWereFound");
    }

    public static String thatIOErrorOccurredWhileParserWasReading() {
        return format("IOErrorOccurredWhileParserWasReading");
    }

    public static String thatIOErrorOccurredWhileParserWasClosing() {
        return format("IOErrorOccurredWhileParserWasClosing");
    }

    /*
     * Messages for JSON generator
     */

    public static String thatIllegalGeneratorMethodWasCalledBeforeAll(String method) {
        requireNonNull(method, "method");
        return format("IllegalGeneratorMethodWasCalledBeforeAll", method);
    }

    public static String thatIllegalGeneratorMethodWasCalledAfterArrayStart(String method) {
        requireNonNull(method, "method");
        return format("IllegalGeneratorMethodWasCalledAfterArrayStart", method);
    }

    public static String thatIllegalGeneratorMethodWasCalledAfterArrayItem(String method) {
        requireNonNull(method, "method");
        return format("IllegalGeneratorMethodWasCalledAfterArrayItem", method);
    }

    public static String thatIllegalGeneratorMethodWasCalledAfterObjectStart(String method) {
        requireNonNull(method, "method");
        return format("IllegalGeneratorMethodWasCalledAfterObjectStart", method);
    }

    public static String thatIllegalGeneratorMethodWasCalledAfterPropertyKey(String method) {
        requireNonNull(method, "method");
        return format("IllegalGeneratorMethodWasCalledAfterPropertyKey", method);
    }

    public static String thatIllegalGeneratorMethodWasCalledAfterPropertyValue(String method) {
        requireNonNull(method, "method");
        return format("IllegalGeneratorMethodWasCalledAfterPropertyValue", method);
    }

    public static String thatIllegalGeneratorMethodWasCalledAfterAll(String method) {
        requireNonNull(method, "method");
        return format("IllegalGeneratorMethodWasCalledAfterAll", method);
    }

    public static String thatGeneratorIsNotCompleted() {
        return format("GeneratorIsNotCompleted");
    }

    public static String thatIOErrorOccurredWhileGeneratorWasWriting() {
        return format("IOErrorOccurredWhileGeneratorWasWriting");
    }

    public static String thatIOErrorOccurredWhileGeneratorWasClosing() {
        return format("IOErrorOccurredWhileGeneratorWasClosing");
    }

    /*
     * Messages for JSON writer
     */

    public static String thatWriterHasAlreadyWritten() {
        return format("WriterHasAlreadyWritten");
    }

    public static String thatWriterHasBeenAlreadyClosed() {
        return format("WriterHasBeenAlreadyClosed");
    }

    /*
     * Messages for JSON pointer
     */

    public static String thatJsonPointerMustStartWithSlash() {
        return format("JsonPointerMustStartWithSlash");
    }

    public static String thatJsonValueDoesNotExistAt(JsonPointer pointer) {
        requireNonNull(pointer, "pointer");
        return format("JsonValueDoesNotExistAt", pointer.toString());
    }

    public static String thatJsonValueCannotBeAddedAt(JsonPointer pointer) {
        requireNonNull(pointer, "pointer");
        return format("JsonValueCannotBeAddedAt", pointer.toString());
    }

    public static String thatJsonDocumentCannotBeRemoved() {
        return format("JsonDocumentCannotBeRemoved");
    }

    public static String thatJsonDocumentCannotBeReplaced() {
        return format("JsonDocumentCannotBeReplaced");
    }

    public static String thatJsonValueMustBeTheSameTypeAsTarget() {
        return format("JsonValueMustBeTheSameTypeAsTarget");
    }

    /*
     * Messages for JSON patch
     */

    public static String thatJsonValueCannotBeMoved(JsonPointer from, JsonPointer to) {
        requireNonNull(from, "from");
        requireNonNull(to, "to");
        return format("JsonValueCannotBeMoved", from, to);
    }

    public static String thatJsonValueIsNotEqualToExpected(String path) {
        requireNonNull(path, "path");
        return format("JsonValueIsNotEqualToExpected", path);
    }

    public static String thatSourceAndTargetTypesDoNotMatch() {
        return format("SourceAndTargetTypesDoNotMatch");
    }

    public static String thatJsonPatchDoesNotContainOperation() {
        return format("JsonPatchDoesNotContainOperation");
    }

    public static String thatJsonPatchContainsUnknownOperation(String op) {
        requireNonNull(op, "op");
        return format("JsonPatchContainsUnknownOperation", op);
    }

    public static String thatJsonPatchDoesNotContainProperty(String op, String name) {
        requireNonNull(op, "op");
        requireNonNull(name, "name");
        return format("JsonPatchDoesNotContainProperty", op, name);
    }

    /*
     * Messages for JSON value
     */

    public static String thatObjectCannotBeConvertedToJsonValue(Object object) {
        return format("ObjectCannotBeConvertedToJsonValue", object.getClass().getName());
    }

    private Message() {
    }

    private static String format(String name) {
        return getPattern(name);
    }

    private static String format(String name, Object... args) {
        return MessageFormat.format(getPattern(name), args);
    }

    private static String at(JsonLocation location) {
        return format("location",
                location.getLineNumber(),
                location.getColumnNumber(),
                location.getStreamOffset());
    }

    private static String nullable(Object object) {
        return (object == null) ? "null" : object.toString();
    }

    private static String getPattern(String name) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);
        return bundle.getString(name);
    }
}

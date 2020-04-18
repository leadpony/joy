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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

/**
 * The implementation of {@link JsonPointer} as defined by RFC 6901.
 *
 * @author leadpony
 */
final class JsonPointerImpl implements ExtendedJsonPointer {

    private static final ExtendedJsonPointer EMPTY = new EmptyJsonPointer();

    private final String jsonPointer;
    private final List<Token> tokens;

    static ExtendedJsonPointer parse(String jsonPointer) {
        if (jsonPointer.isEmpty()) {
            return EMPTY;
        }

        if (jsonPointer.charAt(0) != '/') {
            throw newInvalidPointerException(Message.POINTER_MISSING_SLASH);
        }

        return createPointer(jsonPointer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonStructure> T add(T target, JsonValue value) {
        requireNonNull(target, "target");
        requireNonNull(value, "value");
        return (T) execute(target, Command.ADD, value, tokens.iterator());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonStructure> T remove(T target) {
        requireNonNull(target, "target");
        return (T) remove(target, tokens.iterator());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonStructure> T replace(T target, JsonValue value) {
        requireNonNull(target, "target");
        requireNonNull(value, "value");
        return (T) execute(target, Command.REPLACE, value, tokens.iterator());
    }

    @Override
    public boolean containsValue(JsonStructure target) {
        requireNonNull(target, "target");
        JsonStructure current = target;
        Iterator<Token> it = this.tokens.iterator();
        while (it.hasNext()) {
            Token token = it.next();
            JsonValue value = token.get(current);
            if (value == null) {
                return false;
            } else if (JsonValues.isStructure(value)) {
                current = (JsonStructure) value;
            } else {
                return !it.hasNext();
            }
        }
        return true;
    }

    @Override
    public JsonValue getValue(JsonStructure target) {
        requireNonNull(target, "target");
        JsonStructure current = target;
        Iterator<Token> it = this.tokens.iterator();
        while (it.hasNext()) {
            Token token = it.next();
            JsonValue value = token.get(current);
            if (value == null) {
                throw newNoSuchValueException();
            } else if (JsonValues.isStructure(value)) {
                current = (JsonStructure) value;
            } else if (it.hasNext()) {
                throw newNoSuchValueException();
            } else {
                return value;
            }
        }
        return current;
    }

    /* As a ExtendedJsonPointer */

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean startsWith(ExtendedJsonPointer other) {
        requireNonNull(other, "other");
        return startsWith((JsonPointerImpl) other);
    }

    /* As a Object */

    @Override
    public int hashCode() {
        return jsonPointer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof JsonPointerImpl)) {
            return false;
        }
        JsonPointerImpl other = (JsonPointerImpl) obj;
        return jsonPointer.equals(other.jsonPointer);
    }

    @Override
    public String toString() {
        return jsonPointer;
    }

    private JsonPointerImpl(String jsonPointer, List<Token> tokens) {
        this.jsonPointer = jsonPointer;
        this.tokens = tokens;
    }

    private static JsonPointerImpl createPointer(String jsonPointer) {
        List<Token> tokens = parseReferenceTokens(jsonPointer);
        return new JsonPointerImpl(jsonPointer, tokens);
    }

    private static List<Token> parseReferenceTokens(String jsonPointer) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        final int end = jsonPointer.length();
        while (i < end) {
            // skips '/' prefix
            i++;
            if (i < end) {
                i = parseReferenceToken(jsonPointer, i, end, tokens);
            } else {
                // ends with '/'
                tokens.add(Token.EMPTY);
                break;
            }
        }
        return tokens;
    }

    private static int parseReferenceToken(String jsonPointer, int start, int end, List<Token> tokens) {
        boolean numeric = true;
        int i = start;
        for (; i < end; i++) {
            char c = jsonPointer.charAt(i);
            if (c == '/') {
                break;
            } else if (c == '~') {
                return escapeReferenceToken(jsonPointer, start, end, i, tokens);
            } else if (c < '0' || '9' < c) {
                numeric = false;
            }
        }
        String value = jsonPointer.substring(start, i);
        Token token = createToken(value, numeric);
        tokens.add(token);
        return i;
    }

    private static int escapeReferenceToken(String jsonPointer, int start, int end, int current, List<Token> tokens) {
        StringBuilder b = new StringBuilder();
        b.append(jsonPointer, start, current);

        char c;
        int i = current + 1;
        if (i < end) {
            c = jsonPointer.charAt(i);
            if (c == '0') {
                b.append('~');
                i++;
            } else if (c == '1') {
                b.append('/');
                i++;
            } else {
                b.append('~');
            }
        } else {
            b.append('~');
        }
        start = i;

        for (; i < end; i++) {
            c = jsonPointer.charAt(i);
            if (c == '/') {
                break;
            } else if (c == '~') {
                if (start < i) {
                    b.append(jsonPointer, start, i);
                }
                if (i + 1 < end) {
                    c = jsonPointer.charAt(i + 1);
                    if (c == '0') {
                        b.append('~');
                        i++;
                    } else if (c == '1') {
                        b.append('/');
                        i++;
                    } else {
                        b.append('~');
                    }
                } else {
                    b.append('~');
                }
                start = i + 1;
            }
        }

        if (start < i) {
            b.append(jsonPointer, start, i);
        }
        tokens.add(createToken(b.toString(), false));
        return i;
    }

    private static Token createToken(String value, boolean numeric) {
        if (value.isEmpty()) {
            return Token.EMPTY;
        } else if (value.equals("0")) {
            return Token.ZERO_INDEX;
        } else if (value.equals("-")) {
            return Token.HYPHEN;
        } else if (numeric) {
            if (value.charAt(0) == '0') {
                return new KeyToken(value);
            } else {
                return new IndexToken(value);
            }
        } else {
            return new KeyToken(value);
        }
    }

    private JsonStructure execute(JsonStructure target, Command command, JsonValue value, Iterator<Token> iterator) {
        Token token = iterator.next();
        if (iterator.hasNext()) {
            JsonValue referred = token.get(target);
            if (referred == null) {
                throw newNoSuchValueException();
            }
            if (JsonValues.isStructure(referred)) {
                JsonStructure modified = execute((JsonStructure) referred, command, value, iterator);
                return token.replace(target, modified);
            } else {
                throw newNoSuchValueException();
            }
        } else {
            return command.execute(token, target, value, this);
        }
    }

    private JsonStructure remove(JsonStructure target, Iterator<Token> iterator) {
        Token token = iterator.next();
        if (iterator.hasNext()) {
            JsonValue referred = token.get(target);
            if (referred == null) {
                throw newNoSuchValueException();
            }
            if (JsonValues.isStructure(referred)) {
                JsonStructure removed = remove((JsonStructure) referred, iterator);
                return token.replace(target, removed);
            } else {
                throw newNoSuchValueException();
            }
        } else {
            JsonStructure modified = token.remove(target);
            if (modified == target) {
                throw newNoSuchValueException();
            }
            return modified;
        }
    }

    private boolean startsWith(JsonPointerImpl other) {
        if (other.isEmpty()) {
            return true;
        }
        if (this.tokens.size() < other.tokens.size()) {
            return false;
        }
        int i = 0;
        for (Token otherToken : other.tokens) {
            Token token = this.tokens.get(i++);
            if (!token.toString().equals(otherToken.toString())) {
                return false;
            }
        }
        return true;
    }

    private static JsonException newInvalidPointerException(Message message) {
        return new JsonException(message.toString());
    }

    private JsonException newNoSuchValueException() {
        String message = Message.POINTER_NO_SUCH_VALUE.with(jsonPointer);
        return new JsonException(message);
    }

    private JsonException newIllegalOperationException() {
        String message = Message.POINTER_CANNOT_ADD.with(jsonPointer);
        return new JsonException(message);
    }

    /**
     * Commands to execute.
     *
     * @author leadpony
     */
    enum Command {
        /**
         * The command to add.
         */
        ADD() {
            @Override
            JsonStructure execute(Token token, JsonStructure target, JsonValue value, JsonPointerImpl pointer) {
                JsonStructure modified = token.add(target, value);
                if (modified == target) {
                    throw pointer.newIllegalOperationException();
                }
                return modified;
            }
        },

        /**
         * The command to replace.
         */
        REPLACE() {
            @Override
            JsonStructure execute(Token token, JsonStructure target, JsonValue value, JsonPointerImpl pointer) {
                JsonStructure modified = token.replace(target, value);
                if (modified == target) {
                    throw pointer.newNoSuchValueException();
                }
                return modified;
            }
        };

        /**
         * Executes this command.
         *
         * @param token the current token in the JSON pointer.
         * @param target the target JSON array or object.
         * @param value the value to add to the target.
         * @param pointer the instance of JSON pointer
         * @return modified JSON structure.
         */
        abstract JsonStructure execute(Token token, JsonStructure target, JsonValue value, JsonPointerImpl pointer);
    }

    /**
     * A JSON pointer represented by an empty string.
     *
     * @author leadpony
     */
    private static class EmptyJsonPointer implements ExtendedJsonPointer {

        private static final int HASH_CODE = "".hashCode();

        @Override
        @SuppressWarnings("unchecked")
        public <T extends JsonStructure> T add(T target, JsonValue value) {
            requireNonNull(target, "target");
            requireNonNull(value, "value");
            if (target.getValueType() == value.getValueType()) {
                return (T) value;
            }
            throw newJsonException(Message.POINTER_ILLEGAL_VALUE_TYPE);
        }

        @Override
        public <T extends JsonStructure> T remove(T target) {
            requireNonNull(target, "target");
            throw newJsonException(Message.POINTER_CANNOT_REMOVE_ALL);
        }

        @Override
        public <T extends JsonStructure> T replace(T target, JsonValue value) {
            requireNonNull(target, "target");
            requireNonNull(value, "value");
            throw newJsonException(Message.POINTER_CANNOT_REPLACE_ALL);
        }

        @Override
        public boolean containsValue(JsonStructure target) {
            requireNonNull(target, "target");
            return true;
        }

        @Override
        public JsonValue getValue(JsonStructure target) {
            requireNonNull(target, "target");
            return target;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean startsWith(ExtendedJsonPointer other) {
            requireNonNull(other, "other");
            return other.isEmpty();
        }

        @Override
        public int hashCode() {
            return HASH_CODE;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public String toString() {
            return "";
        }

        private static JsonException newJsonException(Message message) {
            return new JsonException(message.toString());
        }
    }

    /**
     * A reference token which composes a JSON pointer.
     *
     * @author leadpony
     */
    private interface Token {

        Token EMPTY = new KeyToken("");
        Token ZERO_INDEX = new IndexToken("0");
        Token HYPHEN = new HyphenToken();

        default JsonValue get(JsonStructure target) {
            if (target.getValueType() == ValueType.ARRAY) {
                return get(target.asJsonArray());
            } else {
                return get(target.asJsonObject());
            }
        }

        default JsonValue get(JsonArray target) {
            return null;
        }

        default JsonValue get(JsonObject target) {
            return null;
        }

        default JsonStructure add(JsonStructure target, JsonValue value) {
            if (target.getValueType() == ValueType.ARRAY) {
                return add(target.asJsonArray(), value);
            } else {
                return add(target.asJsonObject(), value);
            }
        }

        default JsonArray add(JsonArray target, JsonValue value) {
            return target;
        }

        default JsonObject add(JsonObject target, JsonValue value) {
            return target;
        }

        default JsonStructure replace(JsonStructure target, JsonValue value) {
            if (target.getValueType() == ValueType.ARRAY) {
                return replace(target.asJsonArray(), value);
            } else {
                return replace(target.asJsonObject(), value);
            }
        }

        default JsonArray replace(JsonArray target, JsonValue value) {
            return target;
        }

        default JsonObject replace(JsonObject target, JsonValue value) {
            return target;
        }

        default JsonStructure remove(JsonStructure target) {
            if (target.getValueType() == ValueType.ARRAY) {
                return remove(target.asJsonArray());
            } else {
                return remove(target.asJsonObject());
            }
        }

        default JsonArray remove(JsonArray target) {
            return target;
        }

        default JsonObject remove(JsonObject target) {
            return target;
        }
    }

    /**
     * A reference token representing a property key.
     *
     * @author leadpony
     */
    private static class KeyToken implements Token {

        private final String token;

        protected KeyToken(String token) {
            this.token = token;
        }

        @Override
        public JsonValue get(JsonObject target) {
            return target.asJsonObject().get(token);
        }

        @Override
        public final JsonObject add(JsonObject target, JsonValue value) {
            JsonObjectBuilderImpl builder = new JsonObjectBuilderImpl(target.asJsonObject());
            builder.add(token, value);
            return builder.build();
        }

        @Override
        public final JsonObject replace(JsonObject target, JsonValue value) {
            if (target.containsKey(token)) {
                return add(target, value);
            } else {
                return target;
            }
        }

        @Override
        public final JsonObject remove(JsonObject target) {
            if (target.containsKey(token)) {
                JsonObjectBuilderImpl builder = new JsonObjectBuilderImpl(target.asJsonObject());
                builder.remove(token);
                return builder.build();
            } else {
                return target;
            }
        }

        @Override
        public String toString() {
            return token;
        }
    }

    /**
     * A reference token representing an item index.
     *
     * @author leadpony
     */
    private static class IndexToken extends KeyToken {

        private final int index;

        IndexToken(String token) {
            super(token);
            this.index = Integer.parseInt(token);
        }

        @Override
        public JsonValue get(JsonArray target) {
            JsonArray array = target.asJsonArray();
            if (index < array.size()) {
                return array.get(index);
            } else {
                return null;
            }
        }

        @Override
        public final JsonArray add(JsonArray target, JsonValue value) {
            if (index > target.size()) {
                return target;
            }
            JsonArrayBuilderImpl builder = new JsonArrayBuilderImpl(target);
            if (index < target.size()) {
                builder.add(index, value);
            } else {
                builder.add(value);
            }
            return builder.build();
        }

        @Override
        public final JsonArray replace(JsonArray target, JsonValue value) {
            if (index < target.size()) {
                JsonArrayBuilderImpl builder = new JsonArrayBuilderImpl(target);
                builder.set(index, value);
                return builder.build();
            } else {
                return target;
            }
        }

        @Override
        public final JsonArray remove(JsonArray target) {
            if (index < target.size()) {
                JsonArrayBuilderImpl builder = new JsonArrayBuilderImpl(target.asJsonArray());
                builder.remove(index);
                return builder.build();
            } else {
                return target;
            }
        }
    }

    /**
     * A reference token representing a hyphen.
     *
     * @author leadpony
     */
    private static class HyphenToken extends KeyToken {

        HyphenToken() {
            super("-");
        }

        @Override
        public JsonArray add(JsonArray target, JsonValue value) {
            JsonArrayBuilderImpl builder = new JsonArrayBuilderImpl(target);
            builder.add(value);
            return builder.build();
        }
    }
}

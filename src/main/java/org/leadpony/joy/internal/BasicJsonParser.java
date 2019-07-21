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

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParsingException;

/**
 * An implementation of {@link JsonParser}.
 *
 * @author leadpony
 */
class BasicJsonParser extends AbstractJsonParser {

    private final Reader reader;
    private boolean alreadyClosed;

    private final CharBufferFactory bufferFactory;

    // current state
    private State state;
    // stack of states except current state
    private final Deque<State> stateStack = new ArrayDeque<>();

    private boolean readyToNext;

    private boolean eoi;

    private char[] readBuffer;
    private int readStart;
    private int readEnd;
    private int readPos;

    private int valueStart;
    private int valueEnd;

    private long lineNumber;

    /*
     * The stream offset of the first char in the read buffer.
     */
    private long bufferOffset;

    /*
     * The stream offset of the first char in the current line.
     */
    private long lineOffset;

    private boolean hasFracOrExp;

    private JsonLocation location = JsonLocationImpl.INITIAL;

    BasicJsonParser(Reader reader, CharBufferFactory bufferFactory) {
        this.reader = reader;
        this.bufferFactory = bufferFactory;

        this.lineNumber = 1;
        this.readBuffer = bufferFactory.createBuffer();

        this.state = State.INITIAL;
    }

    /* As a JsonParser */

    @Override
    public boolean hasNext() {
        if (readyToNext) {
            return true;
        }
        this.location = null;
        readyToNext = state.accepts(peekNonSpaceChar(), this);
        return readyToNext;
    }

    @Override
    public Event next() {
        if (!hasNext()) {
            throw new NoSuchElementException(Message.PARSER_NO_EVENTS.toString());
        }
        this.readyToNext = false;
        this.location = null;
        int c = peekNonSpaceChar();
        Event event = state.process(c, this);
        setCurrentEvent(event);
        return event;
    }

    @Override
    public String getString() {
        Event event = getCurrentEvent();
        if (event != Event.KEY_NAME
                && event != Event.VALUE_STRING
                && event != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getString()");
        }
        return buildString();
    }

    @Override
    public boolean isIntegralNumber() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("isIntegralNumber()");
        }
        return !hasFracOrExp || getBigDecimal().scale() == 0;
    }

    @Override
    public int getInt() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getInt()");
        }
        if (canGetIntStrictly()) {
            return getStrictInt();
        } else {
            return getBigDecimal().intValue();
        }
    }

    @Override
    public long getLong() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getLong()");
        }
        if (canGetLongStrictly()) {
            return getStrictLong();
        } else {
            return getBigDecimal().longValue();
        }
    }

    @Override
    public BigDecimal getBigDecimal() {
        if (getCurrentEvent() != Event.VALUE_NUMBER) {
            throw newIllegalStateException("getBigDecimal()");
        }
        return buildBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        if (location == null) {
            location = new JsonLocationImpl(lineNumber, getColumnNumber(), getStreamOffset());
        }
        return location;
    }

    @Override
    public JsonValue getValue() {
        Event event = getCurrentEvent();
        if (event == null) {
            throw newIllegalStateException("getValue()");
        }
        switch (event) {
        case VALUE_TRUE:
            return JsonValue.TRUE;
        case VALUE_FALSE:
            return JsonValue.FALSE;
        case VALUE_NULL:
            return JsonValue.NULL;
        case VALUE_STRING:
        case KEY_NAME:
            return getValueAsString();
        case VALUE_NUMBER:
            return getValueAsNumber();
        case START_ARRAY:
            return getValueAsArray();
        case START_OBJECT:
            return getValueAsObject();
        case END_ARRAY:
        case END_OBJECT:
        default:
            throw newIllegalStateException("getValue()");
        }
    }

    @Override
    public JsonArray getArray() {
        if (getCurrentEvent() != Event.START_ARRAY) {
            throw newIllegalStateException("getArray()");
        }
        return getValueAsArray();
    }

    @Override
    public JsonObject getObject() {
        if (getCurrentEvent() != Event.START_OBJECT) {
            throw newIllegalStateException("getObject()");
        }
        return getValueAsObject();
    }

    @Override
    public void close() {
        if (alreadyClosed) {
            return;
        }

        alreadyClosed = true;

        bufferFactory.releaseBuffer(readBuffer);

        try {
            reader.close();
        } catch (IOException e) {
            throw newJsonException(Message.PARSER_IO_ERROR_WHILE_CLOSING, e);
        }
    }

    /* As a AbstractJsonParser */

    @Override
    boolean isInCollection() {
        return !this.stateStack.isEmpty();
    }

    /* As a BasicJsonParser */

    Event processKey() {
        consumeChar();
        return processKey(peekNonSpaceChar());
    }

    Event processKey(int c) {
        if (c == '"') {
            parseString();
            return Event.KEY_NAME;
        } else {
            throw newUnexpectedCharException(c, '"');
        }
    }

    Event processValue() {
        consumeChar();
        return processValue(peekNonSpaceChar());
    }

    Event processValue(int c) {
        switch (c) {
        case '[':
            consumeChar();
            pushState(State.ARRAY_FIRST_ITEM);
            return Event.START_ARRAY;
        case '{':
            consumeChar();
            pushState(State.OBJECT_FIRST_KEY);
            return Event.START_OBJECT;
        case 't':
            parseTrue();
            return Event.VALUE_TRUE;
        case 'f':
            parseFalse();
            return Event.VALUE_FALSE;
        case 'n':
            parseNull();
            return Event.VALUE_NULL;
        case '"':
            parseString();
            return Event.VALUE_STRING;
        case '-':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            parseNumber(c);
            return Event.VALUE_NUMBER;
        case -1:
            throw newUnexpectedEndException(ParserEventSet.VALUES);
        default:
            throw newUnexpectedCharException(c);
        }
    }

    private int peekNonSpaceChar() {
        final char[] buffer = this.readBuffer;
        for (;;) {
            int readPos = this.readPos;
            int readEnd = this.readEnd;
            while (readPos < readEnd) {
                char c = buffer[readPos];
                if (c > 0x20) {
                    this.readPos = readPos;
                    return c;
                } else if (c == 0x20 || c == '\t') {
                    readPos++;
                } else if (c == '\n') {
                    startNewLine(++readPos);
                } else if (c == '\r') {
                    if (++readPos >= readEnd) {
                        if (!fillReadBuffer(0)) {
                            return -1;
                        }
                        readPos = 0;
                        readEnd = this.readEnd;
                    }
                    c = buffer[readPos];
                    if (c == '\n') {
                        readPos++;
                    }
                    startNewLine(readPos);
                } else {
                    this.readPos = readPos;
                    throw newUnexpectedCharException(c);
                }
            }

            if (!fillReadBuffer(0)) {
                return -1;
            }
        }
    }

    private void startNewLine(int readPos) {
        this.readPos = readPos;
        this.lineNumber++;
        this.lineOffset = getStreamOffset();
    }

    private int peekChar() {
        if (readPos >= readEnd) {
            if (!fillReadBuffer(0)) {
                return -1;
            }
        }
        return readBuffer[readPos];
    }

    private int peekValueChar() {
        if (this.readPos >= this.readEnd) {
            if (!fillReadBufferRetainingValue(this.valueEnd)) {
                return -1;
            }
        }
        return this.readBuffer[this.readPos];
    }

    private void consumeChar() {
        readPos++;
    }

    private void consumeChar(char expected) {
        int c = peekChar();
        if (c == expected) {
            readPos++;
        } else {
            throw newUnexpectedCharException(c, expected);
        }
    }

    /**
     * Fills the buffer with new characters.
     *
     * @param newStart the position to start to read.
     * @return {@code false} if the end of input was reached.
     */
    private boolean fillReadBuffer(int newStart) {
        if (eoi) {
            return false;
        }
        try {
            int charsToRead = readBuffer.length - newStart;
            int charsRead = this.reader.read(readBuffer, newStart, charsToRead);
            if (charsRead < 0) {
                eoi = true;
                this.readPos = this.readEnd;
                return false;
            }
            this.bufferOffset += readEnd - readStart;
            this.readStart = newStart;
            this.readEnd = newStart + charsRead;
            this.readPos = newStart;
            return true;
        } catch (IOException e) {
            throw newJsonException(Message.PARSER_IO_ERROR_WHILE_READING, e);
        }
    }

    private boolean fillReadBufferRetainingValue(int valueEnd) {
        final int valueLen = valueEnd - this.valueStart;
        if (valueLen > readBuffer.length / 2) {
            extendReadBuffer(this.valueStart, valueLen);
        } else if (valueStart > 0) {
            System.arraycopy(readBuffer, this.valueStart, readBuffer, 0, valueLen);
        }
        if (!fillReadBuffer(valueLen)) {
            return false;
        }
        this.valueStart = 0;
        this.valueEnd = valueLen;
        return true;
    }

    private void extendReadBuffer(int valueStart, int valueLen) {
        int newLength = readBuffer.length * 2;
        char[] newBuffer = new char[newLength];
        System.arraycopy(readBuffer, valueStart, newBuffer, 0, valueLen);
        this.readBuffer = newBuffer;
    }

    /**
     * Returns the current column number.
     *
     * @return the current column number.
     */
    private long getColumnNumber() {
        return 1L + getStreamOffset() - lineOffset;
    }

    /**
     * Returns the current stream offset.
     *
     * @return the current stream offset.
     */
    private long getStreamOffset() {
        return bufferOffset + (readPos - readStart);
    }

    private void parseTrue() {
        // Consumes 't'
        consumeChar();

        consumeChar('r');
        consumeChar('u');
        consumeChar('e');
    }

    private void parseFalse() {
        // Consumes 'f'
        consumeChar();

        consumeChar('a');
        consumeChar('l');
        consumeChar('s');
        consumeChar('e');
    }

    private void parseNull() {
        // Consumes 'n'
        consumeChar();

        consumeChar('u');
        consumeChar('l');
        consumeChar('l');
    }

    private void parseString() {
        // Consumes the opening quotation mark
        this.valueStart = ++this.readPos;

        for (;;) {
            char[] buffer = this.readBuffer;
            int readPos = this.readPos;
            int readEnd = this.readEnd;

            while (readPos < readEnd) {
                char c = buffer[readPos];
                if (c == '"') {
                    // Consumes the closing quotation mark
                    this.readPos = readPos + 1;
                    this.valueEnd = readPos;
                    return;
                } else if (c == '\\') {
                    this.readPos = readPos;
                    this.valueEnd = readPos;
                    parseEscapedString();
                    return;
                } else if (c >= 0x20) {
                    readPos++;
                } else {
                    this.readPos = readPos;
                    throw newUnexpectedCharException(c);
                }
            }

            if (!fillReadBufferRetainingValue(readPos)) {
                throw newUnexpectedEndException();
            }
        }
    }

    private void parseEscapedString() {
        char unescaped = unescape();
        this.readBuffer[this.valueEnd++] = unescaped;

        int c;
        while (((c = peekValueChar()) != '"')) {
            if (c == '\\') {
                unescaped = unescape();
                this.readBuffer[this.valueEnd++] = unescaped;
            } else if (c >= 0x20) {
                this.readBuffer[this.valueEnd++] = (char) c;
                consumeChar();
            } else {
                throw newUnexpectedCharException(c);
            }
        }

        // Consumes the last quotation mark.
        consumeChar();
    }

    private char unescape() {
        // Consumes the reverse solidus
        consumeChar();
        int c = peekValueChar();
        switch (c) {
        case '"':
        case '\\':
        case '/':
            consumeChar();
            return (char) c;
        case 'b':
            consumeChar();
            return '\b';
        case 'f':
            consumeChar();
            return '\f';
        case 'n':
            consumeChar();
            return '\n';
        case 'r':
            consumeChar();
            return '\r';
        case 't':
            consumeChar();
            return '\t';
        case 'u':
            return unescapeUnicode();
        default:
            throw newUnexpectedCharException(c);
        }
    }

    private char unescapeUnicode() {
        // Consumes the reverse solidus
        consumeChar();

        int c = peekValueChar();
        int codePoint = hexadecimalToInt(c);
        consumeChar();

        c = peekValueChar();
        codePoint = (codePoint << 4) + hexadecimalToInt(c);
        consumeChar();

        c = peekValueChar();
        codePoint = (codePoint << 4) + hexadecimalToInt(c);
        consumeChar();

        c = peekValueChar();
        codePoint = (codePoint << 4) + hexadecimalToInt(c);
        consumeChar();

        return (char) codePoint;
    }

    private int hexadecimalToInt(int c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        } else if ('A' <= c && c <= 'F') {
            return c - 'A' + 10;
        } else if ('a' <= c && c <= 'f') {
            return c - 'a' + 10;
        } else {
            throw newUnexpectedCharException(c);
        }
    }

    private void parseNumber(int c) {
        hasFracOrExp = false;

        resetValueBuffer(readPos);

        // minus (optional)
        if (c == '-') {
            consumeChar();
            c = peekValueChar();
        }

        // int
        if (c == '0') {
            consumeChar();
            c = peekValueChar();
        } else if ('1' <= c && c <= '9') {
            consumeChar();
            c = peekValueChar();
            while ('0' <= c && c <= '9') {
                consumeChar();
                c = peekValueChar();
            }
        } else {
            throw newUnexpectedCharException(c);
        }

        // frac (optional)
        if (c == '.') {
            hasFracOrExp = true;
            consumeChar();
            c = peekValueChar();
            if ('0' <= c && c <= '9') {
                consumeChar();
                c = peekValueChar();
            } else {
                throw newUnexpectedCharException(c);
            }
            while ('0' <= c && c <= '9') {
                consumeChar();
                c = peekValueChar();
            }
        }

        // exp (optional)
        if (c == 'e' || c == 'E') {
            hasFracOrExp = true;
            consumeChar();
            c = peekValueChar();
            if (c == '-' || c == '+') {
                consumeChar();
                c = peekValueChar();
            }
            if ('0' <= c && c <= '9') {
                consumeChar();
                c = peekValueChar();
            } else {
                throw newUnexpectedCharException(c);
            }
            while ('0' <= c && c <= '9') {
                consumeChar();
                c = peekValueChar();
            }
        }

        valueEnd = readPos;
    }

    private void resetValueBuffer(int start) {
        valueStart = start;
        valueEnd = readEnd;
    }

    private String buildString() {
        return new String(readBuffer, valueStart, valueEnd - valueStart);
    }

    private BigDecimal buildBigDecimal() {
        return new BigDecimal(readBuffer, valueStart, valueEnd - valueStart);
    }

    private boolean canGetIntStrictly() {
        if (hasFracOrExp) {
            return false;
        }
        final int length = valueEnd - valueStart;
        if (readBuffer[valueStart] == '-') {
            if (length <= 10) {
                return true;
            } else if (length == 11) {
                return buildString().compareTo("-2147483648") <= 0;
            }
        } else {
            if (length <= 9) {
                return true;
            } else if (length == 10) {
                return buildString().compareTo("2147483647") <= 0;
            }
        }
        return false;
    }

    private boolean canGetLongStrictly() {
        if (hasFracOrExp) {
            return false;
        }
        final int length = valueEnd - valueStart;
        if (readBuffer[valueStart] == '-') {
            if (length <= 19) {
                return true;
            } else if (length == 20) {
                return buildString().compareTo("-9223372036854775808") <= 0;
            }
        } else {
            if (length <= 18) {
                return true;
            } else if (length == 19) {
                return buildString().compareTo("9223372036854775807") <= 0;
            }
        }
        return false;
    }

    private int getStrictInt() {
        int i = valueStart;
        final boolean minus = readBuffer[i] == '-';
        if (minus) {
            i++;
        }
        int value = 0;
        while (i < valueEnd) {
            char c = readBuffer[i++];
            value = value * 10 + (c - '0');
        }
        return minus ? -value : value;
    }

    private long getStrictLong() {
        int i = valueStart;
        final boolean minus = readBuffer[i] == '-';
        if (minus) {
            i++;
        }
        long value = 0;
        while (i < valueEnd) {
            char c = readBuffer[i++];
            value = value * 10 + (c - '0');
        }
        return minus ? -value : value;
    }

    private JsonString getValueAsString() {
        return JsonValues.valueOf(readBuffer, valueStart, valueEnd - valueStart);
    }

    private JsonNumber getValueAsNumber() {
        if (hasFracOrExp) {
            BigDecimal value = buildBigDecimal();
            return JsonValues.valueOf(value);
        } else if (canGetIntStrictly()) {
            return JsonValues.valueOf(getStrictInt());
        } else if (canGetLongStrictly()) {
            return JsonValues.valueOf(getStrictLong());
        } else {
            BigInteger value = new BigInteger(buildString());
            return JsonValues.valueOf(value);
        }
    }

    /**
     * Returns the current value as a JSON array.
     *
     * @return the JSON array.
     */
    private JsonArray getValueAsArray() {
        JsonArrayBuilder builder = new JsonArrayBuilderImpl();
        while (hasNext()) {
            Event event = next();
            if (event == Event.END_ARRAY) {
                return builder.build();
            }
            builder.add(getValue());
        }
        throw newUnexpectedEndException(ParserEventSet.VALUES_OR_END_ARRAY);
    }

    /**
     * Returns the current value as a JSON object.
     *
     * @return the JSON object.
     */
    private JsonObject getValueAsObject() {
        JsonObjectBuilder builder = new JsonObjectBuilderImpl();
        while (hasNext()) {
            Event event = next();
            if (event == Event.END_OBJECT) {
                return builder.build();
            }
            String keyName = getString();
            if (hasNext()) {
                next();
                builder.add(keyName, getValue());
            } else {
                throw newUnexpectedEndException(':');
            }
        }
        throw newUnexpectedEndException(ParserEventSet.KEY_NAME_OR_END_OBJECT);
    }

    void pushState(State state) {
        stateStack.push(this.state);
        setState(state);
    }

    /**
     * Pops current state.
     */
    void popState() {
        // Consumes closing bracket which triggered this method.
        consumeChar();
        setState(stateStack.pop());
    }

    void setState(State state) {
        this.state = state;
    }

    JsonParsingException newUnexpectedCharException(int actual) {
        if (actual < 0) {
            return newUnexpectedEndException();
        }
        JsonLocation location = getLocation();
        String message = Message.PARSER_UNEXPECTED_CHAR.with(
                location, JsonChar.toString((char) actual));
        return new JsonParsingException(message, location);
    }

    JsonParsingException newUnexpectedCharException(int actual, Object expected) {
        if (actual < 0) {
            return newUnexpectedEndException(expected);
        }
        JsonLocation location = getLocation();
        String message = Message.PARSER_UNEXPECTED_CHAR_FOR.with(
                location, JsonChar.toString((char) actual), expected);
        return new JsonParsingException(message, location);
    }

    JsonParsingException newUnexpectedEndException() {
        JsonLocation location = getLocation();
        String message = Message.PARSER_UNEXPECTED_EOI.with(location);
        return new JsonParsingException(message, location);
    }

    JsonParsingException newUnexpectedEndException(Object expected) {
        JsonLocation location = getLocation();
        String message = Message.PARSER_UNEXPECTED_EOI_FOR_CHAR.with(location, expected);
        return new JsonParsingException(message, location);
    }

    /**
     * Parser state.
     *
     * @author leadpony
     */
    private enum State {
        INITIAL() {
            @Override
            boolean accepts(int c, BasicJsonParser parser) {
                return c >= 0;
            }

            @Override
            Event process(int c, BasicJsonParser parser) {
                parser.setState(FINISHED);
                return parser.processValue(c);
            }
        },

        FINISHED() {
            @Override
            boolean accepts(int c, BasicJsonParser parser) {
                if (c >= 0) {
                    throw parser.newUnexpectedCharException(c);
                } else {
                    return false;
                }
            }

            @Override
            Event process(int c, BasicJsonParser parser) {
                throw parser.newUnexpectedCharException(c);
            }
        },

        ARRAY_FIRST_ITEM() {
            @Override
            Event process(int c, BasicJsonParser parser) {
                if (c == ']') {
                    parser.popState();
                    return Event.END_ARRAY;
                }
                parser.setState(ARRAY_ITEM);
                return parser.processValue(c);
            }
        },

        ARRAY_ITEM() {
            @Override
            Event process(int c, BasicJsonParser parser) {
                if (c == ']') {
                    parser.popState();
                    return Event.END_ARRAY;
                }
                if (c == ',') {
                    return parser.processValue();
                } else {
                    throw parser.newUnexpectedCharException(c, COLON_OR_SQURE_BRACKET);
                }
            }
        },

        OBJECT_FIRST_KEY() {
            @Override
            Event process(int c, BasicJsonParser parser) {
                if (c == '}') {
                    parser.popState();
                    return Event.END_OBJECT;
                }
                parser.setState(OBJECT_VALUE);
                return parser.processKey(c);
            }
        },

        OBJECT_KEY() {
            @Override
            Event process(int c, BasicJsonParser parser) {
                if (c == '}') {
                    parser.popState();
                    return Event.END_OBJECT;
                }
                if (c != ',') {
                    throw parser.newUnexpectedCharException(c, COLON_OR_CURLY_BRACKET);
                }
                parser.setState(OBJECT_VALUE);
                return parser.processKey();
            }
        },

        OBJECT_VALUE() {
            @Override
            Event process(int c, BasicJsonParser parser) {
                if (c != ':') {
                    throw parser.newUnexpectedCharException(c, JsonChar.COLON);
                }
                parser.setState(OBJECT_KEY);
                return parser.processValue();
            }
        };

        private static final Set<JsonChar> COLON_OR_CURLY_BRACKET = JsonChar.of(
                JsonChar.COLON, JsonChar.CLOSING_CURLY_BRACKET);

        private static final Set<JsonChar> COLON_OR_SQURE_BRACKET = JsonChar.of(
                JsonChar.COLON, JsonChar.CLOSING_SQURE_BRACKET);

        boolean accepts(int c, BasicJsonParser parser) {
            if (c >= 0) {
                return true;
            } else {
                throw parser.newUnexpectedEndException();
            }
        }

        abstract Event process(int c, BasicJsonParser parser);
    }
}

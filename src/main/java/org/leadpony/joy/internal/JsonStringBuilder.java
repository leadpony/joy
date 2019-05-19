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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author leadpony
 */
class JsonStringBuilder {

    protected char[] writeBuffer;
    protected int writePos;

    protected JsonStringBuilder() {
        this(new char[16]);
    }

    protected JsonStringBuilder(char[] buffer) {
        this.writeBuffer = buffer;
    }

    final JsonStringBuilder append(String string) {
        final int end = string.length();
        int last = 0;
        for (int i = 0; i < end; i++) {
            char c = string.charAt(i);
            if (c < 0x20 || c == '"' || c == '\\') {
                if (last < i) {
                    appendString(string, last, i);
                }
                switch (c) {
                case '"':
                    append('\\').append('"');
                    break;
                case '\\':
                    append('\\').append('\\');
                    break;
                default:
                    appendString(JsonChar.escape(c));
                    break;
                }
                last = i + 1;
            }
        }

        if (last < end) {
            appendString(string, last, end);
        }

        return this;
    }

    final JsonStringBuilder append(char c) {
        if (writePos >= writeBuffer.length) {
            refreshBuffer(writePos);
        }
        writeBuffer[writePos++] = c;
        return this;
    }

    final JsonStringBuilder append(char c, int count) {
        while (count-- > 0) {
            if (writePos >= writeBuffer.length) {
                refreshBuffer(writePos);
            }
            writeBuffer[writePos++] = c;
        }
        return this;
    }

    final JsonStringBuilder append(BigInteger value) {
        return appendString(value.toString());
    }

    final JsonStringBuilder append(BigDecimal value) {
        return appendString(value.toString());
    }

    final JsonStringBuilder append(int value) {
        return appendString(String.valueOf(value));
    }

    final JsonStringBuilder append(long value) {
        return appendString(String.valueOf(value));
    }

    final JsonStringBuilder append(double value) {
        return appendString(String.valueOf(value));
    }

    final JsonStringBuilder append(boolean value) {
        return appendString(value ? "true" : "false");
    }

    final JsonStringBuilder appendNull() {
        return appendString("null");
    }

    @Override
    public final String toString() {
        return new String(writeBuffer, 0, writePos);
    }

    private JsonStringBuilder appendString(String string) {
        return appendString(string, 0, string.length());
    }

    private JsonStringBuilder appendString(String string, int start, int end) {
        char[] writeBuffer = this.writeBuffer;
        int writePos = this.writePos;

        int i = start;
        while (i < end) {
            if (writePos >= writeBuffer.length) {
                refreshBuffer(writePos);
                writeBuffer = this.writeBuffer;
                writePos = this.writePos;
            }
            int charsToCopy = end - i;
            if (writePos + charsToCopy > writeBuffer.length) {
                charsToCopy = writeBuffer.length - writePos;
            }
            string.getChars(i, i + charsToCopy, writeBuffer, writePos);
            i += charsToCopy;
            writePos += charsToCopy;
        }

        this.writePos = writePos;

        return this;
    }

    protected void refreshBuffer(int pos) {
        final int newBufferSize = writeBuffer.length * 2;
        writeBuffer = Arrays.copyOf(writeBuffer, newBufferSize);
        writePos = pos;
    }
}

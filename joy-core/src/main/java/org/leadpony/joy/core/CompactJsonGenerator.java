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
package org.leadpony.joy.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author leadpony
 */
class CompactJsonGenerator extends SimpleJsonGenerator {

    private final Writer writer;
    private final CharBufferFactory bufferFactory;
    private boolean alreadyClosed;

    CompactJsonGenerator(Writer writer, CharBufferFactory bufferFactory, boolean valueStream) {
        super(bufferFactory.createBuffer(), valueStream);
        this.writer = decorateWriter(writer);
        this.bufferFactory = bufferFactory;
    }

    @Override
    public void close() {
        if (alreadyClosed) {
            return;
        }

        super.close();

        try {
            flush();
            writer.close();
            bufferFactory.releaseBuffer(writeBuffer);
        } catch (IOException e) {
            throw newJsonException(Message.thatIOErrorOccurredWhileGeneratorWasClosing(), e);
        } finally {
            alreadyClosed = true;
        }
    }

    @Override
    public void flush() {
        try {
            flushBuffer();
            writer.flush();
        } catch (IOException e) {
            throw newJsonException(Message.thatIOErrorOccurredWhileGeneratorWasWriting(), e);
        }
    }

    @Override
    protected void refreshBuffer(int pos) {
        try {
            writer.write(writeBuffer, 0, pos);
            writePos = 0;
        } catch (IOException e) {
            throw newJsonException(Message.thatIOErrorOccurredWhileGeneratorWasWriting(), e);
        }
    }

    private void flushBuffer() throws IOException {
        if (writePos > 0) {
            writer.write(writeBuffer, 0, writePos);
            writePos = 0;
        }
    }

    private static Writer decorateWriter(Writer writer) {
        if (writer instanceof StringWriter) {
            return writer;
        } else {
            return new BufferedWriter(writer);
        }
    }
}

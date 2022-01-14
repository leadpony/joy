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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import jakarta.json.stream.JsonGeneratorFactory;

import org.leadpony.joy.api.JsonGenerator;

/**
 * An implementation of {@link JsonGeneratorFactory}.
 *
 * @author leadpony
 */
class JsonGeneratorFactoryImpl extends ConfigurableFactory implements JsonGeneratorFactory {

    private static final String[] SUPPORTED_PROPERTIES = {
        JsonGenerator.PRETTY_PRINTING,
        JsonGenerator.INDENTATION_SIZE,
        JsonGenerator.TAB_INDENTATION,
        JsonGenerator.VALUE_STREAM
    };

    private static final int DEFAULT_INDENTATION_SIZE = 4;

    private final boolean prettyPrinting;
    private final char indentationChar;
    private final int indentationSize;
    private final boolean valueStream;
    private final CharBufferFactory bufferFactory;

    JsonGeneratorFactoryImpl(Map<String, ?> config, CharBufferFactory bufferFactory) {
        super(config, SUPPORTED_PROPERTIES);
        this.bufferFactory = bufferFactory;
        this.prettyPrinting = containsProperty(JsonGenerator.PRETTY_PRINTING);
        if (containsProperty(JsonGenerator.TAB_INDENTATION)) {
            this.indentationChar = '\t';
            this.indentationSize = 1;
        } else {
            this.indentationChar = ' ';
            this.indentationSize = getPropertyValue(JsonGenerator.INDENTATION_SIZE, DEFAULT_INDENTATION_SIZE);
        }
        this.valueStream = containsProperty(JsonGenerator.VALUE_STREAM);
    }

    @Override
    public JsonGenerator createGenerator(Writer writer) {
        requireNonNull(writer, "writer");
        return createConfiguredGenerator(writer);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out) {
        requireNonNull(out, "out");
        Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        return createConfiguredGenerator(writer);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out, Charset charset) {
        requireNonNull(out, "out");
        requireNonNull(charset, "charset");
        Writer writer = new OutputStreamWriter(out, charset);
        return createConfiguredGenerator(writer);
    }

    private JsonGenerator createConfiguredGenerator(Writer writer) {
        if (prettyPrinting) {
            return new PrettyJsonGenerator(writer, bufferFactory, indentationChar, indentationSize, valueStream);
        } else {
            return new CompactJsonGenerator(writer, bufferFactory, valueStream);
        }
    }
}

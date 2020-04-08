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
package org.leadpony.joy.internal;

import static org.leadpony.joy.internal.Requirements.requireNonNull;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;

/**
 * @author leadpony
 */
class JsonWriterFactoryImpl extends JsonGeneratorFactoryImpl implements JsonWriterFactory {

    JsonWriterFactoryImpl(Map<String, ?> config, CharBufferFactory bufferFactory) {
        super(config, bufferFactory);
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        requireNonNull(writer, "writer");
        JsonGenerator generator = createGenerator(writer);
        return new JsonWriterImpl(generator);
    }

    @Override
    public JsonWriter createWriter(OutputStream out) {
        requireNonNull(out, "out");
        Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        JsonGenerator generator = createGenerator(writer);
        return new JsonWriterImpl(generator);
    }

    @Override
    public JsonWriter createWriter(OutputStream out, Charset charset) {
        requireNonNull(out, "out");
        requireNonNull(charset, "charset");
        Writer writer = new OutputStreamWriter(out, charset);
        JsonGenerator generator = createGenerator(writer);
        return new JsonWriterImpl(generator);
    }
}

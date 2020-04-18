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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;

/**
 * An implementation of {@link JsonParserFactory}.
 *
 * @author leadpony
 */
class JsonParserFactoryImpl extends AbstractJsonParserFactory {

    private final CharBufferFactory bufferFactory;

    JsonParserFactoryImpl(Map<String, ?> config, CharBufferFactory bufferFactory) {
        super(config);
        this.bufferFactory = bufferFactory;
    }

    @Override
    public JsonParser createParser(Reader reader) {
        requireNonNull(reader, "reader");
        return new BasicJsonParser(reader, bufferFactory);
    }

    @Override
    public JsonParser createParser(InputStream in) {
        requireNonNull(in, "in");
        Reader reader = createStreamReader(in);
        return new BasicJsonParser(reader, bufferFactory);
    }

    @Override
    public JsonParser createParser(InputStream in, Charset charset) {
        requireNonNull(in, "in");
        requireNonNull(charset, "charset");
        Reader reader = new InputStreamReader(in, charset);
        return new BasicJsonParser(reader, bufferFactory);
    }
}

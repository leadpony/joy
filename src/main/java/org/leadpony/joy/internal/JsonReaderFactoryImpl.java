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

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.stream.JsonParser;

/**
 * An implementation of {@link JsonReaderFactory}.
 *
 * @author leadpony
 */
class JsonReaderFactoryImpl extends JsonParserFactoryImpl implements JsonReaderFactory {

    JsonReaderFactoryImpl(Map<String, ?> config, CharBufferFactory bufferFactory) {
        super(config, bufferFactory);
    }

    @Override
    public JsonReader createReader(Reader reader) {
        JsonParser parser = createParser(reader);
        return new JsonReaderImpl(parser);
    }

    @Override
    public JsonReader createReader(InputStream in) {
        JsonParser parser = createParser(in);
        return new JsonReaderImpl(parser);
    }

    @Override
    public JsonReader createReader(InputStream in, Charset charset) {
        JsonParser parser = createParser(in, charset);
        return new JsonReaderImpl(parser);
    }
}

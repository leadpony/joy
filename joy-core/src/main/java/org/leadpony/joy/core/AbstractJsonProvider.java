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

import static org.leadpony.joy.core.Requirements.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;

/**
 * The implementation of {@link JsonProvider}.
 *
 * @author leadpony
 */
public abstract class AbstractJsonProvider extends JsonProvider implements InputStreamReaderFactory {

    private final CharBufferFactory bufferFactory = new PooledCharBufferFactory();

    /**
     * Constructs this provider.
     */
    protected AbstractJsonProvider() {
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
    public JsonParserFactory createParserFactory(Map<String, ?> config) {
        return new JsonParserFactoryImpl(getConfigOrEmpty(config), bufferFactory);
    }

    @Override
    public JsonGenerator createGenerator(Writer writer) {
        requireNonNull(writer, "writer");
        return new CompactJsonGenerator(writer, bufferFactory);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out) {
        requireNonNull(out, "out");
        Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        return new CompactJsonGenerator(writer, bufferFactory);
    }

    @Override
    public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
        return new JsonGeneratorFactoryImpl(getConfigOrEmpty(config), bufferFactory);
    }

    @Override
    public JsonReader createReader(Reader reader) {
        requireNonNull(reader, "reader");
        return new JsonReaderImpl(createParser(reader));
    }

    @Override
    public JsonReader createReader(InputStream in) {
        requireNonNull(in, "in");
        return new JsonReaderImpl(createParser(in));
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        requireNonNull(writer, "writer");
        JsonGenerator generator = new CompactJsonGenerator(writer, bufferFactory);
        return new JsonWriterImpl(generator);
    }

    @Override
    public JsonWriter createWriter(OutputStream out) {
        requireNonNull(out, "out");
        Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        JsonGenerator generator = new CompactJsonGenerator(writer, bufferFactory);
        return new JsonWriterImpl(generator);
    }

    @Override
    public JsonWriterFactory createWriterFactory(Map<String, ?> config) {
        return new JsonWriterFactoryImpl(getConfigOrEmpty(config), bufferFactory);
    }

    @Override
    public JsonReaderFactory createReaderFactory(Map<String, ?> config) {
        return new JsonReaderFactoryImpl(getConfigOrEmpty(config), bufferFactory);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder() {
        return new JsonObjectBuilderImpl();
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(JsonObject object) {
        requireNonNull(object, "object");
        return new JsonObjectBuilderImpl(object);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(Map<String, Object> map) {
        requireNonNull(map, "map");
        return new JsonObjectBuilderImpl(map);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder() {
        return new JsonArrayBuilderImpl();
    }

    @Override
    public JsonArrayBuilder createArrayBuilder(JsonArray array) {
        requireNonNull(array, "array");
        return new JsonArrayBuilderImpl(array);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
        requireNonNull(collection, "collection");
        return new JsonArrayBuilderImpl(collection);
    }

    @Override
    public JsonPointer createPointer(String jsonPointer) {
        requireNonNull(jsonPointer, "jsonPointer");
        return JsonPointerImpl.parse(jsonPointer);
    }

    @Override
    public JsonPatchBuilder createPatchBuilder() {
        return new JsonPatchBuilderImpl();
    }

    @Override
    public JsonPatchBuilder createPatchBuilder(JsonArray array) {
        requireNonNull(array, "array");
        return new JsonPatchBuilderImpl(array);
    }

    @Override
    public JsonPatch createPatch(JsonArray array) {
        requireNonNull(array, "array");
        return JsonPatchImpl.of(array);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the type of {@code target} is not the
     *                                  same as {@code source}.
     */
    @Override
    public JsonPatch createDiff(JsonStructure source, JsonStructure target) {
        requireNonNull(source, "source");
        requireNonNull(target, "target");
        if (source.getValueType() != target.getValueType()) {
            throw new IllegalArgumentException(Message.PATCH_TYPE_MISMATCH.toString());
        }
        return JsonDiffPatchBuilder.createDiff(source, target);
    }

    @Override
    public JsonMergePatch createMergePatch(JsonValue patch) {
        requireNonNull(patch, "patch");
        return JsonMergePatchImpl.of(patch);
    }

    @Override
    public JsonMergePatch createMergeDiff(JsonValue source, JsonValue target) {
        requireNonNull(source, "source");
        requireNonNull(target, "target");
        return JsonMergePatchImpl.between(source, target);
    }

    @Override
    public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
        return new JsonBuilderFactoryImpl(getConfigOrEmpty(config));
    }

    @Override
    public JsonNumber createValue(int value) {
        return JsonValues.valueOf(value);
    }

    @Override
    public JsonNumber createValue(long value) {
        return JsonValues.valueOf(value);
    }

    @Override
    public JsonNumber createValue(double value) {
        return JsonValues.valueOf(value);
    }

    @Override
    public JsonNumber createValue(BigDecimal value) {
        return JsonValues.valueOf(value);
    }

    @Override
    public JsonNumber createValue(BigInteger value) {
        return JsonValues.valueOf(value);
    }

    @Override
    public JsonString createValue(String value) {
        return JsonValues.valueOf(value);
    }

    protected static Map<String, ?> getConfigOrEmpty(Map<String, ?> config) {
        if (config == null) {
            return Collections.emptyMap();
        } else {
            return config;
        }
    }
}

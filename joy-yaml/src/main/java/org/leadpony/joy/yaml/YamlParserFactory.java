/*
 * Copyright 2020 the original author or authors.
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

package org.leadpony.joy.yaml;

import static org.leadpony.joy.core.Requirements.requireNonNull;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.leadpony.joy.core.AbstractJsonParserFactory;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Parse;
import org.snakeyaml.engine.v2.events.Event;

import jakarta.json.stream.JsonParser;

/**
 * @author leadpony
 */
final class YamlParserFactory extends AbstractJsonParserFactory {

    private final Parse parse;

    YamlParserFactory() {
        this(Collections.emptyMap());
    }

    YamlParserFactory(Map<String, ?> properties) {
        super(properties);
        this.parse = buildParse();
    }

    @Override
    public JsonParser createParser(Reader reader) {
        requireNonNull(reader, "reader");
        Iterator<Event> iterator = parse.parseReader(reader).iterator();
        return new YamlParser(iterator, reader);
    }

    @Override
    public JsonParser createParser(InputStream in) {
        requireNonNull(in, "in");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonParser createParser(InputStream in, Charset charset) {
        requireNonNull(in, "in");
        requireNonNull(charset, "charset");
        // TODO Auto-generated method stub
        return null;
    }

    /* helpers */

    private Parse buildParse() {
        LoadSettings settings = LoadSettings.builder().build();
        return new Parse(settings);
    }

}

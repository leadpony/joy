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
import java.util.Map;

import org.leadpony.joy.core.AbstractJsonProvider;

import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import jakarta.json.spi.JsonProvider;

/**
 * A {@link JsonProvider} for producing YAML parsers.
 *
 * @author leadpony
 */
public final class YamlProvider extends AbstractJsonProvider {

    private YamlParserFactory defaultParserFactory;

    /**
     * Constructs this provider.
     */
    public YamlProvider() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonParser createParser(Reader reader) {
        requireNonNull(reader, "reader");
        return getDefaultParserFactory().createParser(reader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonParser createParser(InputStream in) {
        requireNonNull(in, "in");
        return getDefaultParserFactory().createParser(in);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonParserFactory createParserFactory(Map<String, ?> config) {
        if (config == null || config.isEmpty()) {
            return getDefaultParserFactory();
        } else {
            return new YamlParserFactory(config);
        }
    }

    /* helpers */

    private JsonParserFactory getDefaultParserFactory() {
        if (defaultParserFactory == null) {
            defaultParserFactory = new YamlParserFactory();
        }
        return defaultParserFactory;
    }
}

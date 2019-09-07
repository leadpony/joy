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

import static org.leadpony.joy.internal.Requirements.requireNonNull;

import java.util.Collection;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author leadpony
 */
class JsonBuilderFactoryImpl extends ConfigurableFactory implements JsonBuilderFactory {

    JsonBuilderFactoryImpl(Map<String, ?> config) {
        super(config, NO_SUPPORTED_PROPERTIES);
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
    public JsonObjectBuilder createObjectBuilder(Map<String, Object> object) {
        requireNonNull(object, "object");
        return new JsonObjectBuilderImpl(object);
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
}

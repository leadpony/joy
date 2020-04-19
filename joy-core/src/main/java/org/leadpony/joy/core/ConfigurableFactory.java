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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory with configuration properties.
 *
 * @author leadpony
 */
class ConfigurableFactory {

    public static final String[] NO_SUPPORTED_PROPERTIES = {};

    private final Map<String, Object> properties;

    /**
     * Constructs this factory.
     *
     * @param properties all of the configuration properties.
     * @param supported the keys of configuration properties which this factory supports.
     */
    protected ConfigurableFactory(Map<String, ?> properties, String... supported) {
        if (supported.length == 0) {
            this.properties = Collections.emptyMap();
        } else {
            Map<String, Object> newMap = new HashMap<>();
            for (String key : supported) {
                if (properties.containsKey(key)) {
                    newMap.put(key, properties.get(key));
                }
            }
            this.properties = Collections.unmodifiableMap(newMap);
        }
    }

    public final Map<String, ?> getConfigInUse() {
        return properties;
    }

    public final boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public final <T> T getPropertyValue(String key, T defaultValue) {
        Object value = properties.get(key);
        if (defaultValue.getClass().isInstance(value)) {
            return (T) value;
        } else {
            return defaultValue;
        }
    }
}

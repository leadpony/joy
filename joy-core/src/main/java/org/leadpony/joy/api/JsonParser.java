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
package org.leadpony.joy.api;

/**
 * An extended {@link JsonParser}.
 *
 * @author leadpony
 */
public interface JsonParser {

    /**
     * Configuration property to indicate that the input of the parser is a
     * sequence of JSON values. The value of the property could be anything.
     * <pre>
     * <code>
     * Map&lt;String, Object&gt; config = new HashMap&lt;&gt;();
     * config.put(JsonParser.VALUE_STREAM, Boolean.TRUE);
     * </code>
     * </pre>
     *
     * @since 1.2
     */
    String VALUE_STREAM = "org.leadpony.joy.api.JsonParser.valueStream";
}

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
 * An extended {@link JsonGenerator}.
 *
 * @author leadpony
 */
public interface JsonGenerator extends jakarta.json.stream.JsonGenerator {

    /**
     * Configuration property to specify the number of spaces to be used as an
     * indentation. The value of the property must be an integer. By default the
     * number is 4.
     *
     * <pre>
     * <code>
     * Map&lt;String, Object&gt; config = new HashMap&lt;&gt;();
     * config.put(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
     * config.put(JsonGenerator.INDENTATION_SIZE, 2);
     * </code>
     * </pre>
     *
     * @since 1.1
     */
    String INDENTATION_SIZE = "org.leadpony.joy.api.JsonGenerator.indentationSize";

    /**
     * Configuration property to use a tab for indentation instead of spaces.
     * The value of the property could be anything.
     * <pre>
     * <code>
     * Map&lt;String, Object&gt; config = new HashMap&lt;&gt;();
     * config.put(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
     * config.put(JsonGenerator.TAB_INDENTATION, Boolean.TRUE);
     * </code>
     * </pre>
     *
     * @since 1.1
     */
    String TAB_INDENTATION = "org.leadpony.joy.api.JsonGenerator.tabIndentation";

    /**
     * Configuration property to indicate that the output of the generator is a
     * sequence of JSON values. The value of the property could be anything.
     * <pre>
     * <code>
     * Map&lt;String, Object&gt; config = new HashMap&lt;&gt;();
     * config.put(JsonGenerator.VALUE_STREAM, Boolean.TRUE);
     * </code>
     * </pre>
     *
     * @since 1.2
     */
    String VALUE_STREAM = "org.leadpony.joy.api.JsonGenerator.valueStream";
}

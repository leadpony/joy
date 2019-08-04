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
package org.leadpony.joy.api;

/**
 * An extended {@link JsonGenerator}.
 *
 * @author leadpony
 */
public interface JsonGenerator extends javax.json.stream.JsonGenerator {

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
     * Configuration property to use tab for indentation instead of spaces. The
     * value of the property could be be anything.
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
}

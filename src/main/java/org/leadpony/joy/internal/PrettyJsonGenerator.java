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

import java.io.Writer;

/**
 * @author leadpony
 */
class PrettyJsonGenerator extends CompactJsonGenerator {

    private final int indentSize;
    private int totalIndentSize;

    PrettyJsonGenerator(Writer writer, CharBufferFactory bufferFactory, int indentSize) {
        super(writer, bufferFactory);
        this.indentSize = indentSize;
    }

    @Override
    protected void appendOpeningBracket(char c) {
        super.appendOpeningBracket(c);
        totalIndentSize += indentSize;
    }

    @Override
    protected void appendClosingBracket(char c) {
        totalIndentSize -= indentSize;
        append('\n');
        indent();
        super.appendClosingBracket(c);
    }

    @Override
    protected void appendBreak() {
        append('\n');
        indent();
    }

    @Override
    protected void appendComma() {
        super.appendComma();
        append('\n');
        indent();
    }

    @Override
    protected void appendColon() {
        super.appendColon();
        append(' ');
    }

    private void indent() {
        append(' ', totalIndentSize);
    }
}

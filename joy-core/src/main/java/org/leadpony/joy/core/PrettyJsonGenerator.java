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

import java.io.Writer;

/**
 * @author leadpony
 */
final class PrettyJsonGenerator extends CompactJsonGenerator {

    private final char indentationChar;
    private final int indentationSize;
    private int indentationTotal;

    PrettyJsonGenerator(Writer writer, CharBufferFactory bufferFactory, char indentationChar, int indentationSize,
            boolean valueStream) {
        super(writer, bufferFactory, valueStream);
        this.indentationChar = indentationChar;
        this.indentationSize = indentationSize;
    }

    @Override
    protected void appendOpeningBracket(char c) {
        super.appendOpeningBracket(c);
        indent();
    }

    @Override
    protected void appendClosingBracket(char c) {
        dedent();
        append('\n');
        appendIndentation();
        super.appendClosingBracket(c);
    }

    @Override
    protected void appendBreak() {
        append('\n');
        appendIndentation();
    }

    @Override
    protected void appendComma() {
        super.appendComma();
        append('\n');
        appendIndentation();
    }

    @Override
    protected void appendColon() {
        super.appendColon();
        append(' ');
    }

    private void indent() {
        indentationTotal += indentationSize;
    }

    private void dedent() {
        indentationTotal -= indentationSize;
    }

    private void appendIndentation() {
        append(indentationChar, indentationTotal);
    }
}

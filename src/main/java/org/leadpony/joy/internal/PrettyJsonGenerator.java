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

    private static final int SPACES_PER_INDENTATION = 4;
    private int indentSize;

    PrettyJsonGenerator(Writer writer, CharBufferFactory bufferFactory) {
        super(writer, bufferFactory);
    }

    @Override
    protected void appendOpeningBracket(char c) {
        super.appendOpeningBracket(c);
        indentSize += SPACES_PER_INDENTATION;
    }

    @Override
    protected void appendClosingBracket(char c) {
        indentSize -= SPACES_PER_INDENTATION;
        append('\n');
        append(' ', indentSize);
        super.appendClosingBracket(c);
    }

    @Override
    protected void appendBreak() {
        append('\n');
        append(' ', indentSize);
    }

    @Override
    protected void appendComma() {
        super.appendComma();
        append('\n');
        append(' ', indentSize);
    }

    @Override
    protected void appendColon() {
        super.appendColon();
        append(' ');
    }
}

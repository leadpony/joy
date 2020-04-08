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
package org.leadpony.joy.core;

import jakarta.json.stream.JsonLocation;

/**
 * @author leadpony
 */
class JsonLocationImpl implements JsonLocation {

    /**
     * Initial location.
     */
    static final JsonLocation INITIAL = new JsonLocationImpl(1, 1, 0);
    /**
     * Unknown location.
     */
    static final JsonLocation UNKNOWN = new JsonLocationImpl(-1, -1, -1);

    private final long lineNumber;
    private final long columnNumber;
    private final long streamOffset;

    JsonLocationImpl(long lineNumber, long columnNumber, long streamOffset) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.streamOffset = streamOffset;
    }

    @Override
    public long getLineNumber() {
        return lineNumber;
    }

    @Override
    public long getColumnNumber() {
        return columnNumber;
    }

    @Override
    public long getStreamOffset() {
        return streamOffset;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("[");
        b.append("line=").append(getLineNumber())
         .append(",column=").append(getColumnNumber())
         .append(",offset=").append(getStreamOffset());
        return b.append("]").toString();
    }
}

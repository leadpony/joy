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
 * A basic implementation of {@link JsonLocation}.
 *
 * @author leadpony
 */
public class BasicJsonLocation implements JsonLocation {

    /**
     * Initial location.
     */
    public static final JsonLocation INITIAL = new BasicJsonLocation(1, 1, 0);
    /**
     * Unknown location.
     */
    public static final JsonLocation UNKNOWN = new BasicJsonLocation(-1, -1, -1);

    private final long lineNumber;
    private final long columnNumber;
    private final long streamOffset;

    public BasicJsonLocation(long streamOffset) {
        this.lineNumber = -1;
        this.columnNumber = -1;
        this.streamOffset = streamOffset;
    }

    public BasicJsonLocation(long lineNumber, long columnNumber, long streamOffset) {
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

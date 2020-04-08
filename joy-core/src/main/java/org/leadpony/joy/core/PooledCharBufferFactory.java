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

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A char buffer factory which can keep created buffers.
 *
 * @author leadpony
 */
class PooledCharBufferFactory implements CharBufferFactory {

    private final int defaultSize;
    private final int maxBuffers;

    private final Queue<WeakReference<char[]>> pool;

    PooledCharBufferFactory() {
        this(4096, 5);
    }

    PooledCharBufferFactory(int defaultSize, int maxBuffers) {
        this.defaultSize = defaultSize;
        this.maxBuffers = maxBuffers;
        this.pool = new ArrayDeque<>();
    }

    @Override
    public char[] createBuffer() {
        char[] buffer = getBuffer();
        if (buffer != null) {
            return buffer;
        }
        return createNewBuffer();
    }

    @Override
    public void releaseBuffer(char[] buffer) {
        putBuffer(buffer);
    }

    private char[] createNewBuffer() {
        return new char[defaultSize];
    }

    private synchronized char[] getBuffer() {
        while (!pool.isEmpty()) {
            WeakReference<char[]> ref = pool.poll();
            char[] buffer = ref.get();
            if (buffer != null) {
                return buffer;
            }
        }
        return null;
    }

    private synchronized void putBuffer(char[] buffer) {
        while (pool.size() >= maxBuffers) {
            pool.poll();
        }
        pool.offer(new WeakReference<>(buffer));
    }
}

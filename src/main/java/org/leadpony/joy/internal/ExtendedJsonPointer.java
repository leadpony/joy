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

import javax.json.JsonPointer;

/**
 * The extended interface of {@link javax.json.JsonPointer}.
 *
 * @author leadpony
 */
interface ExtendedJsonPointer extends JsonPointer {

    /**
     * Checks if this pointer is empty or not.
     *
     * @return {@code true} if this pointer is empty, otherwise {@code false}.
     */
    boolean isEmpty();

    /**
     * Checks if this pointer starts with the same reference tokens as the given
     * pointer. If the given pointer has more reference tokens than this pointer
     * then {@code false} will be returned.
     *
     * @param other the other JSON Pointer.
     * @return {@code true} if this pointer starts with the given path, otherwise
     *         {@code false}.
     * @throws NullPointerException if {@code other} is {@code null}.
     */
    boolean startsWith(ExtendedJsonPointer other);
}

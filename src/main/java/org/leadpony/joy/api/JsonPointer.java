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
 * The extended interface of {@link javax.json.JsonPointer}.
 *
 * @author leadpony
 */
public interface JsonPointer extends javax.json.JsonPointer {

    /**
     * Returns the string representation of this JSON pointer. The value to be
     * returned is an empty string or a sequence of '{@code /}' prefixed tokens.
     *
     * @return the valid escaped JSON Pointer string.
     */
    @Override
    String toString();
}

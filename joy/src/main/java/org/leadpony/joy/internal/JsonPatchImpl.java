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
package org.leadpony.joy.internal;

import java.util.Collections;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonPatch;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

/**
 * The implementation of {@link JsonPatch}.
 *
 * @author leadpony
 */
final class JsonPatchImpl implements JsonPatch {

    private final JsonArray array;
    private final List<PatchOperation> operations;

    /*
     * An empty JSON patch.
     */
    private static final JsonPatch EMPTY = new JsonPatchImpl(
        JsonValue.EMPTY_JSON_ARRAY, Collections.emptyList());

    static JsonPatch of(JsonArray array) {
        return of(array, PatchOperation.asOperations(array));
    }

    static JsonPatch of(JsonArray array, List<PatchOperation> operations) {
        if (array.isEmpty()) {
            return EMPTY;
        } else {
            return new JsonPatchImpl(array, operations);
        }
    }

    /**
     * Returns an empty JSON patch.
     *
     * @return an empty JSON patch.
     */
    static JsonPatch empty() {
        return EMPTY;
    }

    /**
     * Constructs this patch.
     *
     * @param array      the JSON array representing this patch.
     * @param operations the list of operations.
     */
    private JsonPatchImpl(JsonArray array, List<PatchOperation> operations) {
        this.array = array;
        this.operations = operations;
    }

    @Override
    public <T extends JsonStructure> T apply(T target) {
        for (PatchOperation operation : this.operations) {
            target = operation.apply(target);
        }
        return target;
    }

    @Override
    public JsonArray toJsonArray() {
        return array;
    }

    /* As an Object */

    @Override
    public int hashCode() {
        return array.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof JsonPatch)) {
            return false;
        }
        JsonPatch other = (JsonPatch) obj;
        return array.equals(other.toJsonArray());
    }

    @Override
    public String toString() {
        return array.toString();
    }
}

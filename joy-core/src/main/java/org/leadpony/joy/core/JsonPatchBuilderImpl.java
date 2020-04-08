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

import static org.leadpony.joy.core.Requirements.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonValue;

/**
 * @author leadpony
 */
class JsonPatchBuilderImpl implements JsonPatchBuilder {

    private List<PatchOperation> operations;

    JsonPatchBuilderImpl() {
    }

    JsonPatchBuilderImpl(JsonArray array) {
        this.operations = PatchOperation.asOperations(array);
    }

    @Override
    public final JsonPatchBuilder add(String path, JsonValue value) {
        requireNonNull(path, "path");
        requireNonNull(path, "value");
        return append(new PatchOperation.Add(path, value));
    }

    @Override
    public final JsonPatchBuilder add(String path, String value) {
        requireNonNull(path, "path");
        requireNonNull(path, "value");
        return append(new PatchOperation.Add(path, JsonValues.valueOf(value)));
    }

    @Override
    public final JsonPatchBuilder add(String path, int value) {
        requireNonNull(path, "path");
        return append(new PatchOperation.Add(path, JsonValues.valueOf(value)));
    }

    @Override
    public final JsonPatchBuilder add(String path, boolean value) {
        requireNonNull(path, "path");
        return append(new PatchOperation.Add(path, JsonValues.valueOf(value)));
    }

    @Override
    public final JsonPatchBuilder remove(String path) {
        requireNonNull(path, "path");
        return append(new PatchOperation.Remove(path));
    }

    @Override
    public final JsonPatchBuilder replace(String path, JsonValue value) {
        requireNonNull(path, "path");
        requireNonNull(path, "value");
        return append(new PatchOperation.Replace(path, value));
    }

    @Override
    public final JsonPatchBuilder replace(String path, String value) {
        requireNonNull(path, "path");
        requireNonNull(path, "value");
        return append(new PatchOperation.Replace(path, JsonValues.valueOf(value)));
    }

    @Override
    public final JsonPatchBuilder replace(String path, int value) {
        requireNonNull(path, "path");
        return append(new PatchOperation.Replace(path, JsonValues.valueOf(value)));
    }

    @Override
    public final JsonPatchBuilder replace(String path, boolean value) {
        requireNonNull(path, "path");
        return append(new PatchOperation.Replace(path, JsonValues.valueOf(value)));
    }

    @Override
    public final JsonPatchBuilder move(String path, String from) {
        requireNonNull(path, "path");
        requireNonNull(from, "from");
        return append(new PatchOperation.Move(path, from));
    }

    @Override
    public final JsonPatchBuilder copy(String path, String from) {
        requireNonNull(path, "path");
        requireNonNull(from, "from");
        return append(new PatchOperation.Copy(path, from));
    }

    @Override
    public final JsonPatchBuilder test(String path, JsonValue value) {
        requireNonNull(path, "path");
        requireNonNull(value, "value");
        return append(new PatchOperation.Test(path, value));
    }

    @Override
    public final JsonPatchBuilder test(String path, String value) {
        requireNonNull(path, "path");
        requireNonNull(value, "value");
        return append(new PatchOperation.Test(path, JsonValues.valueOf(value)));
    }

    @Override
    public final JsonPatchBuilder test(String path, int value) {
        requireNonNull(path, "path");
        return append(new PatchOperation.Test(path, JsonValues.valueOf(value)));
    }

    @Override
    public final JsonPatchBuilder test(String path, boolean value) {
        requireNonNull(path, "path");
        return append(new PatchOperation.Test(path, JsonValues.valueOf(value)));
    }

    @Override
    public final JsonPatch build() {
        JsonPatch patch = JsonPatchImpl.of(buildAsArray(), operations);
        this.operations = null;
        return patch;
    }

    private JsonPatchBuilder append(PatchOperation op) {
        if (operations == null) {
            operations = new ArrayList<>();
        }
        operations.add(op);
        return this;
    }

    private JsonArray buildAsArray() {
        if (operations == null) {
            return JsonValue.EMPTY_JSON_ARRAY;
        }
        JsonArrayBuilder arrayBuilder = new JsonArrayBuilderImpl();
        JsonObjectBuilder objectBuilder = new JsonObjectBuilderImpl();
        for (PatchOperation operation : operations) {
            JsonObject object = operation.toJsonObject(objectBuilder);
            arrayBuilder.add(object);
        }
        return arrayBuilder.build();
    }
}

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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonPatch.Operation;
import javax.json.JsonPointer;

/**
 * An operation which composes a JSON patch.
 *
 * @author leadpony
 */
interface PatchOperation {

    /**
     * Returns the operation enumerator of this operation.
     *
     * @return the operation enumerator.
     */
    Operation getOperation();

    /**
     * Returns the specified path for this operation.
     *
     * @return the specified path.
     */
    String getPath();

    /**
     * Returns the specified path for this operation as a JSON pointer.
     *
     * @return the specified path as a JSON pointer.
     */
    default ExtendedJsonPointer getPointer() {
        return JsonPointerImpl.parse(getPath());
    }

    /**
     * Applies this operation to the specified JSON array or object.
     *
     * @param <T> JSON array or object.
     * @param target the target JSON value.
     * @return JSON value after modification.
     */
    <T extends JsonStructure> T apply(T target);

    /**
     * Converts this operation into a JSON object.
     *
     * @param builder the builder for building a JSON object.
     * @return built JSON object.
     */
    JsonObject toJsonObject(JsonObjectBuilder builder);

    /**
     * A skeletal implementation of {@link PatchOperation}.
     *
     * @author leadpony
     */
    abstract class AbstractPatchOperation implements PatchOperation {

        private final String path;

        protected AbstractPatchOperation(String path) {
            this.path = path;
        }

        @Override
        public final String getPath() {
            return path;
        }

        @Override
        public JsonObject toJsonObject(JsonObjectBuilder builder) {
            builder
                .add("op", getOperation().operationName())
                .add("path", getPath());
            populateObject(builder);
            return builder.build();
        }

        protected void populateObject(JsonObjectBuilder builder) {
        }
    }

    /**
     * An operation of {@link Operation.ADD}.
     *
     * @author leadpony
     */
    class Add extends AbstractPatchOperation {

        private final JsonValue value;

        Add(String path, JsonValue value) {
            super(path);
            this.value = value;
        }

        @Override
        public Operation getOperation() {
            return Operation.ADD;
        }

        @Override
        public <T extends JsonStructure> T apply(T target) {
            return getPointer().add(target, value);
        }

        @Override
        protected void populateObject(JsonObjectBuilder builder) {
            builder.add("value", this.value);
        }
    }

    /**
     * An operation of {@link Operation.REMOVE}.
     *
     * @author leadpony
     */
    class Remove extends AbstractPatchOperation {

        Remove(String path) {
            super(path);
        }

        @Override
        public Operation getOperation() {
            return Operation.REMOVE;
        }

        @Override
        public <T extends JsonStructure> T apply(T target) {
            return getPointer().remove(target);
        }

        @Override
        public JsonObject toJsonObject(JsonObjectBuilder builder) {
            return builder.add("op", getOperation().operationName())
                .add("path", getPath())
                .build();
        }
    }

    /**
     * An operation of {@link Operation.REPLACE}.
     *
     * @author leadpony
     */
    class Replace extends AbstractPatchOperation {

        private final JsonValue value;

        Replace(String path, JsonValue value) {
            super(path);
            this.value = value;
        }

        @Override
        public Operation getOperation() {
            return Operation.REPLACE;
        }

        @Override
        public <T extends JsonStructure> T apply(T target) {
            return getPointer().replace(target, value);
        }

        @Override
        protected void populateObject(JsonObjectBuilder builder) {
            builder.add("value", this.value);
        }
    }

    /**
     * An operation of {@link Operation.MOVE}.
     *
     * @author leadpony
     */
    class Move extends AbstractPatchOperation {

        private final String from;

        Move(String path, String from) {
            super(path);
            this.from = from;
        }

        @Override
        public Operation getOperation() {
            return Operation.MOVE;
        }

        @Override
        public <T extends JsonStructure> T apply(T target) {
            ExtendedJsonPointer from = JsonPointerImpl.parse(this.from);
            ExtendedJsonPointer to = getPointer();
            if (to.startsWith(from)) {
                String message = Message.PATCH_ILLEGAL_MOVE_OPERATION.with(from, to);
                throw new JsonException(message);
            }
            JsonValue value = from.getValue(target);
            T removed = from.remove(target);
            return to.add(removed, value);
        }

        @Override
        protected void populateObject(JsonObjectBuilder builder) {
            builder.add("from", this.from);
        }
    }

    /**
     * An operation of {@link Operation.COPY}.
     *
     * @author leadpony
     */
    class Copy extends AbstractPatchOperation {

        private final String from;

        Copy(String path, String from) {
            super(path);
            this.from = from;
        }

        @Override
        public Operation getOperation() {
            return Operation.COPY;
        }

        @Override
        public <T extends JsonStructure> T apply(T target) {
            JsonPointer from = JsonPointerImpl.parse(this.from);
            JsonPointer to = getPointer();
            return to.add(target, from.getValue(target));
        }

        @Override
        protected void populateObject(JsonObjectBuilder builder) {
            builder.add("from", this.from);
        }
    }

    /**
     * An operation of {@link Operation.TEST}.
     *
     * @author leadpony
     */
    class Test extends AbstractPatchOperation {

        private final JsonValue value;

        Test(String path, JsonValue value) {
            super(path);
            this.value = value;
        }

        @Override
        public Operation getOperation() {
            return Operation.TEST;
        }

        @Override
        public <T extends JsonStructure> T apply(T target) {
            JsonValue actual = getPointer().getValue(target);
            if (!this.value.equals(actual)) {
                String message = Message.PATCH_TEST_FAILED.with(getPath());
                throw new JsonException(message);
            }
            return target;
        }

        @Override
        protected void populateObject(JsonObjectBuilder builder) {
            builder.add("value", this.value);
        }
    }

    /**
     * Converts a JSON array into a list of patch operations.
     *
     * @param array the JSON array to convert.
     * @return newly created list of patch operations.
     */
    static List<PatchOperation> asOperations(JsonArray array) {
        return array.stream()
            .map(JsonValue::asJsonObject)
            .map(PatchOperation::asOperation)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private static PatchOperation asOperation(JsonObject object) {
        if (object == null) {
            return null;
        }
        String op = object.getString("op");
        if (op == null) {
            return null;
        }
        switch (op) {
        case "add":
            return new Add(object.getString("path"), object.get("value"));
        case "remove":
            return new Remove(object.getString("path"));
        case "replace":
            return new Replace(object.getString("path"), object.get("value"));
        case "move":
            return new Move(object.getString("path"), object.getString("from"));
        case "copy":
            return new Copy(object.getString("path"), object.getString("from"));
        case "test":
            return new Test(object.getString("path"), object.get("value"));
        default:
            return null;
        }
    }
}

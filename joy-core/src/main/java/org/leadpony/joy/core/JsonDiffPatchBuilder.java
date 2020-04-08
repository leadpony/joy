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

import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

/**
 * A builder which builds a JSON patch from source and target JSON structures.
 *
 * @author leadpony
 */
final class JsonDiffPatchBuilder extends JsonPatchBuilderImpl {

    /**
     * Crates a JSON patch from source and target {@code JsonStrcture}.
     *
     * @param source the source {@code JsonStructure}.
     * @param target the target {@code JsonStructure}.
     * @return a JSON patch which yields the target when applied to the source.
     */
    static JsonPatch createDiff(JsonStructure source, JsonStructure target) {
        if (source == target) {
            return JsonPatchImpl.empty();
        }
        return new JsonDiffPatchBuilder().buildDiff(source, target);
    }

    private JsonDiffPatchBuilder() {
    }

    private JsonPatch buildDiff(JsonStructure source, JsonStructure target) {
        compare("", source, target);
        return build();
    }

    private void compare(String pointer, JsonValue source, JsonValue target) {
        ValueType sourceType = source.getValueType();
        ValueType targetType = target.getValueType();
        if (sourceType == targetType) {
            if (sourceType == ValueType.ARRAY) {
                compareArrays(pointer, source.asJsonArray(), target.asJsonArray());
            } else if (sourceType == ValueType.OBJECT) {
                compareObjects(pointer, source.asJsonObject(), target.asJsonObject());
            } else if (!source.equals(target)) {
                replace(pointer, target);
            }
        } else {
            replace(pointer, target);
        }
    }

    /**
     * Compares two arrays.
     *
     * <p>
     * We use the longest common sequence algorithm as the Reference Implementation
     * does.
     * </p>
     *
     * @param pointer the current JSON pointer.
     * @param source the source array.
     * @param target the target array.
     */
    private void compareArrays(String pointer, JsonArray source, JsonArray target) {
        int[][] lcs = lcs(source, target);
        addDiff(pointer, source, target, lcs, source.size(), target.size());
    }

    /**
     * Computes the longest common sequence between the given arrays.
     *
     * @param source the source array.
     * @param target the target array.
     * @return the matrix containing the longest common sequence.
     */
    private static int[][] lcs(JsonArray source, JsonArray target) {
        final int sourceSize = source.size();
        final int targetSize = target.size();
        int[][] lcs = new int[sourceSize + 1][targetSize + 1];
        for (int i = 0; i < sourceSize; i++) {
            for (int j = 0; j < targetSize; j++) {
                if (source.get(i).equals(target.get(j))) {
                    lcs[i + 1][j + 1] = lcs[i][j] + 1;
                } else {
                    lcs[i + 1][j + 1] = Math.max(lcs[i + 1][j], lcs[i][j + 1]);
                }
            }
        }
        return lcs;
    }

    private void addDiff(String pointer, JsonArray source, JsonArray target, int[][] lcs, int i, int j) {
        if (i == 0 && j == 0) {
            return;
        } else if (i > 0 && j > 0 && source.get(i - 1).equals(target.get(j - 1))) {
            addDiff(pointer, source, target, lcs, i - 1, j - 1);
        } else if (j > 0 && (i == 0 || lcs[i][j - 1] > lcs[i - 1][j])) {
            // Moves to left.
            addDiff(pointer, source, target, lcs, i, j - 1);
            add(concat(pointer, j - 1), target.get(j - 1));
        } else if (i > 0 && (j == 0 || lcs[i][j - 1] < lcs[i - 1][j])) {
            remove(concat(pointer, i - 1));
            // Moves to up.
            addDiff(pointer, source, target, lcs, i - 1, j);
        } else { // i > 0 && j > 0
            compare(concat(pointer, i - 1), source.get(i - 1), target.get(j - 1));
            addDiff(pointer, source, target, lcs, i - 1, j - 1);
        }
    }

    private void compareObjects(String pointer, JsonObject source, JsonObject target) {
        for (Map.Entry<String, JsonValue> entry : source.entrySet()) {
            String key = entry.getKey();
            if (target.containsKey(key)) {
                compare(concat(pointer, key), entry.getValue(), target.get(key));
            } else {
                remove(concat(pointer, key));
            }
        }
        for (Map.Entry<String, JsonValue> entry : target.entrySet()) {
            String key = entry.getKey();
            if (!source.containsKey(key)) {
                add(concat(pointer, key), entry.getValue());
            }
        }
    }

    private static String concat(String pointer, int index) {
        StringBuilder b = new StringBuilder(pointer);
        b.append('/').append(index);
        return b.toString();
    }

    private static String concat(String pointer, String key) {
        final int initialCapacity = pointer.length() + 1 + key.length();
        StringBuilder b = new StringBuilder(initialCapacity);
        b.append(pointer).append('/');
        int last = 0;
        final int len = key.length();
        for (int i = 0; i < len; i++) {
            char c = key.charAt(i);
            if (c == '~' || c == '/') {
                if (last < i) {
                    b.append(key, last, i);
                }
                if (c == '~') {
                    b.append('~').append('0');
                } else {
                    b.append('~').append('1');
                }
                last = i + 1;
            }
        }
        if (last < len) {
            b.append(key, last, len);
        }
        return b.toString();
    }
}

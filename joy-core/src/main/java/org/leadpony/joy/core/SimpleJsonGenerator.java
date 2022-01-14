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

import static org.leadpony.joy.core.Preconditions.requireFiniteNumber;
import static org.leadpony.joy.core.Preconditions.requireNonNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.stream.JsonGenerationException;

import org.leadpony.joy.api.JsonGenerator;

/**
 * @author leadpony
 */
class SimpleJsonGenerator extends JsonStringBuilder implements JsonGenerator {

    private final Deque<State> stateStack = new ArrayDeque<>();
    private State state;
    private final boolean valueStream;

    SimpleJsonGenerator() {
        this.state = State.INITIAL;
        this.valueStream = false;
    }

    SimpleJsonGenerator(char[] buffer, boolean valueStream) {
        super(buffer);
        this.state = State.INITIAL;
        this.valueStream = valueStream;
    }

    @Override
    public JsonGenerator writeStartObject() {
        state = state.writeStartObject(this);
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        requireNonNull(name, "name");
        state = state.writeStartObject(this, name);
        return this;
    }

    @Override
    public JsonGenerator writeKey(String name) {
        requireNonNull(name, "name");
        state = state.writeKey(this, name);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        state = state.writeStartArray(this);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        requireNonNull(name, "name");
        state = state.writeStartArray(this, name);
        return this;
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        requireNonNull(name, "name");
        requireNonNull(value, "value");
        state = state.write(this, name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, String value) {
        requireNonNull(name, "name");
        requireNonNull(value, "value");
        state = state.write(this, name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        requireNonNull(name, "name");
        requireNonNull(value, "value");
        state = state.write(this, name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        requireNonNull(name, "name");
        requireNonNull(value, "value");
        state = state.write(this, name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, int value) {
        requireNonNull(name, "name");
        state = state.write(this, name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, long value) {
        requireNonNull(name, "name");
        state = state.write(this, name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, double value) {
        requireNonNull(name, "name");
        requireFiniteNumber(value);
        state = state.write(this, name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        requireNonNull(name, "name");
        state = state.write(this, name, value);
        return this;
    }

    @Override
    public JsonGenerator writeNull(String name) {
        requireNonNull(name, "name");
        state = state.writeNull(this, name);
        return this;
    }

    @Override
    public JsonGenerator writeEnd() {
        state = state.writeEnd(this);
        return this;
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        requireNonNull(value, "value");
        state = state.write(this, value);
        return this;
    }

    @Override
    public JsonGenerator write(String value) {
        requireNonNull(value, "value");
        state = state.write(this, value);
        return this;
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        requireNonNull(value, "value");
        state = state.write(this, value);
        return this;
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        requireNonNull(value, "value");
        state = state.write(this, value);
        return this;
    }

    @Override
    public JsonGenerator write(int value) {
        state = state.write(this, value);
        return this;
    }

    @Override
    public JsonGenerator write(long value) {
        state = state.write(this, value);
        return this;
    }

    @Override
    public JsonGenerator write(double value) {
        requireFiniteNumber(value);
        state = state.write(this, value);
        return this;
    }

    @Override
    public JsonGenerator write(boolean value) {
        state = state.write(this, value);
        return this;
    }

    @Override
    public JsonGenerator writeNull() {
        state = state.writeNull(this);
        return this;
    }

    @Override
    public void close() {
        if (valueStream) {
            if (state != State.INITIAL && state != State.NEXT_VALUE) {
                throw newJsonGenerationException(Message.thatGeneratorIsNotCompleted());
            }
        } else {
            if (state != State.FINAL) {
                throw newJsonGenerationException(Message.thatGeneratorIsNotCompleted());
            }
        }
    }

    @Override
    public void flush() {
    }

    final void pushState(State state) {
        stateStack.push(state);
    }

    final State popState() {
        return stateStack.pop();
    }

    final void appendKey(String name) {
        append('"');
        append(name);
        append('"');
        appendColon();
    }

    final void appendValue(JsonValue value) {
        switch (value.getValueType()) {
            case ARRAY:
                appendArray((JsonArray) value);
                break;
            case OBJECT:
                appendObject((JsonObject) value);
                break;
            case STRING:
                appendValue(((JsonString) value).getString());
                break;
            case NUMBER:
                append(value.toString());
                break;
            case TRUE:
                append("true");
                break;
            case FALSE:
                append("false");
                break;
            case NULL:
                appendNull();
                break;
            default:
                break;
        }
    }

    final void appendArray(JsonArray array) {
        appendOpeningBracket('[');
        Iterator<JsonValue> it = array.iterator();
        if (it.hasNext()) {
            appendBreak();
            appendValue(it.next());
            while (it.hasNext()) {
                appendComma();
                appendValue(it.next());
            }
        }
        appendClosingBracket(']');
    }

    final void appendObject(JsonObject object) {
        appendOpeningBracket('{');
        Iterator<Map.Entry<String, JsonValue>> it = object.entrySet().iterator();
        if (it.hasNext()) {
            Map.Entry<String, JsonValue> entry = it.next();
            appendBreak();
            appendKey(entry.getKey());
            appendValue(entry.getValue());
            while (it.hasNext()) {
                entry = it.next();
                appendComma();
                appendKey(entry.getKey());
                appendValue(entry.getValue());
            }
        }
        appendClosingBracket('}');
    }

    final void appendValue(String value) {
        append('"');
        append(value);
        append('"');
    }

    protected void appendOpeningBracket(char c) {
        append(c);
    }

    protected void appendClosingBracket(char c) {
        append(c);
    }

    protected void appendBreak() {
    }

    protected void appendComma() {
        append(',');
    }

    protected void appendColon() {
        append(':');
    }

    protected void appendSpace() {
        append(' ');
    }

    static JsonException newJsonException(String message, IOException e) {
        return new JsonException(message, e);
    }

    static JsonGenerationException newJsonGenerationException(String message) {
        return new JsonGenerationException(message);
    }

    /**
     * A state of this generator.
     *
     * @author leadpony
     */
    enum State {
        INITIAL(Message::thatIllegalGeneratorMethodWasCalledBeforeAll) {

            @Override
            State writeStartObject(SimpleJsonGenerator g) {
                g.appendOpeningBracket('{');
                g.pushState(g.valueStream ? NEXT_VALUE : FINAL);
                return START_OBJECT;
            }

            @Override
            State writeStartArray(SimpleJsonGenerator g) {
                g.appendOpeningBracket('[');
                g.pushState(g.valueStream ? NEXT_VALUE : FINAL);
                return START_ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, JsonValue value) {
                g.appendValue(value);
                return g.valueStream ? NEXT_VALUE : FINAL;
            }

            @Override
            State write(SimpleJsonGenerator g, String value) {
                g.appendValue(value);
                return g.valueStream ? NEXT_VALUE : FINAL;
            }

            @Override
            State write(SimpleJsonGenerator g, BigDecimal value) {
                g.append(value);
                return g.valueStream ? NEXT_VALUE : FINAL;
            }

            @Override
            State write(SimpleJsonGenerator g, BigInteger value) {
                g.append(value);
                return g.valueStream ? NEXT_VALUE : FINAL;
            }

            @Override
            State write(SimpleJsonGenerator g, int value) {
                g.append(value);
                return g.valueStream ? NEXT_VALUE : FINAL;
            }

            @Override
            State write(SimpleJsonGenerator g, long value) {
                g.append(value);
                return g.valueStream ? NEXT_VALUE : FINAL;
            }

            @Override
            State write(SimpleJsonGenerator g, double value) {
                g.append(value);
                return g.valueStream ? NEXT_VALUE : FINAL;
            }

            @Override
            State write(SimpleJsonGenerator g, boolean value) {
                g.append(value);
                return g.valueStream ? NEXT_VALUE : FINAL;
            }

            @Override
            State writeNull(SimpleJsonGenerator g) {
                g.appendNull();
                return g.valueStream ? NEXT_VALUE : FINAL;
            }
        },
        NEXT_VALUE(Message::thatIllegalGeneratorMethodWasCalledBeforeAll) {
            @Override
            State writeStartObject(SimpleJsonGenerator g) {
                g.appendOpeningBracket('{');
                g.pushState(NEXT_VALUE);
                return START_OBJECT;
            }

            @Override
            State writeStartArray(SimpleJsonGenerator g) {
                g.appendOpeningBracket('[');
                g.pushState(NEXT_VALUE);
                return START_ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, JsonValue value) {
                if (!value.getValueType().equals(ValueType.OBJECT)
                        && !value.getValueType().equals(ValueType.ARRAY)) {
                    g.appendSpace();
                }

                g.appendValue(value);
                return NEXT_VALUE;
            }

            @Override
            State write(SimpleJsonGenerator g, String value) {
                g.appendSpace();
                g.appendValue(value);
                return NEXT_VALUE;
            }

            @Override
            State write(SimpleJsonGenerator g, BigDecimal value) {
                g.appendSpace();
                g.append(value);
                return NEXT_VALUE;
            }

            @Override
            State write(SimpleJsonGenerator g, BigInteger value) {
                g.appendSpace();
                g.append(value);
                return NEXT_VALUE;
            }

            @Override
            State write(SimpleJsonGenerator g, int value) {
                g.appendSpace();
                g.append(value);
                return NEXT_VALUE;
            }

            @Override
            State write(SimpleJsonGenerator g, long value) {
                g.appendSpace();
                g.append(value);
                return NEXT_VALUE;
            }

            @Override
            State write(SimpleJsonGenerator g, double value) {
                g.appendSpace();
                g.append(value);
                return NEXT_VALUE;
            }

            @Override
            State write(SimpleJsonGenerator g, boolean value) {
                g.appendSpace();
                g.append(value);
                return NEXT_VALUE;
            }

            @Override
            State writeNull(SimpleJsonGenerator g) {
                g.appendSpace();
                g.appendNull();
                return NEXT_VALUE;
            }
        },
        FINAL(Message::thatIllegalGeneratorMethodWasCalledAfterAll) {
        },
        START_ARRAY(Message::thatIllegalGeneratorMethodWasCalledAfterArrayStart) {

            @Override
            State writeStartObject(SimpleJsonGenerator g) {
                g.appendBreak();
                g.appendOpeningBracket('{');
                g.pushState(ARRAY);
                return START_OBJECT;
            }

            @Override
            State writeStartArray(SimpleJsonGenerator g) {
                g.appendBreak();
                g.appendOpeningBracket('[');
                g.pushState(ARRAY);
                return START_ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, JsonValue value) {
                g.appendBreak();
                g.appendValue(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, String value) {
                g.appendBreak();
                g.appendValue(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, BigDecimal value) {
                g.appendBreak();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, BigInteger value) {
                g.appendBreak();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, int value) {
                g.appendBreak();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, long value) {
                g.appendBreak();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, double value) {
                g.appendBreak();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, boolean value) {
                g.appendBreak();
                g.append(value);
                return ARRAY;
            }

            @Override
            State writeNull(SimpleJsonGenerator g) {
                g.appendBreak();
                g.appendNull();
                return ARRAY;
            }

            @Override
            State writeEnd(SimpleJsonGenerator g) {
                g.appendClosingBracket(']');
                return g.popState();
            }
        },
        ARRAY(Message::thatIllegalGeneratorMethodWasCalledAfterArrayItem) {

            @Override
            State writeStartObject(SimpleJsonGenerator g) {
                g.appendComma();
                g.appendOpeningBracket('{');
                g.pushState(this);
                return START_OBJECT;
            }

            @Override
            State writeStartArray(SimpleJsonGenerator g) {
                g.appendComma();
                g.appendOpeningBracket('[');
                g.pushState(this);
                return START_ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, JsonValue value) {
                g.appendComma();
                g.appendValue(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, String value) {
                g.appendComma();
                g.appendValue(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, BigDecimal value) {
                g.appendComma();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, BigInteger value) {
                g.appendComma();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, int value) {
                g.appendComma();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, long value) {
                g.appendComma();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, double value) {
                g.appendComma();
                g.append(value);
                return ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, boolean value) {
                g.appendComma();
                g.append(value);
                return ARRAY;
            }

            @Override
            State writeNull(SimpleJsonGenerator g) {
                g.appendComma();
                g.appendNull();
                return ARRAY;
            }

            @Override
            State writeEnd(SimpleJsonGenerator g) {
                g.appendClosingBracket(']');
                return g.popState();
            }
        },
        START_OBJECT(Message::thatIllegalGeneratorMethodWasCalledAfterObjectStart) {

            @Override
            State writeStartObject(SimpleJsonGenerator g, String name) {
                g.appendBreak();
                g.appendKey(name);
                g.appendOpeningBracket('{');
                g.pushState(OBJECT);
                return START_OBJECT;
            }

            @Override
            State writeKey(SimpleJsonGenerator g, String name) {
                g.appendBreak();
                g.appendKey(name);
                return KEY_NAME;
            }

            @Override
            State writeStartArray(SimpleJsonGenerator g, String name) {
                g.appendBreak();
                g.appendKey(name);
                g.appendOpeningBracket('[');
                g.pushState(OBJECT);
                return START_ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, JsonValue value) {
                g.appendBreak();
                g.appendKey(name);
                g.appendValue(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, String value) {
                g.appendBreak();
                g.appendKey(name);
                g.appendValue(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, BigDecimal value) {
                g.appendBreak();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, BigInteger value) {
                g.appendBreak();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, int value) {
                g.appendBreak();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, long value) {
                g.appendBreak();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, double value) {
                g.appendBreak();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, boolean value) {
                g.appendBreak();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State writeNull(SimpleJsonGenerator g, String name) {
                g.appendBreak();
                g.appendKey(name);
                g.appendNull();
                return OBJECT;
            }

            @Override
            State writeEnd(SimpleJsonGenerator g) {
                g.appendClosingBracket('}');
                return g.popState();
            }
        },
        KEY_NAME(Message::thatIllegalGeneratorMethodWasCalledAfterPropertyKey) {

            @Override
            State writeStartObject(SimpleJsonGenerator g) {
                g.appendOpeningBracket('{');
                g.pushState(OBJECT);
                return START_OBJECT;
            }

            @Override
            State writeStartArray(SimpleJsonGenerator g) {
                g.appendOpeningBracket('[');
                g.pushState(OBJECT);
                return START_ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, JsonValue value) {
                g.appendValue(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String value) {
                g.appendValue(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, BigDecimal value) {
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, BigInteger value) {
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, int value) {
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, long value) {
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, double value) {
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, boolean value) {
                g.append(value);
                return OBJECT;
            }

            @Override
            State writeNull(SimpleJsonGenerator g) {
                g.appendNull();
                return OBJECT;
            }
        },
        OBJECT(Message::thatIllegalGeneratorMethodWasCalledAfterPropertyValue) {
            @Override
            State writeStartObject(SimpleJsonGenerator g, String name) {
                g.appendComma();
                g.appendKey(name);
                g.appendOpeningBracket('{');
                g.pushState(this);
                return START_OBJECT;
            }

            @Override
            State writeKey(SimpleJsonGenerator g, String name) {
                g.appendComma();
                g.appendKey(name);
                return KEY_NAME;
            }

            @Override
            State writeStartArray(SimpleJsonGenerator g, String name) {
                g.appendComma();
                g.appendKey(name);
                g.appendOpeningBracket('[');
                g.pushState(this);
                return START_ARRAY;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, JsonValue value) {
                g.appendComma();
                g.appendKey(name);
                g.appendValue(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, String value) {
                g.appendComma();
                g.appendKey(name);
                g.appendValue(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, BigDecimal value) {
                g.appendComma();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, BigInteger value) {
                g.appendComma();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, int value) {
                g.appendComma();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, long value) {
                g.appendComma();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, double value) {
                g.appendComma();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State write(SimpleJsonGenerator g, String name, boolean value) {
                g.appendComma();
                g.appendKey(name);
                g.append(value);
                return OBJECT;
            }

            @Override
            State writeNull(SimpleJsonGenerator g, String name) {
                g.appendComma();
                g.appendKey(name);
                g.appendNull();
                return OBJECT;
            }

            @Override
            State writeEnd(SimpleJsonGenerator g) {
                g.appendClosingBracket('}');
                return g.popState();
            }
        };

        private final Function<String, String> message;

        State(Function<String, String> message) {
            this.message = message;
        }

        State writeStartObject(SimpleJsonGenerator g) {
            throw newJsonGenerationException("writeStartObject()");
        }

        State writeStartObject(SimpleJsonGenerator g, String name) {
            throw newJsonGenerationException("writeStartObject(String)");
        }

        State writeKey(SimpleJsonGenerator g, String name) {
            throw newJsonGenerationException("writeKey(String)");
        }

        State writeStartArray(SimpleJsonGenerator g) {
            throw newJsonGenerationException("writeStartArray()");
        }

        State writeStartArray(SimpleJsonGenerator g, String name) {
            throw newJsonGenerationException("writeStartArray(String)");
        }

        State write(SimpleJsonGenerator g, String name, JsonValue value) {
            throw newJsonGenerationException("write(String,JsonValue)");
        }

        State write(SimpleJsonGenerator g, String name, String value) {
            throw newJsonGenerationException("write(String,String)");
        }

        State write(SimpleJsonGenerator g, String name, BigDecimal value) {
            throw newJsonGenerationException("write(String,BigDecimal)");
        }

        State write(SimpleJsonGenerator g, String name, BigInteger value) {
            throw newJsonGenerationException("write(String,BigInteger)");
        }

        State write(SimpleJsonGenerator g, String name, int value) {
            throw newJsonGenerationException("write(String,int)");
        }

        State write(SimpleJsonGenerator g, String name, long value) {
            throw newJsonGenerationException("write(String,long)");
        }

        State write(SimpleJsonGenerator g, String name, double value) {
            throw newJsonGenerationException("write(String,double)");
        }

        State write(SimpleJsonGenerator g, String name, boolean value) {
            throw newJsonGenerationException("write(String,boolean)");
        }

        State writeNull(SimpleJsonGenerator g, String name) {
            throw newJsonGenerationException("writeNull(String)");
        }

        State writeEnd(SimpleJsonGenerator g) {
            throw newJsonGenerationException("writeEnd()");
        }

        State write(SimpleJsonGenerator g, JsonValue value) {
            throw newJsonGenerationException("write(JsonValue)");
        }

        State write(SimpleJsonGenerator g, String value) {
            throw newJsonGenerationException("write(String)");
        }

        State write(SimpleJsonGenerator g, BigDecimal value) {
            throw newJsonGenerationException("write(BigDecimal)");
        }

        State write(SimpleJsonGenerator g, BigInteger value) {
            throw newJsonGenerationException("write(BigDecimal)");
        }

        State write(SimpleJsonGenerator g, int value) {
            throw newJsonGenerationException("write(int)");
        }

        State write(SimpleJsonGenerator g, long value) {
            throw newJsonGenerationException("write(long)");
        }

        State write(SimpleJsonGenerator g, double value) {
            throw newJsonGenerationException("write(double)");
        }

        State write(SimpleJsonGenerator g, boolean value) {
            throw newJsonGenerationException("write(boolean)");
        }

        State writeNull(SimpleJsonGenerator g) {
            throw newJsonGenerationException("writeNull()");
        }

        protected final JsonGenerationException newJsonGenerationException(String method) {
            return new JsonGenerationException(message.apply(method));
        }
    }
}

/*
 * Copyright 2020 the original author or authors.
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

package org.leadpony.joy.yaml.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.leadpony.jsonp.testsuite.helper.LoggerFactory;

import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import jakarta.json.stream.JsonParsingException;

/**
 * @author leadpony
 */
public abstract class AbstractJsonParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJsonParserTest.class);

    @ParameterizedTest
    @EnumSource(SimpleYamlTestCase.class)
    public void nextShouldReturnExpectedEventsFromReader(SimpleYamlTestCase test) {
        var actual = new ArrayList<Event>();

        try (JsonParser parser = createParser(test.json)) {
            while (parser.hasNext()) {
                actual.add(parser.next());
            }
        }

        assertThat(actual).containsExactly(test.events);
    }

    @ParameterizedTest
    @EnumSource(IllFormedYamlTestCase.class)
    public void nextShouldThrowParsingException(IllFormedYamlTestCase test) {
        Throwable thrown = null;

        try (JsonParser parser = createParser(test.yaml)) {
            int iterations = test.iterations;
            while (--iterations > 0) {
                parser.next();
            }

            thrown = catchThrowable(() -> {
                parser.next();
            });
        }

        LOG.info(thrown.getMessage());

        assertThat(thrown).isNotNull().isInstanceOf(JsonParsingException.class);
    }

    protected abstract JsonParser createParser(String json);

    public enum StringTestCase {
        DOUBLE_QUOTED_UNICIDE(
                "\"Sosa did fine.\\u263A\"",
                "Sosa did fine.\u263A"
                ),

        DOUBLE_QUOTED_CONTROL(
                "\"\\b1998\\t1999\\t2000\\n\"",
                "\b1998\t1999\t2000\n"
                ),

        DOUBLE_QUOTED_HEX_ESCAPE(
                "\"\\x0d\\x0a is \\r\\n\"",
                "\r\n is \r\n"
                ),

        SINGLE_QUOTED(
                "'\"Howdy!\" he cried.'",
                "\"Howdy!\" he cried."
                ),

        SINGLE_QUOTED_COMMENT(
                "' # Not a ''comment''.'",
                " # Not a 'comment'."
                ),

        TIE_FIGHTER(
                "'|\\-*-/|'",
                "|\\-*-/|"),

        MULTI_LINE_FLOW_PLAIN(
                """
                This unquoted scalar
                spans many lines.
                """,
                "This unquoted scalar spans many lines."
                ),

        MULTI_LINE_FLOW_QUOTED(
                """
                "So does this
                quoted scalar.\\n"
                """,
                "So does this quoted scalar.\n"
                );

        final String json;
        final String expected;

        StringTestCase(String json, String expected) {
            this.json = json;
            this.expected = expected;
        }
    }

    @ParameterizedTest
    @EnumSource(StringTestCase.class)
    public void getStringShouldReturnExpectedValue(StringTestCase test) {
        String actual = null;

        try (JsonParser parser = createParser(test.json)) {
            parser.next();
            actual = parser.getString();
        }

        assertThat(actual).isEqualTo(test.expected);
    }

    public enum BigDecimalTestCase {
        ZERO("0", BigDecimal.ZERO),
        NEGATIVE_ZERO("-0", BigDecimal.ZERO),
        ONE("1", BigDecimal.ONE),
        POSITIVE_INTEGER("3", BigDecimal.valueOf(3)),
        NEGATIVE_INTEGER("-19", BigDecimal.valueOf(-19)),
        SCIENTIFIC("12e03", BigDecimal.valueOf(12, -3)),
        NEGATIVE_SCIENTIFIC("-2E+05", BigDecimal.valueOf(-2, -5));

        final String json;
        final BigDecimal expected;

        BigDecimalTestCase(String json, BigDecimal expected) {
            this.json = json;
            this.expected = expected;
        }
    }

    @ParameterizedTest
    @EnumSource(BigDecimalTestCase.class)
    public void getBigDecimalShouldReturnExpectedValue(BigDecimalTestCase test) {
        BigDecimal actual = null;

        try (JsonParser parser = createParser(test.json)) {
            parser.next();
            actual = parser.getBigDecimal();
        }

        assertThat(actual).isEqualTo(test.expected);
    }

    public enum IntegralNumberTestCase {
        INTEGER("365", true),
        FRACTIONAL_PART("123.00", false),
        PI("3.14", false);

        final String json;
        final boolean expected;

        IntegralNumberTestCase(String json, boolean expected) {
            this.json = json;
            this.expected = expected;
        }
    }

    @ParameterizedTest
    @EnumSource(IntegralNumberTestCase.class)
    public void isIntegralNumberShouldReturnExpectedResult(IntegralNumberTestCase test) {
        Boolean actual = null;

        try (JsonParser parser = createParser(test.json)) {
            parser.next();
            actual = parser.isIntegralNumber();
        }

        assertThat(actual).isEqualTo(test.expected);
    }
}

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

import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import jakarta.json.JsonException;

/**
 * A factory of input stream readers.
 *
 * @author leadpony
 */
final class StreamReaders {

    /**
     * UTF-32 Big Endian.
     */
    private static final Charset UTF_32BE = Charset.forName("UTF-32BE");

    /**
     * UTF-32 Little Endian.
     */
    private static final Charset UTF_32LE = Charset.forName("UTF-32LE");

    /**
     * The default character encoding.
     */
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    /**
     * Creates a reader which will read from the specified input stream. The The
     * character encoding of the input will be detected according to the logic
     * described in RFC 4627.
     *
     * @param in the original input source.
     * @return newly create reader.
     *
     * @see <a href="https://www.ietf.org/rfc/rfc4627.txt">RFC 4627</a>
     */
    static Reader createStreamReader(InputStream in) {
        try {
            int b1 = in.read();
            if (b1 < 0) {
                // empty
                return createReader(in, DEFAULT_ENCODING);
            }

            int b2 = in.read();
            if (b2 < 0) {
                // 1 letter
                return createReader(in, DEFAULT_ENCODING, (byte) b1);
            }

            // 2 letters or more

            if (b1 == 0xfe && b2 == 0xff) {
                // UTF-16BE with BOM
                return createReader(in, UTF_16BE);
            }

            int b3 = in.read();
            if (b3 < 0) {
                // 2 letters
                if (b1 == 0xff && b2 == 0xfe) {
                    return createReader(in, UTF_16LE);
                } else {
                    if (b1 == 0) {
                        return createReader(in, UTF_16BE, (byte) b1, (byte) b2);
                    } else if (b2 == 0) {
                        return createReader(in, UTF_16LE, (byte) b1, (byte) b2);
                    }
                    return createReader(in, DEFAULT_ENCODING, (byte) b1, (byte) b2);
                }
            }

            // 3 letters or more

            if (b1 == 0xef && b2 == 0xbb && b3 == 0xbf) {
                // UTF-8 with BOM
                return createReader(in, UTF_8);
            }

            int b4 = in.read();
            if (b4 < 0) {
                // 3 letters
                return createReader(in, DEFAULT_ENCODING, (byte) b1, (byte) b2, (byte) b3);
            }

            // 4 letters or more

            if (b1 == 0 && b2 == 0 && b3 == 0xfe && b4 == 0xff) {
                // UTF-32BE with BOM
                return createReader(in, UTF_32BE);
            } else if (b1 == 0xff && b2 == 0xfe && b3 == 0 && b4 == 0) {
                // UTF-32LE with BOM
                return createReader(in, UTF_32LE);
            } else if (b1 == 0xff && b2 == 0xfe) {
                // UTF-16LE with BOM
                return createReader(in, UTF_16LE, (byte) b3, (byte) b4);
            }

            if (b1 == 0 && b3 == 0) {
                if (b2 == 0) {
                    return createReader(in, UTF_32BE, (byte) b1, (byte) b2, (byte) b3, (byte) b4);
                } else {
                    return createReader(in, UTF_16BE, (byte) b1, (byte) b2, (byte) b3, (byte) b4);
                }
            } else if (b2 == 0 && b4 == 0) {
                if (b3 == 0) {
                    return createReader(in, UTF_32LE, (byte) b1, (byte) b2, (byte) b3, (byte) b4);
                } else {
                    return createReader(in, UTF_16LE, (byte) b1, (byte) b2, (byte) b3, (byte) b4);
                }
            }

            return createReader(in, DEFAULT_ENCODING, (byte) b1, (byte) b2, (byte) b3, (byte) b4);

        } catch (IOException e) {
            throw new JsonException(Message.PARSER_IO_ERROR_WHILE_READING.toString(), e);
        }
    }

    private static Reader createReader(InputStream in, Charset charset) {
        return new InputStreamReader(in, charset);
    }

    private static Reader createReader(InputStream in, Charset charset, byte... bytes) throws IOException {
        PushbackInputStream s = new PushbackInputStream(in, bytes.length);
        s.unread(bytes);
        return new InputStreamReader(s, charset);
    }

    private StreamReaders() {
    }
}

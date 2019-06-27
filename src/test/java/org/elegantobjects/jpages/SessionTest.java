/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2019 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.elegantobjects.jpages;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public final class SessionTest {

    @Test
    public void testWorks() throws Exception {
        org.elegantobjects.jpages.Session session = new org.elegantobjects.jpages.Session(
            new org.elegantobjects.jpages.App.Resource() {
                @Override
                public org.elegantobjects.jpages.App.Resource refine(final String name, final String value) {
                    return this;
                }
                @Override
                public void print(final org.elegantobjects.jpages.App.Output output) {
                    output.print("Content-Type", "text/plain");
                    output.print("Content-Length", "13");
                    output.print("X-Body", "Hello, world!");
                }
            }
        );
        String response = session.response("GET / HTTP/1.1\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 200 OK\r\n"));
    }

}

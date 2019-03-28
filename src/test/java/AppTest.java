/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.webinar;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public final class AppTest {

    class TextResource implements App.Resource {
        private final String body;
        public TextResource(String text) {
            this.body = text;
        }
        @Override
        public App.Resource refine(final String name, final String value) {
            return this;
        }
        @Override
        public void print(final App.Output output) {
            output.print("Content-Type", "text/plain");
            output.print("Content-Length", Integer.toString(this.body.length()));
            output.print("X-Body", this.body);
        }
    }

    @Test
    public void testWorks() throws Exception {
        int port = 12345;
        final Thread thread = new Thread(
            () -> {
                App app = new App(
                    new App.Resource() {
                        @Override
                        public App.Resource refine(final String name, final String value) {
                            if (!name.equals("X-Query")) {
                                return this;
                            }

                            if (value.equals("/")) {
                                return new TextResource("Hello, world!");
                            } else if (value.equals("/balance")) {
                                return new TextResource("256");
                            } else if (value.equals("/id")) {
                                return new TextResource("yegor");
                            } else {
                                return new TextResource("Not found!");
                            }
                        }
                        @Override
                        public void print(final App.Output output) {
                            output.print("X-Body", "Not found");
                        }
                    }
                );
                try {
                    app.start(port);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    throw new IllegalStateException(ex);
                }
            }
        );
        thread.setDaemon(true);
        thread.start();
        for (int attempt = 0; attempt < 10; ++attempt) {
            String response = new JdkRequest("http://localhost:" + port)
                .fetch().as(RestResponse.class).body();
            MatcherAssert.assertThat(response, Matchers.containsString("Hello, world!"));
        }
        MatcherAssert.assertThat(
            new JdkRequest("http://localhost:" + port + "/balance").fetch().as(RestResponse.class).body(),
            Matchers.equalTo("256")
        );
        MatcherAssert.assertThat(
            new JdkRequest("http://localhost:" + port + "/id").fetch().as(RestResponse.class).body(),
            Matchers.equalTo("yegor")
        );
        thread.interrupt();
        thread.join();
    }

}

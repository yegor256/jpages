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

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * The test of the App.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @since 0.1
 */
public final class AppTest {

    @Test
    public void testWorks() throws Exception {
        final int port = 12345;
        final Thread thread = new Thread(
            () -> {
                final App app = new App(
                    new Page() {
                        @Override
                        public Page with(final String name, final String value) {
                            if (!"X-Query".equals(name)) {
                                return this;
                            }
                            if ("/".equals(value)) {
                                return new TextPage("Hello, world!");
                            }
                            if ("/balance".equals(value)) {
                                return new TextPage("256");
                            }
                            if ("/id".equals(value)) {
                                return new TextPage("yegor");
                            }
                            return new TextPage("Not found!");
                        }
                        @Override
                        public Output via(final Output output) {
                            return output.with("X-Body", "Not found");
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
            final String response = new JdkRequest("http://localhost:" + port)
                .fetch().as(RestResponse.class).body();
            MatcherAssert.assertThat(
                response,
                Matchers.containsString("Hello, world!")
            );
        }
        MatcherAssert.assertThat(
            new JdkRequest("http://localhost:" + port + "/balance")
                .fetch().as(RestResponse.class).body(),
            Matchers.equalTo("256")
        );
        MatcherAssert.assertThat(
            new JdkRequest("http://localhost:" + port + "/id")
                .fetch().as(RestResponse.class).body(),
            Matchers.equalTo("yegor")
        );
        thread.interrupt();
        thread.join();
    }

}

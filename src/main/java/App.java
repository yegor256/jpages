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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class App extends IOException {

    interface Resource {
        Resource refine(String name, String value);
        void print(Output output);
    }

    interface Output {
        void print(String name, String value);
    }

    private final Resource resource;

    public App(Resource res) {
        this.resource = res;
    }

    public static class StringBuilderOutput implements Output {
        private final StringBuilder buffer;
        StringBuilderOutput(StringBuilder buf) {
            this.buffer = buf;
        }
        @Override
        public void print(final String name, final String value) {
            if (this.buffer.length() == 0) {
                this.buffer.append("HTTP/1.1 200 OK\r\n");
            }
            if (name.equals("X-Body")) {
                this.buffer.append("\r\n").append(value);
            } else {
                this.buffer.append(name).append(": ").append(value).append("\r\n");
            }
        }
    }

    void start(int port) throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            server.setSoTimeout(1000);
            while (true) {
                try (Socket socket = server.accept()) {
                    try (InputStream input = socket.getInputStream();
                         OutputStream output = socket.getOutputStream()) {
                        byte[] buffer = new byte[10000];
                        int total = input.read(buffer);
                        output.write(
                            new Session(this.resource).response(
                                new String(Arrays.copyOfRange(buffer, 0, total))
                            ).getBytes()
                        );
                    }
                } catch (SocketTimeoutException ex) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                }
            }
        }
    }

}

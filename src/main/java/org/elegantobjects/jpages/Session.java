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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Session {

    private final org.elegantobjects.jpages.App.Resource resource;

    Session(org.elegantobjects.jpages.App.Resource res) {
        this.resource = res;
    }

    String response(String request) throws IOException {
        Map<String, String> pairs = new HashMap<>();
        String[] lines = request.split("\r\n");
        for (int idx = 1; idx < lines.length; ++idx) {
            String[] parts = lines[idx].split(":");
            pairs.put(parts[0].trim(), parts[1].trim());
        }
        String[] parts = lines[0].split(" ");
        pairs.put("X-Method", parts[0]);
        pairs.put("X-Query", parts[1]);
        pairs.put("X-Protocol", parts[2]);
        org.elegantobjects.jpages.App.Resource res = this.resource;
        for (Map.Entry<String, String> pair : pairs.entrySet()) {
            res = res.refine(pair.getKey(), pair.getValue());
        }
        final StringBuilder buf = new StringBuilder();
        res.print(new org.elegantobjects.jpages.App.StringBuilderOutput(buf));
        return buf.toString();
    }

}

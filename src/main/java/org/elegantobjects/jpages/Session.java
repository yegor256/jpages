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

import java.util.HashMap;
import java.util.Map;

/**
 * The session.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @since 0.1
 */
public class Session {

    private final Page page;

    Session(final Page pge) {
        this.page = pge;
    }

    final Page with(final String request) {
        final Map<String, String> pairs = new HashMap<>(0);
        final String[] lines = request.split("\r\n");
        for (int idx = 1; idx < lines.length; ++idx) {
            final String[] parts = lines[idx].split(":");
            pairs.put(parts[0].trim(), parts[1].trim());
        }
        final String[] parts = lines[0].split(" ");
        pairs.put("X-Method", parts[0]);
        pairs.put("X-Query", parts[1]);
        pairs.put("X-Protocol", parts[2]);
        Page target = this.page;
        for (final Map.Entry<String, String> pair : pairs.entrySet()) {
            target = target.with(pair.getKey(), pair.getValue());
        }
        return target;
    }

}

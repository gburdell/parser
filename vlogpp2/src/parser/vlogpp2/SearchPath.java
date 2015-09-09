/*
 * The MIT License
 *
 * Copyright 2015 gburdell.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package parser.vlogpp2;

import gblib.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author gburdell
 */
public class SearchPath {

    public List<Incdir> add(final String dirName) throws Incdir.Invalid {
        final Incdir inc = new Incdir(dirName);
        if (!m_path.contains(inc)) {
            m_path.add(inc);
        }
        return get();
    }

    public List<Incdir> get() {
        return Collections.unmodifiableList(m_path);
    }

    /**
     * Look for file using search path. Find all candidates (in search path
     * order) which can be read.
     *
     * @param fname file to find.
     * @return all (readable) matching candidates.
     */
    public List<File> find(final String fname) {
        final List<File> found = new LinkedList<>();
        if (fname.startsWith("/")) {
            found.add(new File(fname));
        } else {
            File file;
            for (final Incdir dir : get()) {
                file = new File(dir.get(), fname);
                if (file.canRead()) {
                    found.add(file);
                } else if (file.exists()) {
                    Messages.message('W', "VPP-FILE-1", file.getCanonicalPath(), dir.get().getFilename());
                }
            }
        }
        return Collections.unmodifiableList(found);
    }

    private final List<Incdir> m_path = new LinkedList<>();

}

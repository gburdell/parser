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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Track global state here.
 * @author gburdell
 */
public class Global {
    private Global() {        
    }
    
    public static Global getTheOne() {
        return stTheOne;
    }
    
    public List<Incdir> addInclDir(final String dirName) throws Incdir.Invalid {
        m_inclDirs.add(new Incdir(dirName));
        return getInclDirs();
    }
    
    public List<Incdir> getInclDirs() {
        return Collections.unmodifiableList(m_inclDirs);
    }
    
    private final List<Incdir>    m_inclDirs = new LinkedList<>();
    
    private static final Global stTheOne = new Global();
    
    // Property values
    public final static String stToolRoot = System.getProperty("tool.root", 
                "/home/gburdell/projects/apfe/v2/vlogppv2");
}

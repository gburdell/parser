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
package parser.slf2;

import gblib.Util.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gburdell
 */
public class SourceFileTest {

    private static final List<Pair<String, Boolean>> stTests = new LinkedList<>();

    private static final String stTestLoc = "./test";

    private static List<Pair<String, Boolean>> addTest(final String fname, final boolean pass) {
        try {
            File f = new File(stTestLoc, fname);
            stTests.add(new Pair<>(f.getCanonicalPath(), pass));
        } catch (IOException ex) {
            Logger.getLogger(SourceFileTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stTests;
    }

    static {
        addTest("f1.lib", true);
        addTest("f2.lib", true);
    }

    /**
     * Test of parse method, of class SourceFile.
     */
    @Test
    public void testParse() {
        for (final Pair<String, Boolean> test : stTests) {
            System.out.println("parse: " + test.e1);
            try {
                boolean expResult = test.e2;
                SourceFile instance = new SourceFile(test.e1);
                boolean result = instance.parse();
                assertEquals(expResult, result);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SourceFileTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

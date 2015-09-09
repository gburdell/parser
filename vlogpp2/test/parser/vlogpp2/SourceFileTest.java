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

import gblib.Util.Pair;
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

    private static final String stDir = "/home/gburdell/projects/parser/vlogpp2/testData";
    private static final List<Pair<String, Boolean>> stTests = new LinkedList<>();

    private static void add(final String fname, boolean pass) {
        stTests.add(new Pair(stDir + "/" + fname, pass));
    }

    static {
        /*
         add("f1.v", false);
         add("f2.v", true);
         add("f3.v", false);
         add("f4.v", true);        
         add("f5.v", false);        
         add("f6.v", false);
         add("f7.v", true);       
         add("f8.v", true);
         add("f9.v", true);
         add("f10.v", true);
         add("f11.v", true);
         add("f12.v", false);
         add("f13.v", true);
         add("f14.v", true);
         add("f15.v", true);
         */
        add("f16.v", true);
        //add("sparc.v", true);
        add("f17.v", false);
        add("f18.v", false);
    }

    /**
     * Test of parse method, of class SourceFile.
     */
    @Test
    public void testParse() throws Incdir.Invalid {
        Global.stSearchPath.add(stDir);
        SourceFile instance;
        boolean expResult;
        boolean result;
        for (final Pair<String, Boolean> test : stTests) {
            System.out.println("parse: " + test.e1);
            expResult = test.e2;
            result = false;
            try {
                instance = new SourceFile(test.e1, System.out);
                result = instance.parse();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SourceFileTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseError ex) {
                System.err.println(ex);
            } catch (IOException ex) {
                Logger.getLogger(SourceFileTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            assertEquals(expResult, result);
        }
    }
}

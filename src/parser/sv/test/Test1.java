//The MIT License
//
//Copyright (c) 2006-2010  Karl W. Pfalzer
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package parser.sv.test;
import  java.io.File;
import  parser.sv.Main;
import  parser.v2k.Preproc;
import  java.util.*;

/**
 * Test ability to find files which only contain (used `defines).
 * @author kpfalzer
 */
public class Test1 {
    public static void main(String argv[]) {
        test1a();
    }

    static class MyFile extends File {
        public MyFile(String fn) {
            super(fn);
        }

        /**/
        @Override
        public boolean equals(Object obj) {
            File to = (File)obj;
            return this.getAbsoluteFile().equals(to.getAbsoluteFile());
        }/**/
    }

    private static void test1a() {
        String ff[] = new String[]{
            "/Users/kpfalzer/projects/parser/test/uses_define.v", "uses_define.v"
        };
        File f1 = new File(ff[0]);
        File f2 = new File(ff[1]);
        boolean eq = f1.equals(f2);
        MyFile m1 = new MyFile(ff[0]);
        MyFile m2 = new MyFile(ff[1]);
        eq = m1.equals(m2);
        List<MyFile> l1 = new LinkedList<MyFile>();
        l1.add(m1);
        eq = l1.contains(m2);
        String fns[] = new String[]{
            "has_define3.v","has_define2.v", 
            "has_define2.v",
            "has_define.v","uses_define.v"
        };
        List<String> used = new LinkedList<String>();
        Main ptree = null;

        if (false) {
            used.add("m1.v");
            ptree = new Main(new String[]{"m1.v"});
        } else {
            used.add("uses_define.v");
            ptree = new Main(fns);
        }


        Preproc pp = Preproc.getTheOne();
        List<String> needUsed = pp.getDefineOnlyFiles(used);
    }
}

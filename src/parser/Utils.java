//The MIT License
//
//Copyright (c) 2006-2010  Karl W. Pfalzer
//Copyright (c) 2011-      George P. Burdell
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
package parser;
import  java.util.*;
import  java.io.*;

/**
 *
 * @author karl
 */
public class Utils {

    /** Creates a new instance of Utils */
    public static String stripDoubleQuotes (final String s) {
        int len = s.length();
        String ns = s.substring(1, len-1);
        return ns;
    }

    public static void invariant(boolean c) {
        if (false == c) {
            Thread.dumpStack();
            System.exit(1);
        }
    }

    private final static String stDOT = ".";

    public static String getToolRoot() {
        String root = System.getProperty("tool.root");
        if (null == root) {
            root = stDOT;
        }
        return root;
    }

    public static void fatal(Exception ex) {
        PrintStream err = System.err;
        err.print(ex.getMessage());
        ex.printStackTrace(err);
        System.exit(1);
    }

    /**Lookup property value.
     *
     * @param prop  property name
     * @return true if property exists and set to "true" or else false.
     */
    public static boolean getPropertyAsBool(String prop) {
        String pv = System.getProperty(prop);
        boolean v = (null == pv) ? false : Boolean.parseBoolean(pv);
        return v;
    }
    public static int getPropertyAsInt(String nm) {
        int rval = Integer.MIN_VALUE;
        String str = System.getProperty(nm);
        if (null != str) {
            rval = Integer.parseInt(str);
        }
        return rval;
    }
    public static void abnormalExit(Exception ex) {
        System.err.println(ex.getMessage());
        ex.printStackTrace(System.err);
        System.exit(1);
    }

    public static List<String> arrayToList(String s[]) {
        List<String> rval = new LinkedList<String>();
        for (String i : s) {
            rval.add(i);
        }
        return rval;
    }

    public static int streamCopy(BufferedInputStream from, BufferedOutputStream to) throws IOException {
        final int bufSz = 2048;
        byte buf[] = new byte[bufSz];
        int tlCnt = 0, cnt = 0;
        while (0 <= (cnt = from.read(buf, 0, bufSz))) {
            tlCnt += cnt;
            to.write(buf, 0, cnt);
        }
        to.flush();
        return tlCnt;
    }

    public static String nl() {
        return m_nl;
    }

    /**
     * Look for file in specified directories.
     * @param dirs
     * @param fname
     * @param getCanon
     * @param reportDuplSvr
     * @return
     */
    public static Pair<File,File> findFile(List<File> dirs, String fname, boolean getCanon,
            int reportDuplSvr)
    {
        Pair<File,File> rval = new Pair<File,File>(null,null);
        List<Pair<File,File>> locs = new LinkedList<Pair<File,File>>();
        for (File d : dirs)
        {
            File f = new File(d, fname);
            if (f.exists())
            {
                try {
                    if (getCanon) {
                        f = f.getCanonicalFile();
                        d = d.getCanonicalFile();
                    }
                    rval = new Pair<File,File>(d,f);
                    if (0 < reportDuplSvr) {
                        if ( ! locs.contains(rval)) {
                            locs.add(rval); //no duplicates
                        }
                    } else {
                        break; //for
                    }
                } catch (Exception ex) {
                    //do nothing
                }
            }
        }
        if ((0 < reportDuplSvr) && (1 < locs.size())) {
            char svr = (1 == reportDuplSvr) ? 'W' : 'E';
            Message.message(svr, "INCL-3", fname, locs.size());
            int cnt = 1;
            for (Pair<File,File> dirf : locs) {
                MessageMgr.message(svr, "INCL-3.1", dirf.v2.getPath(), cnt++);
            }
            rval = locs.get(0); //return 1st one
        }
        return rval;
    }

    public static Pair<File,File> findFile(List<File> dirs, String fname) {
        return findFile(dirs, fname, true, 1);
    }

    /** Return a null x as an empty collection. */
    public static <T> T asEmpty(T x, T empty) {
        return (null != x) ? x : empty;
    }

	public static <T> T downCast(Object o) {
		return (T)o;
	}

    /**
     * Add only new elements to list.
     * @param <T> type of list element.
     * @param to list to update with only new elements.
     * @param from list to get new elements from.
     */
    public static <T> void addAllNoDups(List<T> to, List<T> from) {
        for (T ele : from) {
            if (! to.contains(ele)){
                to.add(ele);
            }
        }
    }

    private final static String m_nl = System.getProperty("line.separator");
}

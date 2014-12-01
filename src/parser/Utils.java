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
        Pair<File,File> rval = new Pair<>(null,null);
        List<Pair<File,File>> locs = new LinkedList<>();
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
                    rval = new Pair<>(d,f);
                    if (0 < reportDuplSvr) {
                        if ( ! locs.contains(rval)) {
                            locs.add(rval); //no duplicates
                        }
                    } else {
                        break; //for
                    }
                } catch (IOException ex) {
                    //do nothing
                }
            }
        }
        if ((0 < reportDuplSvr) && (1 < locs.size())) {
            char svr = (1 == reportDuplSvr) ? 'W' : 'E';
            Message.message(svr, "INCL-3", fname, locs.size());
            int cnt = 1;
            for (Pair<File,File> dirf : locs) {
                MessageMgr.message(svr, "INCL-3.1", new Object[]{dirf.v2.getPath(), cnt++});
            }
            rval = locs.get(0); //return 1st one
        }
        return rval;
    }

    public static Pair<File,File> findFile(List<File> dirs, String fname) {
        return findFile(dirs, fname, true, 1);
    }

}

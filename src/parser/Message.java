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

import antlr.ANTLRException;
import antlr.TokenStreamException;
import gblib.Util;

/**
 *
 * @author karl
 */
public class Message {

    public static boolean stExceptionAddLineno = false;
    private static ILexer stLexer;

    public static void setLexer(ILexer lxr) {
        stLexer = lxr;
    }

    public static boolean hasParseError() {
        return (0 < stErrorCnt);
    }

    public static void message(String fname, int lineno, TokenStreamException ex) {
        String msg = ex.toString();
        if (stExceptionAddLineno) {
            msg = fname + ":" + lineno + ": " + msg;
        }
        Util.error("ERR-2", msg);
        stErrorCnt++;
    }

    public static void message(ANTLRException ex) {
        //try to extract prefix <CODE/> or <svr:CODE/>
        String code = "ERR-1";
        String m = ex.toString().trim();
//ex.printStackTrace(System.err);
        char svr = 'E';
        if ('<' == m.charAt(0)) {
            int n = m.indexOf("/>");
            if (0 < n) {
                code = m.substring(1, n);
                if (':' == code.charAt(1)) {
                    svr = code.charAt(0);
                    code = code.substring(2);
                }
                m = m.substring(n + 2);
            }
        }
        if ('E' == svr) {
            stErrorCnt++;
        }
        message(svr, code, m);
    }

    /**
     * public static void message(RecognitionException ex) { String code =
     * "ERR-1"; String m = ex.toString().trim(); LocationBase loc = new
     * LocationBase(stLexer.getFilename(), ex.getLine()); stErrorCnt++;
     * message(loc, 'E', code, m); }
 *
     */
    public static void message(char severity, String code, Object... args) {
        String fn = stLexer.getFilename();
        Integer ln = stLexer.getLine();
        Object nargs[] = new Object[args.length + 2];
        nargs[0] = fn;
        nargs[1] = ln;
        System.arraycopy(args, 0, nargs, 2, args.length);
        MessageMgr.message(severity, code, nargs);
    }

    public static void message(ALocation loc, char severity, String code,
            Object... args) {
        String fn = loc.getFilename();
        Integer ln = loc.getLineno();
        Object nargs[] = new Object[args.length + 2];
        nargs[0] = fn;
        nargs[1] = ln;
        System.arraycopy(args, 0, nargs, 2, args.length);
        MessageMgr.message(severity, code, nargs);
    }

    /**
     * Display message iff. doMsg==true.
     */
    public static void message(boolean doMsg, char svr, String code, String msg) {
        if (doMsg) {
            String fn = stLexer.getFilename();
            Integer ln = stLexer.getLine();
            MessageMgr.message(svr, code, fn, ln, msg);
        }
    }

    /**
     * Display message iff. doMsg==true.
     */
    public static void message(boolean doMsg, Location loc, char svr,
            String code, Object... args) {
        if (doMsg) {
            message(loc, svr, code, args);
        }
    }

    public static void message(char svr, String code, String msg) {
        String fn = stLexer.getFilename();
        Integer ln = stLexer.getLine();
        MessageMgr.message(svr, code, fn, ln, msg);
    }

    public static void syntaxError(String msg) {
        message('E', stStax, msg);
    }

    public static void syntaxWarning(String msg) {
        message('W', stStax, msg);
    }
    private final static String stStax = "PARSE-1";
    private static int stErrorCnt = 0;
}

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
package parser.v2k;
import parser.ILexer;
import  parser.ExceptionBase;
import  parser.Utils;
import  parser.Message;
import  parser.Pair;
import  java.util.*;
import  antlr.TokenStreamException;
import  antlr.RecognitionException;

/**
 *
 * @author karl
 */
public abstract class Parser {
    public Parser() {
        init();
    }

    private void init() {
        stPP = Preproc.getTheOne();
        setTheOne(this);
        addSearchPath(".");
    }

    protected static Preproc stPP;
    
    static class ParseException extends ExceptionBase {
        public ParseException(char severity, String code, Object ... args) {
            super(severity, code, args);
        }
    }

    protected abstract void parse(String fname)
            throws TokenStreamException, RecognitionException;

    protected abstract ILexer getLexer();

    public void parse(String argv[]) {
        for (String ai : argv) {
            try {
                parse(ai);
            } catch (TokenStreamException ex) {
                if (null != ex.getMessage()) {
                    Message.message(ex);
                }
            } catch (Exception ex) {
                if (true) {//false == ExceptionBase.class.isInstance(ex)) {
                    Utils.abnormalExit(ex);
                }
            }
        }
    }
    public void parse(List<String> argv, List<String> inclDirs, 
            List<Pair<String,String> > defs) {
        addSearchPaths(inclDirs);
        addDefines(defs);
        parse(argv.toArray(new String[0]));
    }
    public void addDefines(List<Pair<String,String> > keyVals) {
        stPP.addDefines(keyVals);
    }
    public void addSearchPaths(List<String> paths) {
        stPP.addSearchPaths(paths);
    }
    private static void setTheOne(Parser x) {
        assert(null == stTheOne);
        stTheOne = x;
    }
    public static Parser getTheOne() {
        return stTheOne;
    }
    public String getFilename() {
        return (null == getLexer()) ? "<cmdline>" : getLexer().getFilename();
    }
    public int getLine() {
        return (null == getLexer()) ? 0 : getLexer().getLine();
    }
    public boolean addSearchPath(String dir) {
        return stPP.addSearchPath(dir);
    }
    /**
     * Get include files, in no particular order.
     */
    public Map<String,Integer> getIncludeFiles() {
        return stPP.getIncludeFiles();
    }
    /**
     * Get define only files.
     * @param srcs all used sources.
     * @return define only files.
     */
    public String[] getDefineOnlyFiles(String srcs[]) {
        List<String> fn = Utils.arrayToList(srcs);
        List<String> rval = stPP.getDefineOnlyFiles(fn);
        return rval.toArray(new String[0]);
    }
    public static void setAbsPath(boolean v) {
        stUseAbsPaths = v;
    }
    public static void setDumpPP(boolean v) {
        stDumpPP = v;
    }
    public static void setKeepComments(boolean v) {
        stShowComments = v;
    }
    public static void setRedefinedCheck(int v) {
        stRedefinedCheck = v;
    }

    private static Parser stTheOne = null;
    static final int stBufSz = 1<<20;
    /**Use absolute paths or not.*/
    static boolean stUseAbsPaths = false;
    /**Dump preprocessed files to file.E*/
    static boolean stDumpPP = false;
    /**Show define definitions in preprocessed output.*/
    static boolean stDumpDefn = false;
    /**Show comments during pp dump.*/
    static boolean stShowComments = true;
    /**Type of macro redefinition check: 1=value difference; 2=file location.*/
    static int stRedefinedCheck = 1;
    /**Include file multiple candidates: 0=dont report, 1=warning, >1=error.*/
    static int stMultIncludeCheck = 1;
 }

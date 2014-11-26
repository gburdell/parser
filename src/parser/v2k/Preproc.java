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
import  java.util.*;
import  java.io.*;
import  antlr.*;
import  parser.ExceptionBase;
import  parser.Utils;
import  parser.Pair;
import  parser.Message;
import  parser.Location;

/**
 *
 * @author karl
 */
public class Preproc {
    public final static String stDefineDelimitLoc = "@@";

	public static Preproc getTheOne() {
		return stTheOne;
	}

    public Preproc() {
        init();
    }

    private void init() {
        addSearchPath(".");
        assert stTheOne == null;
        stTheOne = this;
        VlogppLexer.setPP(getTheOne());
    }

    private static final VlogLocation stCmdLineLoc = new VlogLocation("<cmdline>", 0);
    
    VlogLocation getLexerLoc() {
        return (null != m_preproc) ? m_preproc.getLocation() : stCmdLineLoc;
    }
    public void setPreproc(IPreproc pp) {
        m_preproc = pp;
    }

	public static void setIfdefVerbose(boolean verbose) {
		stDoIfdefMessages = verbose;
	}
    
    public void addDefines(List<Pair<String,String> > keyVals) {
        for (Pair<String,String> kv : keyVals) {
            addDefine(kv.v1, kv.v2);
        }
    }
    public void addSearchPaths(List<String> paths) {
        for (String p : paths) {
            addSearchPath(p);
        }
    }
    public boolean addSearchPath(String dir) {
        boolean rval = false;
        File d = new File(dir);
        if (d.isDirectory()) {
            if (Parser.stUseAbsPaths) {
                d = d.getAbsoluteFile();
            }
            if (false == m_searchPath.contains(d)) {
                m_searchPath.add(d);
            }
            rval = true;
        }
        return rval;
    }
    public void uponEOF() throws TokenStreamException, CharStreamException {
        m_preproc.pop();
    }
    /**
     * Get include files, in no particular order.
     * @return include files.
     */
    public Map<String,Integer> getIncludeFiles() {
        return m_includes;
    }

    /**
     * Get list of files which only define macros which are used within
     * used sources and other define only files whose macros are used.
     * @param usedSrcs list of used sources
     * @return list of files which only define macros.
     */
    public List<String> getDefineOnlyFiles(List<String> usedSrcs) {
        return m_macroUsed.getMacroDefnFiles(usedSrcs);
    }

 /*Allow to redefine
    public boolean okToDefine(String key) throws RecognitionException {
        boolean rval = !passToken() || isDefined(key);
        if (false == rval) {
            throw new ParseException('E', "MACRO-1", key);
        }
        return rval;
    }
  */
    public boolean okToDefine(String key) throws RecognitionException {
        boolean rval = !passToken() || isDefined(key);
        if (false == rval) {
            Message.message(getLexerLoc(), 'E', "MACRO-1", key);
        }
        return true;    //allow to redefine
    }
   
    // Return true if tok found; else tok==return val
    public boolean isDefined(String key) {
        return m_defines.containsKey(key);
    }
    public void expandMacro(String key) throws TokenStreamRetryException {
        if (passToken()) {
            if (false == isDefined(key)) {
                return;
            }
            String v = m_defines.get(key).getVal();
            m_macroUsed.markUsed(key, getLexerLoc());
            m_preproc.push(v);
        }
    }
    public void expandMacro(String key, List<String> parms) 
        throws TokenStreamRetryException {
        if (passToken()) {
            assert(isDefined(key));
            String v = m_defines.get(key).getVal(parms);
            m_macroUsed.markUsed(key, getLexerLoc());
            m_preproc.push(v);
        }
    }
    public boolean hasParams(String key) {
        boolean has = m_defines.containsKey(key);
        if (has) {
            has = m_defines.get(key).hasParams();
        }
        return has;
    }
    private static boolean isRedefined(MacroVal has, MacroVal check) {
        boolean rval = false;
        if (null != has) {
            switch (Parser.stRedefinedCheck) {
                case 1:
                    rval = !has.getVal().equals(check.getVal());
                    break;
                default:
                    rval = !has.getWhereDefn().equals(check.getWhereDefn());
            }
        }
        return rval;
    }
    /**
     * Process macro value before saving to remove comments.
     * @param val macro value
     * @return processes macro value
     */
    private static String process(String val) {
        //remove line comments
        int start, end;
        while (true) {
            start = val.indexOf("//");
            if (0 > start) {
                break;  //while
            }
            String ns = val.substring(0, start);
            end = val.indexOf("\\", start);
            if (0 < end) {
                ns += val.substring(end);
            }
            val = ns;
        }
        val = val.replace("\\\n"," ").replace("\\\r"," ")
            .replace('\n', ' ').replace('\r',' ').trim();
        return val;
    }
    
    public boolean addDefine(String key, String val) {
        // Strip location (if present)
        VlogLocation loc = null;
        final String delim = stDefineDelimitLoc;
        int ix = key.indexOf(delim); //fname:lnum<delim>
        if (0 < ix) {
            int ix2 = key.indexOf(":");
            if (0 < ix2) {
                String fn = key.substring(0, ix2);
                int ln = Integer.parseInt(key.substring(ix2+1, ix));
                loc = new VlogLocation(fn, ln);
                key = key.substring(ix+delim.length());
            }
        }
        MacroVal has = m_defines.get(key);
        if (passToken()) {
            if (null == val) {
                val = "";
            }
            val = process(val);
            MacroVal m = new MacroVal(val, loc);
            if (isRedefined(has, m)) {
                Message.message(m.getWhereDefn(), 'W', "MACRO-3", key, 
                                has.getWhereDefn());
            } else {
                m_defines.put(key, m);
            }
            m_macroUsed.addDefn(key, m.getWhereDefn());
        }
        return has != null;
    }
    public boolean addDefine(String key, List<String> parms, String val) {
        MacroVal has = m_defines.get(key);
        if (passToken()) {
            val = process(val);
            MacroVal m = new MacroVal(parms, val);
            Location loc = m.getWhereDefn();
            if (isRedefined(has, m)) {
                Message.message(loc, 'W', "MACRO-3", key,
                                has.getWhereDefn());
            } else {
                m_defines.put(key, m);
            }
            m_macroUsed.addDefn(key, m.getWhereDefn());
        }
        return has != null;
    }

    /**
     * Search for include file 'fn' using search path.
     * 
     * @param fn
     * @throws TokenStreamRetryException
     * @throws RecognitionException 
     */
    public void includeFile(String fn) /*"fn"*/ 
            throws TokenStreamRetryException,
                   RecognitionException {
        if (false == passToken()) {
            return;
        }
        String tfn = fn.replace('"', ' ').trim();
        //Get Pair<dir,filePath>
        Pair<File,File> ifile = Utils.findFile(m_searchPath, tfn, Parser.stUseAbsPaths,
                Parser.stMultIncludeCheck);
        if (null == ifile.v2) {
            throw new ParseException(getLexerLoc(), 'E', "INCL-2", tfn);
        }
        tfn = ifile.v2.getPath();  //already canonica from findFile
        addToMap(m_includes, tfn);
        addToMap(m_usedPaths, ifile.v1.getPath());
        m_preproc.push(ifile.v2);
    }

    private static void addToMap(Map<String,Integer> map, String key) {
        if (false == map.containsKey(key)) {
            map.put(key, new Integer(1));
        } else {
            map.put(key, map.get(key)+1);
        }
    }

    /**Alter next state if previous is block*/
    private IfdefState next(IfdefState dflt) {
        IfdefState ns = dflt;
        if (!m_ifdefStack.empty() && 
                (IfdefState.eDone != m_ifdefStack.peek())) {
            ns = IfdefState.eBlockDone;
        }
        return ns;
    }
    public void undef(String key) {
        if (null == m_defines.remove(key)) {
            Message.message(getLexerLoc(), 'W', "MACRO-2", key);
        }
    }
	private void ifdefMessages(char code, String macro) {
		if (stDoIfdefMessages) {
			String v = m_ifdefStack.peek().pass() ? "true" : "false";
			Message.message(getLexerLoc(), 'I', "MACRO-"+code, macro, v);
		}
	}
	private void ifdefMessages(char code) {
		if (stDoIfdefMessages) {
			String v = m_ifdefStack.peek().pass() ? "true" : "false";
			Message.message(getLexerLoc(), 'I', "MACRO-"+code, v);
		}
	}
    public void ticIfdef(String key) {
        boolean rval = isDefined(key);
        m_ifdefStack.push(next(rval ? IfdefState.eDone 
                : IfdefState.eNotDone));
		ifdefMessages('4', key);
    }
    public void ticIfndef(String key) {
        boolean rval = !isDefined(key);
        m_ifdefStack.push(next(rval ? IfdefState.eDone 
                : IfdefState.eNotDone));
		ifdefMessages('5', key);
    }
    public void ticElse() {
        IfdefState was = m_ifdefStack.pop();
		IfdefState nxt = (was==IfdefState.eDone)
                ? IfdefState.eBlockDone : IfdefState.eDone;
        m_ifdefStack.push(next(nxt));
		ifdefMessages('6');
    }
    public void ticElsif(String key) {
        IfdefState was = m_ifdefStack.pop();
        IfdefState nxt = (was==IfdefState.eDone) ? IfdefState.eBlockDone
                : (isDefined(key) ? IfdefState.eDone : IfdefState.eNotDone);
        m_ifdefStack.push(next(nxt));
		ifdefMessages('7', key);
    }
    public void ticEndif() {
        m_ifdefStack.pop();
    }
    /**Return true to parser if can pass token.
     * This way we can block tokens while processing ifdef blocks.
     */
    public boolean passToken() {
        return (m_ifdefStack.isEmpty()) ? true : m_ifdefStack.peek().pass();
    }

    static class ParseException extends ExceptionBase {
        public ParseException(VlogLocation loc, char severity, String code, Object ... args) {
            super(loc, severity, code, args);
        }
    }
    
    public Stack<LexState> getStack() {
        return m_lexStack;
    }

    public static interface IPreproc {
        public void push(File f) throws TokenStreamRetryException;
        public void push(String txt) throws TokenStreamRetryException;
        public void pop() throws TokenStreamRetryException;
        public VlogLocation getLocation();
    }
    
    private static enum IfdefState {
        eDone, eNotDone, eBlockDone;
                 
        public boolean pass() {
            return (this==eDone);
        }
    }

    private IPreproc    m_preproc;
    
    public static class LexState {
        public LexState(LexerSharedInputState lis, boolean doLine) {
            m_state = lis;
            m_doLine = doLine;
        }
        public final LexerSharedInputState getState() {
            return m_state;
        }
        public final boolean getDoLine() {
            return m_doLine;
        }
        final LexerSharedInputState   m_state;
        final boolean                 m_doLine;
    }
    
    /**Stack of lexer states*/
    private Stack<LexState> m_lexStack = new Stack<>();

    /**Stack of ifdef states*/
    private Stack<IfdefState> m_ifdefStack = new Stack<>();
    private Map<String,MacroVal> m_defines = new HashMap<>();

    /**
     * Track where macros are defined and if they are used.
     * From this info, can return files which may have only defined
     * macros.  These are used if any of the macros are actually used.
     */
    private static class MacrosUsed {
        /**
         * Wrap File so we can use equals between File.
         */
        private static class MyFile extends File {
            public MyFile(Location loc) {
                this(loc.getFilename());
            }

            public MyFile(String fn) {
                super(fn);
                fname = fn;
            }

            public boolean equals(Object obj) {
                File f = (File)obj;
                return this.getAbsoluteFile().equals(f.getAbsoluteFile());
            }

            final String fname;
        }

        void addDefn(String macro, Location loc) {
            if (null != loc) {
                MyFile f = new MyFile(loc);
                if (!m_filesByMacro.containsKey(macro)) {
                    m_filesByMacro.put(macro, new LinkedList<MyFile>());
                }
                m_filesByMacro.get(macro).add(f);
            }
        }
        void markUsed(String macro, Location loc) {
            if (null != loc) {
                MyFile f = new MyFile(loc);
                List<String> l = null;
                if (!m_macrosUsed.containsKey(f)) {
                    l = new LinkedList<>();
                    m_macrosUsed.put(f, l);
                } else {
                    l = m_macrosUsed.get(f);
                }
                if (!l.contains(macro)) {
                    l.add(macro);
                }
            }
        }
        /**
         * Go through usedSrcs and create unique list of used macros.
         * Foreach macro, get list of sources which define macro.
         * Foreach (macro) source, return source if not in usedSrcs
         * @param usedSrcs list of files which are used.  This set is the union of
         *                 source files and included files (known a priori).
         * @return list of source files which define macros used by usedSrcs 
         *         but are not in usedSrcs.
         */
        List<String> getMacroDefnFiles(final List<String> usedSrcs) {
            List<String> rval = new LinkedList<>();
            List<String> allUsed = new LinkedList<>(usedSrcs);
            int lastAllUsedSize = 0;
            do {
                lastAllUsedSize = allUsed.size();
                List<String> newUsed = getMacroDefnFilesIter(allUsed);
                Utils.addAllNoDups(rval, newUsed);
                allUsed.addAll(newUsed);
            } while (lastAllUsedSize != allUsed.size());
            return rval;
        }

        private List<String> getMacroDefnFilesIter(final List<String> usedSrcs) {
            List<String> rval = new LinkedList<>();
            List<MyFile> usedFiles = new LinkedList<>();
            //Hash/keys of used macros
            Map<String,Object> usedMacros = new HashMap<>();
            //1) get macros used
            for (String fn : usedSrcs) {
                MyFile f = new MyFile(fn);
                usedFiles.add(f);
                if (m_macrosUsed.containsKey(f)) {
                    for (String macro : m_macrosUsed.get(f)) {
                        if (! usedMacros.containsKey(macro)) {
                            usedMacros.put(macro, null);    //value if nil, not used.
                        }
                    }
                }
            }
            //2) get sources which defined macro not in usedFiles
            for (String macro : usedMacros.keySet()) {
                for (MyFile src : m_filesByMacro.get(macro)) {
                    if (! usedFiles.contains(src)) {
                        rval.add(src.fname);
                    }
                }
            }
            return rval;
        }
        /**
         * Map of file which defined macro/key.
         */
        Map<String,List<MyFile>> m_filesByMacro = new HashMap<>();
        /**
         * Map of macros used by File/key.
         */
        Map<MyFile,List<String>> m_macrosUsed = new HashMap<>();
    }

    private MacrosUsed m_macroUsed = new MacrosUsed();

    private List<File>  m_searchPath = new LinkedList<>();
    /**
     * Keep track of any include files processed (in no particular order).
     */
    private Map<String,Integer> m_includes = new HashMap<>();
    /**
     * Keep track of searchPath dirs actually used.
     */
    private Map<String,Integer> m_usedPaths = new HashMap<>();

    private static Preproc  stTheOne = new Preproc();
	/**
	 * Issue messages about ifdef processing
	 */
	private static boolean stDoIfdefMessages = false;
}

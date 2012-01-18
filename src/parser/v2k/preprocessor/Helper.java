/*
 *  The MIT License
 * 
 *  Copyright 2011 George P. Burdell
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package parser.v2k.preprocessor;

import antlr.LexerSharedInputState;
import antlr.CharStreamException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStreamException;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.io.File;
import java.io.FileReader;
import parser.Pair;
import static parser.Utils.findFile;
import static parser.Utils.invariant;
import parser.v2k.preprocessor.antlrx.InputBufferX;
import parser.v2k.preprocessor.antlrx.CharBufferX;
import static parser.MessageMgr.message;
import static parser.Utils.downCast;
import static parser.Utils.fatal;

/**
 * A helper class to Lexer.
 * This class offloads the state of preprocessor/lexer.
 * @author gburdell
 */
public class Helper implements PreprocLexerTokenTypes {

    private static Helper   stTheOne = null;
    
    public static Helper getTheOne() {
        return stTheOne;
    }
    
    /**
     * Set the current source.
     * @param fname create tokens from this file.
     */
    public static void setSource(String fname) {
        if (null == stTheOne) {
            stTheOne = new Helper(fname);
        } else {
            stTheOne.push(fname);
        }
    }
    
    private Helper(String fname) {
        init(fname);
    }

    private void init(String fname) {
        assert(null == stTheOne);
        push(fname);
        stTheOne = this;
    }
    
    /**
     * Get next token method to be used to generate post pre-processed token
     * stream.
     * @return next token.
     * @throws TokenStreamException on lexer error. 
     */
    public Token nextToken() throws TokenStreamException {
        Token tok = getLexer().nextToken();
        if (!passToken()) {
            if (NL != tok.getType()) {
                tok.setType(Token.SKIP);
            }
        }
        return tok;
    }

    /**
     * Generate warning message using current location.
     * @param code message code.
     * @param args var args (excluding location).
     */
    private void warn(String code, Object... args) {
        messageAddLocation('W', code, args);
    }

    /**
     * Generate error message using current location.
     * @param code message code.
     * @param args var args (excluding location).
     */
    private void error(String code, Object... args) {
        messageAddLocation('E', code, args);
    }

    private void messageAddLocation(char type, String code, Object... args) {
        String fn = getLexer().getFilename();
        Integer ln = getLexer().getLine();
        Object nargs[] = new Object[args.length + 2];
        nargs[0] = fn;
        nargs[1] = ln;
        System.arraycopy(args, 0, nargs, 2, args.length);
        message(type, code, nargs);
    }

    private PreprocLexer getLexer() {
        return m_tokSrcStack.peek().getLexer();
    }

    private boolean isDefined(String mnm) {
        return m_macros.isDefined(mnm);
    }

    private boolean hasParams(String mnm) {
        return m_macros.hasParams(mnm);
    }

    private void macroBegin(String nm) {
        m_macroName = nm;
    }

    private void macroBegin(Token tok) {
        macroBegin(tok.getText());
    }

    private void macroEnd(String defn) {
        defn = defn.replace("\\\n", " ").replace("\\\r", " ").replace('\n', ' ').replace('\r', ' ').trim();
        if (null == m_macroParms) {
            m_macros.add(m_macroName, defn);
        } else {
            m_macros.add(m_macroName, m_macroParms, defn);
            m_macroParms = null;
        }
        m_macroName = null;
    }

    private void addMacroParm(String parm, String dflt) {
        if (null == m_macroParms) {
            m_macroParms = Parm.createList();
        }
        Parm p = new Parm(parm);
        p.setDefault(dflt);
        m_macroParms.add(p);
    }

    public void addMacroParm(Token parm, String dflt) {
        addMacroParm(parm.getText(), dflt);
    }

    /**
     * Get next token, skipping comments and whitespace, as specified.
     *
     * @param skipWS skip whitespace (NL | WS).
     * @param skipComments skip any comments.
     * @return
     * @throws TokenStreamException 
     */
    private Token getNextToken(boolean skipWS, boolean skipComments) throws TokenStreamException {
        m_lastToken = null;
        int type;
        for (boolean stay = true; stay;) {
            m_lastToken = getLexer().nextToken();
            type = m_lastToken.getType();
            stay = (skipWS && (type == NL || type == WS))
                    || (skipComments && type == COMMENT);
            //TODO: detect EOF and pop
        }
        return m_lastToken;
    }

    /**
     * Get next token, skipping whitespace and comments.
     * @return next token.
     * @throws TokenStreamException 
     */
    private Token getNextToken() throws TokenStreamException {
        return getNextToken(true, true);
    }

    /**
     * Slurp up default text.
     * Member m_lastToken is set to last token, which should be COMMA or LPAREN.
     * @return default text.
     */
    public String getDefaultText() throws CharStreamException, TokenStreamException {
        m_sbld = new StringBuilder();
        //Follow set is COMMA or RPAREN.
        //Slurp up to balance (), [] and {}
        for (boolean stay = true; stay;) {
            getNextToken();
            switch (m_lastToken.getType()) {
                case LPAREN:
                    balance(RPAREN);
                    break;
                case LCURLY:
                    balance(RCURLY);
                    break;
                case LBRACK:
                    balance(RBRACK);
                    break;
                case COMMA:
                case RPAREN:
                    stay = false;
                    break;
                default:
                    m_sbld.append(m_lastToken.getText());
            }
        }
        return m_sbld.toString();
    }

    /**
     * Accumulate balance (), [], {}.
     * @param closeTok token to balance to.
     */
    private void balance(int closeTok) throws TokenStreamException, CharStreamException {
        Token tok = getNextToken();
        int tokc = tok.getType();
        assert (tokc == LPAREN || tokc == LCURLY || tokc == LBRACK);
        m_sbld.append(tok.getText());
        for (boolean stay = true; stay;) {
            tok = getNextToken();
            switch (tok.getType()) {
                case LPAREN:
                    balance(RPAREN);
                    break;
                case LCURLY:
                    balance(RCURLY);
                    break;
                case LBRACK:
                    balance(RBRACK);
                    break;
                default:
                    m_sbld.append(tok.getText());
                    stay = closeTok != tok.getType();
            }
        }
    }

    /**
     * Process `op.
     * @param code one of TIC_op Token type.
     * @param txt original op sans tic.
     * @return token to pass back.
     */
    public Token processTicOp(int code, String txt)
            throws RecognitionException, CharStreamException, TokenStreamException {
        if (m_slurpingMacroText) {
            //we're grabbing macro text, so any embedded `op want part of text
            //and not expanded.
            setToken(code, "`" + txt);
        } else {
            switch (code) {
                case TIC_DEFINE:
                    getDefineBody();
                    break;
                case TIC_MACRO:
                    ticMacro(txt);
                    break;
                case TIC_IFNDEF:
                    ticIfndef(expectNextAsIdent());
                    break;
                case TIC_IFDEF:
                    ticIfdef(expectNextAsIdent());
                    break;
                case TIC_ELSE:
                    ticElse();
                    break;
                case TIC_ELSIF:
                    ticElsif(expectNextAsIdent());
                    break;
                case TIC_ENDIF:
                    ticEndif();
                    break;
                case TIC_INCLUDE:
                    ticInclude();
                    break;
                default:
            }
            switch (code) {
                case TIC_FILENM:
                case TIC_LINE:
                case TIC_LINENUM: //fall thru
                case TIC_MACRO:
                    //pass these through
                    break;
                default:
                    setSkipToken();
            }
            //TODO: generate `line after preprocessing, since `define messes up
            //newlines returned.
        }
        /*
         * TODO: a bit kludgy perhaps that we muck w/ lexer state then have
         * to pass back state
         */
        return getLexer().getTokenObject();
    }

    private void ticInclude() throws TokenStreamException, RecognitionException {
        getLexer().setExpectFilename(true);
        Token tok = getNextToken();
        expect(tok, new int[]{STRING, IMPL_FILENAME});
        getLexer().setExpectFilename(false);
        String ifn = tok.getText();
        String fn = getInclFile(ifn);
        if (null != fn) {
            //TODO: `line
            push(fn);
        } else {
            warn("INCL-2", ifn);
        }
    }

    /**
     * Add directory to include file search path.
     * @param dir directory to add.
     * @return true if dir is directory and readable, else false. 
     */
    public boolean addInclSearchPath(String dir) {
        File asFile = new File(dir);
        boolean rval = asFile.canRead() && asFile.isDirectory();
        if (rval) {
            m_inclSearchPaths.add(asFile);
        }
        return rval;
    }

    private String getInclFile(String inclFname) {
        int n = inclFname.length() - 1;
        char fl[] = {inclFname.charAt(0), inclFname.charAt(n)};
        invariant(0 <= match(fl, stInclNameAlts));
        boolean hasAngled = fl[0] == '<';
        inclFname = inclFname.substring(1, n);  //drop opening/closing <> or ""
        String fn = null;
        Pair<File, File> dfn = findFile(m_inclSearchPaths, inclFname);
        if (null != dfn.v1) {
            fn = new File(dfn.v1, dfn.v2.getName()).getAbsolutePath();
        }
        return fn;
    }
    private static final String stInclNameAlts[] = {"<>", "\"\""};

    /**
     * Match sequence of chars with one of alternates.
     * @param cs sequence of chars.
     * @param alts alternates to match.
     * @return index of alts which matches cs.
     */
    private static int match(char cs[], String alts[]) {
        for (int ix = 0; ix < alts.length; ix++) {
            boolean rval = true;
            for (int i = 0; i < alts[ix].length(); i++) {
                rval &= (alts[ix].charAt(i) == cs[i]);
            }
            if (rval) {
                return ix;
            }
        }
        return -1;
    }

    private String expectNextAsIdent() throws RecognitionException, TokenStreamException {
        getNextToken();
        expect(m_lastToken, IDENT);
        return m_lastToken.getText();
    }

    private void ticMacro(String macnm)
            throws TokenStreamException, RecognitionException, CharStreamException {
        if (isDefined(macnm)) {
            String val = null;
            if (hasParams(macnm)) {
                List<String> args = getActualArgs();
                try {
                    val = m_macros.expandMacro(macnm, args);
                } catch (MacroDefns.ExpansionException ex) {
                    throw new RecognitionException(ex.getMessage(),
                            m_lastToken.getFilename(), m_lastToken.getLine(),
                            m_lastToken.getColumn());
                }
            } else {
                val = m_macros.getVal(macnm);
            }
            //need to rescan substitution so push back into buffer
            //in case of nested `ops.
            InputBufferX bufx = downCast(getLexer().getInputState().getInput());
            bufx.prepend(val);
            setToken(Token.SKIP);
        } else {
            setToken(TIC_MACRO, "`" + macnm);
        }
    }

    private void setToken(int type, String txt) {
        Token tok = getLexer().getTokenObject();
        assert (null != tok);
        tok.setType(type);
        tok.setText(txt);
    }
    public final static String stNul = "";

    private void setToken(int type) {
        setToken(type, stNul);
    }

    private void setSkipToken() {
        setToken(Token.SKIP);
    }

    /**
     * Expect token to be of specific type.
     * @param tok token to check.
     * @param type type to verify.
     * @param typeNm name of type.
     * @throws RecognitionException if no match.
     */
    private void expect(Token tok, int type) throws RecognitionException {
        expect(tok, new int[]{type});
    }

    /**
     * Expect token to be one of specific type(s).
     * @param tok token to check.
     * @param type type(s) to verify.
     * @return type of token.
     * @throws RecognitionException if no match.
     */
    private int expect(Token tok, int type[]) throws RecognitionException {
        StringBuilder msg = new StringBuilder("found '");
        msg.append(tok.getText()).append("', expected ");
        for (int i = 0; i < type.length; i++) {
            if (0 < i) {
                msg.append(", ");
            }
            msg.append(stToknameByType.get(type[i]));
            if (tok.getType() == type[i]) {
                return tok.getType();
            }
        }
        throw new RecognitionException(msg.toString(), tok.getFilename(), tok.getLine(), tok.getColumn());
    }

    /**
     * Skip comments and whitespace until specified type.
     * @param type skip tokens until this type is found.
     * @return token which matched type.
     */
    private Token skipUntil(int type) throws TokenStreamException, RecognitionException {
        Token tok = getNextToken();
        expect(tok, type);
        return tok;
    }

    private void getDefineBody()
            throws TokenStreamException, RecognitionException, CharStreamException {
        Token tok = skipUntil(IDENT);
        macroBegin(tok);
        //there would be no space betweem IDENT and (, as per LRM
        if ('(' == getLexer().LA(1)) {
            getFormalArgs();
        }
        getMacroText();
        setSkipToken();
    }

    private void getFormalArgs()
            throws TokenStreamException, RecognitionException, CharStreamException {
        getLexer().match('(');
        Token tok = getNextToken();
        while (true) {
            if (RPAREN == tok.getType()) {
                break;  //loop
            }
            tok = getFormalArg(tok);
        }
        //loop breaks w/ tok==)
        setSkipToken();
    }

    private Token getFormalArg(Token tok)
            throws TokenStreamException, RecognitionException, CharStreamException {
        String dflt = null;
        expect(tok, IDENT);
        Token id = tok;
        tok = getNextToken();
        if (EQ == tok.getType()) {
            dflt = getDefaultText();
            if (COMMA == m_lastToken.getType()) {
                tok = getNextToken();
            }
        }
        addMacroParm(id, dflt);
        return tok;
    }

    /**
     * Slurp up macro text, and do not expand any `op found during slurp.
     */
    private void getMacroText() throws TokenStreamException, CharStreamException {
        m_slurpingMacroText = true;
        StringBuilder bld = new StringBuilder();
        Token tok;
        do {
            tok = getLexer().nextToken();
            bld.append(tok.getText());
        } while (NL != tok.getType());
        macroEnd(bld.toString());
        m_slurpingMacroText = false;
    }

    private List<String> getActualArgs()
            throws TokenStreamException, RecognitionException, CharStreamException {
        List<String> args = new LinkedList<String>();
        Token tok = getNextToken();
        int lastType;
        expect(tok, LPAREN);
        while (true) {
            String arg = getDefaultText();
            args.add(arg);
            //Upon return, will have hit ,|)
            tok = m_lastToken;
            lastType = tok.getType();
            if (RPAREN == lastType) {
                break;  //loop
            }
        }
        return args;
    }

    private static enum IfdefState {

        eDone, eNotDone, eBlockDone;

        public boolean pass() {
            return (this == eDone);
        }
    }

    private void ticIfdef(String key) {
        boolean rval = isDefined(key);
        m_ifdefStack.push(next(rval ? IfdefState.eDone
                : IfdefState.eNotDone));
    }

    private void ticIfndef(String key) {
        boolean rval = !isDefined(key);
        m_ifdefStack.push(next(rval ? IfdefState.eDone
                : IfdefState.eNotDone));
    }

    private void ticElse() {
        IfdefState was = m_ifdefStack.pop();
        IfdefState nxt = (was == IfdefState.eDone)
                ? IfdefState.eBlockDone : IfdefState.eDone;
        m_ifdefStack.push(next(nxt));
    }

    private void ticElsif(String key) {
        IfdefState was = m_ifdefStack.pop();
        IfdefState nxt = (was == IfdefState.eDone) ? IfdefState.eBlockDone
                : (isDefined(key) ? IfdefState.eDone : IfdefState.eNotDone);
        m_ifdefStack.push(next(nxt));
    }

    /**Alter next state if previous is block*/
    private IfdefState next(IfdefState dflt) {
        IfdefState ns = dflt;
        if (!m_ifdefStack.empty()
                && (IfdefState.eDone != m_ifdefStack.peek())) {
            ns = IfdefState.eBlockDone;
        }
        return ns;
    }

    private void ticEndif() {
        assert (!m_ifdefStack.empty());
        m_ifdefStack.pop();
    }

    /**Return true to parser if can pass token.
     * This way we can block tokens while processing ifdef blocks.
     */
    private boolean passToken() {
        return (m_ifdefStack.isEmpty()) ? true : m_ifdefStack.peek().pass();
    }

    /**
     * Create lexer with ability to push-back `macro expansions.
     */
    private static class TokenSource {

        private TokenSource(String fname) {
            m_fname = fname;
            try {
                m_rdr = new FileReader(fname);
            } catch (Exception ex) {
                fatal(ex);
            }
            CharBufferX buf = new CharBufferX(m_rdr);
            LexerSharedInputState sis = new LexerSharedInputState(buf);
            m_lexer = new PreprocLexer(sis);
            m_lexer.setFilename(m_fname);
        }

        private PreprocLexer getLexer() {
            return m_lexer;
        }

        private void close() {
            try {
                m_rdr.close();
            } catch (Exception ex) {
                fatal(ex);
            }
        }
        private final String m_fname;
        private FileReader m_rdr;
        private PreprocLexer m_lexer;
    }

    /**
     * Create and push new token source.
     * @param fname filename of new token source.
     */
    private void push(String fname) {
        m_tokSrcStack.push(new TokenSource(fname));
    }
    
    /**
     * Pop token source and close it.
     */
    private void pop() {
        TokenSource ts = m_tokSrcStack.pop();
        ts.close();
    }
    
    /**
     * Stack of token sources
     */
    private Stack<TokenSource> m_tokSrcStack = new Stack<TokenSource>();
    /**
     * Stack of ifdef states
     */
    private Stack<IfdefState> m_ifdefStack = new Stack<IfdefState>();
    private boolean m_slurpingMacroText = false;
    private StringBuilder m_sbld;
    private MacroDefns m_macros = new MacroDefns();
    private List<Parm> m_macroParms;
    private String m_macroName;
    private Token m_lastToken = null;
    /**
     * Valid search paths for include files.
     */
    private List<File> m_inclSearchPaths = new LinkedList<File>();
    private static final HashMap<Integer, String> stToknameByType =
            new HashMap<Integer, String>();

    static {
        stToknameByType.put(IDENT, "<ident>");
        stToknameByType.put(STRING, "<string>");
        stToknameByType.put(IMPL_FILENAME, "<filename>");
    }
}

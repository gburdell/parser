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
package parser.v2k.preprocessor;
import  java.util.*;
import parser.LaBuffer;
import parser.LaBuffer.Producer;
import static parser.MessageMgr.message;

/**
 *
 * @author kpfalzer
 */
public class FormalArgsParser {

    private static enum EState {
        eStart, eError, eGotName, eGetDefault, eDone
    }

    FormalArgsParser(String parmsAndDefn, Location start) {
        Reader rdr = new StringReader(parmsAndDefn, start);
        m_la = new LA(rdr, stLaN);
    }

    boolean parse() {
        //TODO: catch premature EOF?
        expect('(');
        while (EState.eDone != m_state && EState.eError != m_state) {
            m_tok = m_la.la(0);
            //Skip all whitespace
            if (m_tok.isWhiteSpace()) {
                accept();
            } else {
                switch (m_state) {
                    case eStart:
                        if (match(')')) {
                            m_state = EState.eDone;
                            accept();
                        } else if (expect(Tokenizer.EType.eIdent, "<ident>")) {
                            addParam();
                        } //else {error state already set}
                        break;
                    case eGotName:
                        //last m_parms added
                        /*
                         * Could be ) , = =, =)  where last 2 are empty text default
                         */
                        if (match("=,") || match("=)")) {
                            m_last.setDefault(stEmptyString);
                            m_state = (m_la.la(1).asChar()==')') ? EState.eDone : EState.eStart;
                            m_last = null;
                            accept(m_matchCnt);
                        } else {
                            if (match(')')) {
                                m_state = EState.eDone;
                            } else if (match(',')) {
                                m_state = EState.eStart;
                            } else if (match('=')) {
                                m_state = EState.eGetDefault;
                            } else {
                                unexpected();
                            }
                            accept();
                        }
                        break;
                    case eGetDefault:
                        String dflt = slurpDefault();
                        assert(null != dflt);
                        m_last.setDefault(dflt);
                        m_state = EState.eStart;
                        break;
                }
            }
        }
        m_slurper = new StringBuilder(stEmptyString);
        boolean ok = (EState.eError != m_state);
        while (ok && !m_tok.isEOF()) {
            slurp();
        }
        return ok;
    }
    
    public String getMacroText() {
        return m_slurper.toString();
    }

    private String slurpDefault() {
        //TODO: catch premature EOF?
        //TODO: slurp up default value starting w/ current tok.
        //make sure to balance (), [], {}
        m_slurper = new StringBuilder();
        char c;
        while (EState.eGetDefault == m_state) {
            m_tok = m_la.la(0);
            assert(!m_tok.isEOF());
            if (match(',')) {
                accept();
                break;  //while
            } else if (match(')')) {
                //do NOT accept, since want eStart to detect and be done.
                break;
            } else if (! slurpClosure()) {
                slurp();
            }
        }
        return m_slurper.toString();
    }
    
    private static final char[][] stClosure = {{'(',')'},{'[',']'},{'{','}'}};
    
    private boolean slurpClosure() {
        boolean rval = false;
        char end = 0;
        for (char cl[] : stClosure) {
            if (match(cl[0])) {
                rval = true;
                end = cl[1];
                slurp();
                break;
            }
        }
        boolean loop = rval;
        while (loop) {
            m_tok = m_la.la(0);
            assert(! m_tok.isEOF());
            if (match(end)) {
                slurp();
                loop = false;
            } else if (! slurpClosure()) {
                slurp();                
            }            
        }
        return rval;
    }
    
    private void slurp() {
        m_tok = accept();
        if (! m_tok.isEOF()) {
            m_slurper.append(m_tok.m_tokText); 
        }
    }
    
    private void unexpected() {
        message('E', "VPP-5", m_tok.m_tokText);
        m_state = EState.eError;
    }
    
    private void addParam() {
        m_last = new Parm(m_tok.m_tokText);
        accept();
        m_parms.add(m_last);
        m_state = EState.eGotName;        
    }
    
    private Tok accept() {
        return m_la.accept();
    }
    
    private void accept(int n) {
        m_la.accept(n);
    }
    
    List<Parm> getParms() {
        return m_parms;
    }

    boolean match(char c) {
        return match(m_tok, c);
    }
    
    boolean match(Tok tok, char c) {
        char ch = 0xFF;
        switch (tok.m_tokType) {
            case eChar:
                ch = tok.asChar();
                break;
            case eIdent:
                if (1 == tok.m_tokText.length()) {
                    ch = tok.asChar();
                }
                break;
        }
        boolean rval = (ch == c);
        return rval;
    }
    
    boolean match(char[] cs, boolean skipWhite) {
        boolean rval = true;
        m_matchCnt = 0;
        while (rval && (m_matchCnt < cs.length)) {
            if (! (skipWhite && m_la.la(m_matchCnt).isWhiteSpace())) {
                rval &= match(m_la.la(m_matchCnt), cs[m_matchCnt]);
            }
            m_matchCnt++;
        }
        return rval && (m_matchCnt >= cs.length);
    }
    
    boolean match(final String s, boolean skipWhite) {
        return match(s.toCharArray(), skipWhite);
    }
    
    boolean match(final String s) {
        return match(s, true);
    }
    
    boolean expect(char c) {
        m_tok = m_la.la(0);
        boolean rval = match(c);
        if (! rval) {
            message('E', "VPP-3", m_tok.m_loc.toString(), c, m_tok.m_tokText);
            m_state = EState.eError;
        }
        accept();
        return rval;
    }

    boolean expect(Tokenizer.EType tokType, String asText) {
        boolean rval = (m_tok.m_tokType == tokType);
        if (! rval) {
            message('E', "VPP-4", m_tok.m_loc.toString(), asText, m_tok.m_tokText);
            m_state = EState.eError;            
        }
        return rval;
    }
    
    /**
     * Assemble lookahead data.
     */
    private static class Tok {
        Tok(Tokenizer tokz, Location loc) {
            m_loc = loc.dup();
            m_tokType = tokz.nextToken();
            m_tokText = (m_tokType != Tokenizer.EType.eEOF) ? tokz.getText() : null;
        }
        boolean isWhiteSpace() {
            return m_tokType == Tokenizer.EType.eWhiteSpace;
        }
        char asChar() {
            assert (Tokenizer.EType.eChar==m_tokType || 
                    (Tokenizer.EType.eIdent==m_tokType) && (1==m_tokText.length()));
            return m_tokText.charAt(0);
        }
        boolean isEOF() {
            return (null == m_tokText); //see constructor
        }
        private Tokenizer.EType m_tokType; 
        private String          m_tokText;
        private Location        m_loc;
    }
    private static class LA extends LaBuffer<Tok> {
        static class P implements Producer<Tok> {
            P(Reader rdr) {
                m_rdr = rdr;
                m_tokz = new Tokenizer(rdr);
            }
            private Reader      m_rdr;
            private Tokenizer   m_tokz;
            public Tok getNext() {
                return new Tok(m_tokz, m_rdr.getLocation());
            }
        }
        public LA(Reader rdr, int n) {
            super(new P(rdr), new Tok[n]);
        }        
    }
    /**
     * Lookahead depth.
     */
    private static final int stLaN = 3;
    private List<Parm>  m_parms = new LinkedList<Parm>();
    private Parm        m_last = null;
    private LA          m_la;
    private EState      m_state = EState.eStart;
    private Tok         m_tok = null;
    private static final String stEmptyString = "";
    private int         m_matchCnt = 0;
    private StringBuilder   m_slurper = null;
}

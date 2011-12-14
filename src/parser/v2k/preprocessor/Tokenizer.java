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
import static parser.Utils.invariant;
import  parser.LaBuffer;

/**
 *
 * @author kpfalzer
 */
/**
 * Tokenizer for getting formal args.
 */
public class Tokenizer {
    public Tokenizer(Reader rdr) {
        m_la = new LA(rdr, 3);
    }

    private LA              m_la;
    private StringBuilder   m_text;

    static enum EType {
        eChar,
        eString,    // "foo" or 'c'
        eIdent,
        eWhiteSpace,
        eEOF,
    }

    private static enum EState {
        eReset,
        eInBlockComment,
        eIdent,
        eWhiteSpace,
        eString
    }

    private EState  m_state;

    String getText() {
        return m_text.toString();
    }

    //TODO: embelish w/ location
    EType nextToken() {
        EType type = EType.eEOF;
        m_state = EState.eReset;
        m_text = new StringBuilder();
        boolean stay = true;
        while (stay && !m_la.isEOF()) {
            char nchar = m_la.accept();
            m_text.append(nchar);
            switch(m_state) {
                case eReset:
                    if ('/'==nchar && '*'==m_la.la(0)) {
                        m_text.append(m_la.accept());
                        m_state = EState.eInBlockComment;
                    } else if ('"'==nchar) {
                        m_state = EState.eString;
                    } else if ('\''==nchar) {
                        char la[] = {m_la.la(0),m_la.la(1),m_la.la(2)};
                        type = EType.eString;
                        if ('\''==la[0]) {
                            m_text.append(m_la.accept());
                        } else if ('\\'==la[0]) {
                            invariant('\''==la[2]);
                            m_text.append(la[0]).append(la[1]).append(la[2]);
                            m_la.accept(3);
                        } else if ('\''==la[1]) {
                            m_text.append(la[0]).append(la[1]);
                            m_la.accept(2);
                        } else {
                            type = EType.eChar;
                        }
                        stay = false;
                    } else if (isIdent(nchar, true)) {
                        type = EType.eIdent;
                        if (isIdent(m_la.la(0))) {
                            m_state = EState.eIdent;
                        } else {
                            stay = false;
                        }
                    } else if (isWhiteSpace(nchar)) {
                        type = EType.eWhiteSpace;
                        if (isWhiteSpace(m_la.la(0))) {
                            m_state = EState.eWhiteSpace;
                        } else {
                            stay = false;
                        }
                    } else {
                        type = EType.eChar;
                        stay = false;
                    }
                    break;
                case eWhiteSpace:
                    stay = isWhiteSpace(m_la.la(0));
                    break;
                case eIdent:
                    stay = isIdent(m_la.la(0));
                    break;
                case eInBlockComment:
                    if ('*'==nchar && '/'==m_la.la(0)) {
                        m_text.append(m_la.accept());
                        m_text = new StringBuilder();   //punt comment
                        m_state = EState.eReset;
                    }
                    break;
                case eString:
                    if ('"'==nchar) {
                        type = EType.eString;
                        stay = false;
                    } else if ('\\'==nchar) {
                        invariant(!m_la.isEOF());
                        m_text.append(m_la.accept());
                    }
                    break;
                default:
                    assert(false);
            }
        }
        return type;
    }

    boolean isWhiteSpace(char c) {
        return Character.isWhitespace(c);
    }

    boolean isIdent(char ch, boolean isStart) {
        boolean ok = ('_'==ch) || Character.isLetter(ch);
        ok = ok || (!isStart && Character.isDigit(ch));
        return ok;
    }

    boolean isIdent(char ch) {
        return isIdent(ch, false);
    }

    /**
     * Read characters from Reader with fixed lookahead.
     * @author kpfalzer
     */
    private static class LA {
        private static class Buf extends LaBuffer<Integer> {
            static class P implements Producer<Integer> {
                P(Reader r) {
                    m_rdr = r;
                }
                public Integer getNext() {
                    return m_rdr.getNextChar();
                }
                Reader  m_rdr;
            }
            Buf(Reader rdr, int n) {
                super(new P(rdr), new Integer[n], 0);
            }
        }
        public LA(Reader rdr, int n) {
            m_buf = new Buf(rdr, n);
        }
        public boolean isEOF() {
            return (0 > m_buf.la(0));
        }
        public char la(int n) {
            return (char)m_buf.la(n).intValue();
        }
        public char accept() {
            return (char)m_buf.accept().intValue();
        }
        public void accept(int n) {
            m_buf.accept(n);
        }
        private Buf m_buf;
    }
}

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
package parser.slf3;

import gblib.FileLineCharReader;
import gblib.FileLocation;
import gblib.Util;
import gblib.Util.Triplet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.BitSet;
import java.util.regex.Pattern;

/**
 *
 * @author gburdell
 */
public class Tokenizer {

    public Tokenizer(final String fname) throws FileNotFoundException {
        m_is = new FileLineCharReader(fname);
    }

    public static enum EType {

        eEOF, eString, eNumber, eIdent,
        eCellK, eLibraryK,
        eLeftParen, eRightParen,
        eLeftCurly, eRightCurly,
        eLeftBracket, eRightBracket,
        eOther
    }

    public static class SyntaxError extends Exception {
    }

    public class Token extends Triplet<EType, FileLocation, String> {

        public Token(final EType type, final FileLocation loc, final String s) {
            super(type, loc, s);
            m_buf.setLength(0);
        }

        public Token(final EType type, final FileLocation loc) {
            this(type, loc, m_buf.toString());
        }

        public boolean oneOf(final EType... args) {
            return 0 <= Util.linearSearch(args, e1);
        }
    }

    public boolean isEOF() throws IOException {
        return m_is.isEOF();
    }

    public Token nextToken() throws IOException, SyntaxError {
        Token tok = null;
        char la0;
        while (null == tok) {
            if (isEOF()) {
                return new Token(EType.eEOF, m_is.getLocation(), null);
            }
            la0 = la();
            if (Character.isWhitespace(la0)) {
                accept();
            } else if ('"' == la0) {
                tok = string();
            } else if (la0 == '/' && la(1) == '*') {
                blockComment();
            } else {
                if (null != (tok = number(la0, la(1)))) {
                    //nothing
                } else if (null != (tok = ident(la0))) {
                    //nothing
                } else {
                    switch (la0) {
                        case '[':
                            tok = acceptChar(EType.eLeftBracket);
                            break;
                        case ']':
                            tok = acceptChar(EType.eRightBracket);
                            break;
                        case '(':
                            tok = acceptChar(EType.eLeftParen);
                            break;
                        case ')':
                            tok = acceptChar(EType.eRightParen);
                            break;
                        case '{':
                            tok = acceptChar(EType.eLeftCurly);
                            break;
                        case '}':
                            tok = acceptChar(EType.eRightCurly);
                            break;
                        default:
                            tok = acceptChar(EType.eOther);
                    }
                }
            }
        }
        return tok;
    }

    private Token acceptChar(final EType type) throws IOException {
        final FileLocation begin = m_is.getLocation();
        append(stVerbose);
        return new Token(type, begin);
    }

    /**
     * Create lookup to qualify ident characters. [0] is fot 1st char; [1] is
     * for subsequent. And, yes; the funny ./+- are legit... as found
     * empirically. (see parser/slf/slg.g)
     */
    private static final BitSet stIdent[] = new BitSet[]{new BitSet(256), new BitSet(256)};

    static {
        for (char c = 'a'; c <= 'z'; c++) {
            stIdent[0].set(c);
            stIdent[1].set(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            stIdent[0].set(c);
            stIdent[1].set(c);
        }
        stIdent[0].set('_');
        for (char c = '0'; c <= '9'; c++) {
            stIdent[1].set(c);
        }
        for (char c : new char[]{'_', '.', '/', '+', '-'}) {
            stIdent[1].set(c);
        }
    }

    private Token ident(final char la0) throws IOException {
        //('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'.'|'/'|'+'|'-')*
        if (!stIdent[0].get(la0)) {
            return null;
        }
        final FileLocation begin = m_is.getLocation();
        append();
        while (stIdent[1].get(la())) {
            append();
        }
        EType type;
        switch (m_buf.toString()) {
            case "library":
                type = EType.eLibraryK;
                break;
            case "cell":
                type = EType.eCellK;
                break;
            default:
                type = EType.eIdent;
        }
        return new Token(type, begin);
    }

    private boolean isInteger(final char la0, final char la1) throws IOException {
        return (Character.isDigit(la0)
                || (('+' == la0 || '-' == la0) && Character.isDigit(la1)));
    }

    /**
     * Detect number and process, else return null.
     *
     * @param la0 la(0)
     * @param la1 la(1)
     * @return number or null.
     * @throws IOException
     */
    private Token number(final char la0, final char la1) throws IOException, SyntaxError {
        if (!isInteger(la0, la1)) {
            return null;
        }
        final FileLocation begin = m_is.getLocation();
        append(stVerbose);
        if ('+' == la0 || '-' == la0) {
            append(stVerbose);
        }
        while (Character.isDigit(la())) {
            append(stVerbose);
        }
        if ('.' == la()) {
            append(stVerbose);
            if (!Character.isDigit(la())) {
                syntaxError(4, la(), "[0-9]");
            }
            while (Character.isDigit(la())) {
                append(stVerbose);
            }
        }
        if ('E' == la() || 'e' == la()) {
            append(stVerbose);
            if ('+' == la() || '-' == la()) {
                append(stVerbose);
            }
            if (!Character.isDigit(la())) {
                syntaxError(4, la(), "[0-9]");
            }
            while (Character.isDigit(la())) {
                append(stVerbose);
            }
        }
        if (stDebug) {
            assert Pattern.matches("[\\+\\-]?[0-9]+(\\.[0-9]+)?([eE][\\+\\-]?[0-9]+)?", m_buf);
        }
        return new Token(EType.eNumber, begin);
    }

    private void append(final boolean append) throws IOException {
        final char c = accept();
        if (append) {
            m_buf.append(c);
        }
    }

    private void append() throws IOException {
        append(true);
    }

    private Token string() throws IOException, SyntaxError {
        final FileLocation begin = m_is.getLocation();
        append();
        char c;
        while (!isEOF()) {
            c = accept();
            m_buf.append(c);
            if ('\\' == c) {
                append();
            } else if ('"' == c) {
                break;
            }
        }
        if (isEOF()) {
            syntaxError(2);
        }
        return new Token(EType.eString, begin);
    }

    private void blockComment() throws IOException, SyntaxError {
        accept(2);
        while (!isEOF()) {
            if ('/' == la() && '*' == la(1)) {
                syntaxError(1);
            } else if ('*' == la() && '/' == la(1)) {
                accept(2);
                break;
            } else {
                accept();
            }
        }
        if (isEOF()) {
            syntaxError(2);
        }
    }

    private void syntaxError(final int code) throws SyntaxError {
        syntaxError("SLF-" + code, m_is.getLocation());
    }

    private void syntaxError(final int code, final char s1, final String s2) throws SyntaxError {
        syntaxError("SLF-" + code, m_is.getLocation(), Character.toString(s1), s2);
    }

    private void syntaxError(final String code, Object... args) throws SyntaxError {
        Messages.message(code, args);
        throw new SyntaxError();
    }

    private char la(int n) throws IOException {
        return m_is.la(n);
    }

    private char la() throws IOException {
        return la(0);
    }

    private char accept(int n) throws IOException {
        return m_is.accept(n);
    }

    private char accept() throws IOException {
        return accept(1);
    }

    private final StringBuilder m_buf = new StringBuilder();
    private final FileLineCharReader m_is;

    /**
     * Enable verbosity by retaining token string details where not really
     * needed.
     */
    public static boolean stVerbose = true;
    public static boolean stDebug = false;
}

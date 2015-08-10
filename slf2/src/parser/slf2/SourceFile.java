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
package parser.slf2;

import gblib.FileCharReader;
import gblib.FileLocation;
import gblib.Util.Pair;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 *
 * @author gburdell
 */
public class SourceFile extends FileCharReader {

    public SourceFile(String fname) throws FileNotFoundException {
        super(fname, true);
    }

    private static final Pattern stString = Pattern.compile("\\\"(.*?)\\\"");
    private static final Pattern stSpacing = Pattern.compile("[ \\t]+");
    private static final Pattern stIdent = Pattern.compile("([a-zA-Z_]\\w*)(?=\\W)");
    private static final Pattern stLibrary = Pattern.compile("library(?=\\W)");
    private static final Pattern stCell = Pattern.compile("cell(?=\\W)");

    public boolean parse() {
        boolean ok = false;
        try {
            while (!isEOF()) {
                setRemainder();
                if (skipSpacing()) {
                    //do nothing
                } else if (acceptOnMatch(stLibrary)) {
                    ok = library();
                } else {
                    assert false;//todo: error
                }
            }
            if (!ok) {
               assert false; //todo: error
            }
        } catch (ParseError ex) {
            assert false;//todo
        }
        return ok;
    }

    private boolean skipSpacing() throws ParseError {
        boolean match = true;
        if (acceptOnMatch(stBlockComment[0])) {
            blockComment();
        } else if (acceptOnMatch(stSpacing)) {
            //do nothing
        } else if (isEOL()) {
            next();
        } else {
            match = false;
        }
        return match;
    }

    private static enum EState {

        eLibLparen, eLibraryName, eLibRparen, eLibraryBody,
        eCellLparen, eCellName, eCellRparen, eCellBody,
        eWaitForEOF
    }

    private static final Pattern stLibraryName
            = Pattern.compile("()|");

    private boolean library() throws ParseError {
        //entry upon library
        EState state = EState.eLibLparen;
        while (!isEOF()) {
            setRemainder();
            if (!skipSpacing()) {
                switch (state) {
                    case eLibLparen:
                        if (acceptOnMatch('(')) {
                            state = EState.eLibraryName;
                        } else {
                            assert false;//todo: error
                        }
                        break;
                    case eLibraryName:
                        if (acceptOnMatch(stIdent, stString)) {
                            state = EState.eLibRparen;
                        } else {
                            assert false;//todo: error
                        }
                        break;
                    case eLibRparen:
                        if (acceptOnMatch(')')) {
                            state = EState.eLibraryBody;
                        } else {
                            assert false;//todo: error
                        }
                        break;
                    case eLibraryBody:
                    case eCellBody:
                        final char c = (char) la();
                        int n;
                        if (m_closures.empty() && (c != '{')) {
                            assert false;//todo: error
                        } else if ((1 == m_closures.size())
                                && (EState.eCellBody == state)
                                && (c != '{')) {
                            assert false;//todo: error
                        }
                        if (acceptOnMatch(stString)) {
                            //do nothing
                        } else if (0 <= (n = stClosures[0].indexOf(c))) {
                            //add the matching/closure
                            m_closures.add(new Pair<>(getFileLocation(),stClosures[1].charAt(n)));
                            next();
                        } else if (c == m_closures.peek().e2) {
                            if (!m_closures.empty()) {
                                m_closures.pop();
                                if ('}' == c) {
                                    if (m_closures.empty()) {
                                        state = EState.eWaitForEOF;
                                    } else if ((1 == m_closures.size())) {
                                        state = EState.eLibraryBody;
                                    }
                                }
                                next();
                            } else {
                                assert false;//todo: error
                            }
                        } else if (0 <= (n = stClosures[1].indexOf(c))) {
                            syntaxError(c);//todo: doesn't match closure so error
                        } else if (acceptOnMatch(stCell)) {
                            state = EState.eCellLparen;
                        } else {
                            next();
                        }
                        break;
                    case eCellLparen:
                        if (acceptOnMatch('(')) {
                            state = EState.eCellName;
                        } else {
                            assert false;//todo: error
                        }
                        break;
                    case eCellName:
                        if (acceptOnMatchSave(stIdent, stString)) {
                            addCell();
                            state = EState.eCellRparen;
                        } else {
                            assert false;//todo: error
                        }
                        break;
                    case eCellRparen:
                        if (acceptOnMatch(')')) {
                            state = EState.eCellBody;
                        } else {
                            assert false;//todo: error
                        }
                        break;
                    case eWaitForEOF:
                    //fall through
                    default:
                        assert false;//todo: error
                }
            }
        }
        final boolean ok = (state == EState.eWaitForEOF);
        return ok;
    }
    
    private void syntaxError(final char c) {
        final String loc = getLocation();
        System.err.printf("Error: %s: syntax error at '%c'\n",loc,c);
        assert false;
    }
    
    private void addCell() {
        final FileLocation loc = getMatched().peek().e1;
        final String cellNm = getMatched().remove().e2;
        
    }

    private static final String[] stClosures = new String[]{"({[", ")}]"};

    private final Stack<Pair<FileLocation,Character>> m_closures = new Stack<>();
    private final Map<String,FileLocation>  m_locByCell = new HashMap<>();

    private class SlfParseError extends Exception {

    }
}

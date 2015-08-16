//The MIT License
//
//Copyright (c) 2015-      George P. Burdell
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
package parser.slf3;

import gblib.FileLocation;
import gblib.MessageMgr;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import parser.slf3.Tokenizer.EType;
import parser.slf3.Tokenizer.Token;

/**
 *
 * @author kpfalzer
 */
public class Parser {

    public Parser() {
    }

    private static enum EState {

        eLibraryBody,
        eCellLparen, eCellName, eCellRparen, eCellBody,
        eWaitForEOF
    }

    private int m_cellCnt = 0;
    
    protected void parse(String fname) {
        m_cellCnt = 0;
        try {
            final Tokenizer lexer = new Tokenizer(fname);
            Token tok = lexer.nextToken();
            if (EType.eLibraryK != tok.e1) {
                parseError(4, tok, "library");
            }
            tok = lexer.nextToken();
            if (EType.eLeftParen != tok.e1) {
                parseError(4, tok, "(");
            }
            tok = lexer.nextToken();
            if (!tok.oneOf(EType.eString, EType.eIdent)) {
                parseError(4, tok, "ident | string");
            }
            final String libNm = tok.e3;
            tok = lexer.nextToken();
            if (EType.eRightParen != tok.e1) {
                parseError(4, tok, ")");
            }
            tok = lexer.nextToken();
            if (EType.eLeftCurly != tok.e1) {
                parseError(4, tok, "{");
            }
            EState state = EState.eLibraryBody;
            m_closures.add(EType.eRightCurly);
            while ((tok = lexer.nextToken()).e1 != EType.eEOF) {
                switch (state) {
                    case eLibraryBody:
                        if (EType.eCellK == tok.e1 && (1 == m_closures.size())) {
                            state = EState.eCellLparen;
                        } else if (matchLeftClosures(tok)) {
                            //do nothing
                        } else if (matchRightClosures(tok)) {
                            if (m_closures.empty()) {
                                state = EState.eWaitForEOF;
                            }
                        }
                        break;
                    case eCellLparen:
                        if (tok.e1 != EType.eLeftParen) {
                            parseError(4, tok, "(");
                        }
                        state = EState.eCellName;
                        break;
                    case eCellName:
                        if (!tok.oneOf(EType.eString, EType.eIdent)) {
                            parseError(4, tok, "ident | string");
                        }
                        addCell(tok);
                        state = EState.eCellRparen;
                        break;
                    case eCellRparen:
                        if (tok.e1 != EType.eRightParen) {
                            parseError(4, tok, ")");
                        }
                        state = EState.eCellBody;
                        break;
                    case eCellBody:
                        if (matchLeftClosures(tok)) {
                            //do nothing
                        } else if (matchRightClosures(tok)) {
                            if (1 == m_closures.size()) {
                                state = EState.eLibraryBody;
                            }
                        }
                        break;
                    default:
                        assert false;
                }
            }
            if (state != EState.eWaitForEOF) {
                parseError(3, tok);
            }
        } catch (FileNotFoundException ex) {
            MessageMgr.message('E', "FILE-2", new Object[]{fname, "read"});
        } catch (Tokenizer.SyntaxError | ParseError ex) {
            //do nothing
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean matchLeftClosures(final Tokenizer.Token tok) throws ParseError {
        boolean match = true;
        switch (tok.e1) {
            case eLeftBracket:
                m_closures.push(EType.eRightBracket);
                break;
            case eLeftCurly:
                m_closures.push(EType.eRightCurly);
                break;
            case eLeftParen:
                m_closures.push(EType.eRightParen);
                break;
            default:
                match = false;
        }
        if (match) {
            if (m_closures.size() > stMaxStackSize) {
                parseError(6, tok, "closure stack > " + stMaxStackSize);
            }
            if (1 == m_closures.size() && EType.eRightCurly != m_closures.peek()) {
                parseError(6, tok, "unexpected closure start");
            }
        }
        return match;
    }

    private static final int stMaxStackSize = 1024;

    private boolean matchRightClosures(final Tokenizer.Token tok) throws ParseError {
        boolean match = tok.oneOf(EType.eRightBracket, EType.eRightCurly, EType.eRightParen);
        if (match) {
            if (m_closures.empty()) {
                parseError(6, tok, "unmatched closure");
            }
            final EType expect = m_closures.pop();
            if (tok.e1 != expect) {
                parseError(4, tok, stRightClosures.get(expect));
            }
        }
        return match;
    }

    private static final Map<EType, String> stRightClosures = new HashMap<>();

    static {
        stRightClosures.put(EType.eRightBracket, "]");
        stRightClosures.put(EType.eRightCurly, "}");
        stRightClosures.put(EType.eRightParen, ")");
    }
    private final Stack<EType> m_closures = new Stack<>();

    public static class ParseError extends Exception {
    }

    private void parseError(int code, final Tokenizer.Token tok, final String more) throws ParseError {
        Messages.message("SLF-" + code, tok.e2.toString(), tok.e3, more);
        throw new ParseError();
    }

    private void parseError(int code, final Tokenizer.Token tok) throws ParseError {
        final String at = (tok.e1 != EType.eEOF) ? tok.e3 : "<EOF>";
        Messages.message("SLF-" + code, tok.e2.toString(), at);
        throw new ParseError();
    }

    public void parse(String fnames[]) {
        for (String fn : fnames) {
            parse(fn);
            Messages.message("SLF-5", fn, m_cellCnt);
        }
    }

    private void addCell(final Token tok) {
        final String nm = (tok.e3.startsWith("\"") 
                ? tok.e3.substring(1, tok.e3.length()-1) : tok.e3);
        stTracker.addModule(nm, tok.e2);
        m_cellCnt++;
    }

    public static class Module extends parser.Module {

        public Module(final String modName, final FileLocation loc) {
            super(parser.Module.EType.eLib, modName, new Location(loc));
        }
    }

    public static class Tracker extends parser.Tracker {

        public void addModule(final String modName, final FileLocation loc) {
            super.addModule(new Module(modName, loc));
        }        
    }

    public static void main(String argv[]) {
        new Parser().parse(argv);
    }

    public static final Tracker stTracker = new Tracker();
}

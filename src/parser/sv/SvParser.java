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
package parser.sv;

import java.io.Reader;
import parser.v2k.IPipedLexer;
import parser.ILexer;
import parser.v2k.Lexer;
import parser.v2k.Parser;
import parser.MessageMgr;
import antlr.TokenStreamException;
import antlr.RecognitionException;
import antlr.LexerSharedInputState;
import java.io.File;
import parser.v2k.Preproc;

/**
 *
 * @author kpfalzer
 */
public class SvParser extends Parser {

    public SvParser() {
    }

    protected void parse(String fname)
            throws TokenStreamException, RecognitionException {
        if (false == fname.equals("<cmdline>")) {
            MessageMgr.message('I', "FILE-3", fname);
        }
        {
            /*
             * Fix bug to create analyze/r1.3
             * Case where d/e/g.v has `include ./h.vh.
             * Need to implicitly add path of source (of g.v): d/e
             */
            File f = new File(fname);
            String dir = f.getParent();
            if (null != dir) {
                Preproc.getTheOne().addSearchPath(dir);
            }
        }
        m_lexer = new SvLexer(fname);
        m_parser = new SysVlogParser(m_lexer);
        m_parser.source_text();
    }

    protected ILexer getLexer() {
        return m_lexer;
    }

    public SysVlogParser getParser() {
        return m_parser;
    }
    private SvLexer m_lexer;
    private SysVlogParser m_parser;

    private static class SvLexer extends SysVlogLexer implements IPipedLexer {

        public SvLexer(String fname) {
            super((LexerSharedInputState) null); //a bogus constructor
            m_lexer = new Lexer(fname, this);
        }

        public void setReader(Reader rdr) {
            super.inputState = new LexerSharedInputState(rdr);
        }

        public Lexer getLexer() {
            return m_lexer;
        }
        private Lexer m_lexer;
    }
}

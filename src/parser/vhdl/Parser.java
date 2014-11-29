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

package parser.vhdl;
import  antlr.Token;
import  parser.MessageMgr;
import  parser.Message;
import  parser.ExceptionBase;
import  parser.Utils;
import  parser.ILexer;
import  antlr.TokenStreamException;
import  antlr.RecognitionException;
import  java.io.FileReader;
import  java.io.FileNotFoundException;
import  java.io.IOException;

/**
 *
 * @author kpfalzer
 */
public class Parser implements ILexer {

    public Parser() {
        init();
    }
    
    private void init() {
        stTheOne = this;
    }

    private static Parser stTheOne = null;

    static public Parser getTheOne() {
        return stTheOne;
    }

    protected void parse(String fname)
            throws TokenStreamException, RecognitionException {
        FileReader rdr = null;
        try {
            MessageMgr.message('I', "FILE-3", fname);
            rdr = new FileReader(fname);
            m_lexer = new VhdlLexer(rdr);
            m_lexer.setFilename(fname);
            m_parser = new VhdlParser(m_lexer);
            m_parser.setFilename(fname);
            m_parser.design_file();
            rdr.close();
        } catch (FileNotFoundException ex) {
            badFile(fname);
        } catch (IOException ex) {
            badFile(fname);
        }
    }

    private static void badFile(String fname) {
        MessageMgr.message('E', "FILE-2", fname, "read");
    }

    @Override
    public String getFilename() {
        return m_parser.getFilename();
    }

    @Override
    public int getLine() {
        return m_lexer.getLine();
    }

    @Override
    public Token nextToken() throws TokenStreamException {
        gblib.Util.invariant(false); //should never use
        return null;
    }

    private static class ParseException extends ExceptionBase {
        public ParseException(char severity, String code, Object ... args) {
            super(severity, code, args);
        }
    }

    public void parse(String fnames[]) {
        for (String fn : fnames) {
            try {
                parse(fn);
            } catch (TokenStreamException ex) {
                if (null != ex.getMessage()) {
                    Message.message(getFilename(), getLine(), ex);
                }
            } catch (Exception ex) {
                if (false == ExceptionBase.class.isInstance(ex)) {
                    gblib.Util.abnormalExit(ex);
                }
            }
        }
    }


    public static void main(String argv[]) {
        new Parser().parse(argv);
    }



    private VhdlLexer   m_lexer;
    private VhdlParser  m_parser;
}

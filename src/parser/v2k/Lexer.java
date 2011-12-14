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
import  static parser.Utils.abnormalExit;
import  static parser.Utils.getPropertyAsBool;
import  parser.MessageMgr;
import  parser.Message;
import  antlr.*;
import  java.io.*;

/**
 * Lexer base class.
 * @author karl
 */
public class Lexer implements ILexer {//TokenStream {

    private static final int stBufSz = Parser.stBufSz;
    
    public Lexer(String fname, IPipedLexer lxr) {
        m_lexer = new VLexer(fname, lxr);
        m_lexer.start();
    }
    
    //Debug preprocessor
    private static final boolean stDBG1 = getPropertyAsBool("DBG1");
    //Debug SysVlog lexer
    private static final boolean stDBG2 = getPropertyAsBool("DBG2");

    //@Override
    public Token nextToken() throws TokenStreamException {
        Token tok = m_lexer.nextToken();
        Message.message(stDBG2, 'I', "DBG-2", tok.getText());
        return tok;
	}        
    
    public String getFilename() {
        return m_lexer.getFilename();
    }
    
    public int getLine() {
        return m_lexer.getLine();
    }
    
   /**The post processed lexer for use by parser.*/
    private VLexer      m_lexer;
    private PipedReader m_reader = new PipedReader();

    private final class VLexer implements ILexer {
        VLexer(String fname, IPipedLexer lxr) {
            lxr.setReader(m_reader);
            lxr.setFilename(fname);
            m_lexer = lxr;
            Message.setLexer(m_lexer);
            try {
                m_writer = new PipedWriter();
                m_reader.connect(m_writer);

                // start thread going and write into pipe
                m_lexerpp = new Lexerpp(getFilename(), m_writer);
            }
            catch (Exception ex) {
                abnormalExit(ex);
            }
        }

        private void start() {
            m_lexerpp.start();
        }

        public String getFilename() {
            return m_lexer.getFilename();
        }

        public int getLine() {
            return m_lexer.getLine();
        }

        public Token nextToken() throws TokenStreamException {
            return m_lexer.nextToken();
        }

        private ILexer      m_lexer;
        private Lexerpp     m_lexerpp;
        private PipedWriter m_writer;
    }

    /**
     * Preprocessor acting as writer side of Pipe.
     */
    private static class Lexerpp extends Thread {
        Lexerpp(String fname, Writer writer) throws IOException {
            InputStream fis = 
                new BufferedInputStream(new FileInputStream(fname), stBufSz);
            m_lexer = new Helper(fis, fname);
            m_writer = writer;
            if (Parser.stDumpPP) {
                String fnm = new File(fname).getPath() + ".E";
                try {
                    m_outf = new FileWriter(fnm);
                    MessageMgr.message('I', "VPP-1", fnm);
                }
                catch (Exception ex) {
                    m_outf = null;
                    MessageMgr.message('W', "VPP-2", fnm);
                }
            }
        }
        
        @Override
        public void run() {
            BufferedWriter bw = new BufferedWriter(m_writer);
            //By putting try{} block here, instead of w/in while(),
            //mitigate issue of cascade (lexing) errors caused by the first one.
            try {
                Token tok;
                int type;
                while (true) {
                    tok = m_lexer.nextToken();
					type = tok.getType();
                    String s;
					if (VlogppLexer.EOF == type) {
                        break; //while
					}
					s = tok.getText();
					m_writer.append(s);
					if (null != m_outf) {
                        m_outf.write(s);
					}
				}
            }
            catch (TokenStreamException ex) {
                //NOTE: enable this for lexer debugging.
                //abnormalExit(ex);
            }
            catch (Exception ex) {
                abnormalExit(ex);
            }
            finally {
                try {
                    if (null != m_outf) {
                        m_outf.close();
                    }
                    bw.close();
                }
                catch (Exception ex) {
                    abnormalExit(ex);
                }
            }
        }
        /**Output preprocessed file.*/
        private Writer  m_outf;
        private Writer  m_writer;
        private Helper  m_lexer;

        private class Helper implements TokenStream, Preproc.IPreproc {
            private Helper(InputStream fis) {
                m_lexer = new VlogppLexer(fis);
                m_pp = Preproc.getTheOne();
                m_pp.setPreproc(this);
            }
            Helper(InputStream fis, String fname) {
                this(fis);
                m_lexer.setFilename(fname);
            }
            
            private static final int eIncl = 1;
            private static final int ePostIncl = 2;
            
            private void addTicLine(String fname, int lnum, int type) {
                StringBuffer s = new StringBuffer("\n`line ")
                    .append(lnum).append(" ").append('"').append(fname)
                    .append("\" ").append(type).append("\n");
                try {
                    m_writer.append(s);
                    if (null != m_outf) {
                        m_outf.append(s);
                    }
                }
                catch (Exception ex) {
                    abnormalExit(ex);
                }
            }
            private void addTicLine(LexerSharedInputState is, int type) {
                addTicLine(is.getFilename(), is.getLine(), type);
            }
            //@Override
            public void pop() throws TokenStreamRetryException {
                if (m_pp.getStack().empty()) 
                    return;
                Preproc.LexState lis = m_pp.getStack().pop();
                m_lexer.setInputState(lis.getState());
                if (lis.getDoLine()) {
                    addTicLine(lis.getState(), ePostIncl);
                }
                throw new TokenStreamRetryException();
            }
            private void retry(VlogppLexer newLexer, boolean addLine) 
                    throws TokenStreamRetryException {
                LexerSharedInputState sst = m_lexer.getInputState();
                Preproc.LexState lis = new Preproc.LexState(sst, addLine);
                m_pp.getStack().push(lis);
                sst = newLexer.getInputState();
                m_lexer.setInputState(sst);
                if (addLine) {
                    addTicLine(sst, eIncl);
                }
                throw new TokenStreamRetryException();
            }
            //@Override
            public void push(File f) throws TokenStreamRetryException {
                BufferedInputStream bis = null;
                VlogppLexer lxr = null;
                try {
                    bis = new BufferedInputStream(new FileInputStream(f), 
                        stBufSz);
                    String fn = Parser.stUseAbsPaths ? 
                        f.getAbsolutePath() : f.getPath();
                    lxr = new VlogppLexer(bis);
                    lxr.setFilename(fn);
                    Message.message(getLocation(), 'I', "INCL-1", fn);
                } catch (Exception ex) 
                {}
                if (null != lxr)
                   retry(lxr, true);
            }
            //@Override
			public void push(String txt) throws TokenStreamRetryException {
			    StringReader rdr = new StringReader(txt);
			    VlogppLexer lxr = new VlogppLexer(rdr);
			    LexerSharedInputState ls = m_lexer.getInputState();
			    lxr.setFilename(ls.getFilename());
			    lxr.setLine(ls.getTokenStartLine());
			    lxr.setColumn(ls.getTokenStartColumn());
				retry(lxr, false);
			}
            //@Override
            public Token nextToken() throws TokenStreamException {
                // return input.nextToken();
                // keep looking for a token until you don't
                // get a retry exception.
                Token tok;
				String text;
				boolean passToken;
                while (true) {
                    try {
                       tok = m_lexer.nextToken();
					   text = tok.getText();
					   passToken = passToken();
					   if (passToken) {
                            Message.message(stDBG1, getLocation(), 
                                'I', "DBG-1", text);
                            return tok;
                       } else {
						   boolean emit = false;
						   switch (tok.getType()) {
							   case VlogppLexerTokenTypes.COMMENT:	//fall through
							   case VlogppLexerTokenTypes.ML_COMMENT: //fall through
							   case VlogppLexerTokenTypes.SL_COMMENT:
								   emit = true;
								   break;
							   default:
								   emit = text.equals("\n");
						   }
						   if (emit) {
								return tok;
						   }
					   }
                    }
                    catch (TokenStreamRetryException r) {
                        // just retry "forever"
                    }
                }
            }
            //@Override
            public VlogLocation getLocation() {
                String fname = m_lexer.getFilename();
                int lnum = m_lexer.getLine();
                VlogLocation loc = new VlogLocation(fname, lnum);
                return loc;
            }
            /**Return true to parser if can pass token.
             * This way we can block tokens while processing ifdef blocks.
             */
            private boolean passToken() {
				return m_pp.passToken();
            }

            private VlogppLexer m_lexer;
            private Preproc     m_pp;
        }
    }
}

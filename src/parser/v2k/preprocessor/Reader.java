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

/**
 *
 * @author kpfalzer
 */
public abstract class Reader {
    public Location getLocation() {
        return m_loc;
    }
    public void setCurrLine(String s, int pos) {
        if (isEOF()) {
            assert(null == s);
        } else {
            assert(EState.eExpectLineUpdate == m_state);
            m_currLine = s;
            m_currPos = pos;
            m_state = EState.eGetChar;
        }
    }
    
    /**
     * Get fresh new line.  It is expected that receiver will process
     * line and then update here.
     * @return new line (with NL appended) or null if end of stream.
     */
    public abstract String getNextLine();
    
    public boolean isEOF() {
        return (EState.eEOF == m_state);
    }
    
    /**
     * Get next char.
     * @return next character, 0/nul or -1 on EOF.
     */
    public abstract int getNextChar();
    
    public EState getState() {
        return m_state;
    }
    public void setInBlockComment(boolean val) {
        m_inBlockComment = val;
    }
    public boolean getInBlockComment() {
        return m_inBlockComment;
    }
    void setState(EState state) {
        m_state = state;
    }
    protected String            m_currLine = null;
    protected int               m_currPos = -1;
    protected boolean           m_inBlockComment = false;
    protected Location          m_loc = null;
    public final static char stNl = '\n';
    
    public static enum EState {
        eGetLine,           //request should get new line.
        eExpectLineUpdate,  //new line was read by preprocessor, so expect
                            //it to update this reader.
        eGetChar,           //expect characters to be read.
        eEOF,               //at end of file.
    }
    protected EState  m_state = EState.eGetLine;
}

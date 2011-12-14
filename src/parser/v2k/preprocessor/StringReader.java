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
public class StringReader extends Reader {
    public StringReader(String s, Location from) {
        init(s, from);
    }
    private void init(String s, Location from) {
        m_currLine = s;
        m_currPos = 0;
        m_state = EState.eGetChar;
        m_loc = new Location(from);
    }
    
    @Override
    public int getNextChar() {
        int nchar = -1;
        final int n = m_currLine.length();
        if (m_currPos < n) {
            nchar = m_currLine.charAt(m_currPos++);
            if (stNl == nchar) {
                m_loc.incrLineNum();
            }
            if (m_currPos >= n) {
                setState(EState.eEOF);
            }
        }
        return nchar;
    }

    @Override
    public String getNextLine() {
        assert false;
        return null;
    }
    
}

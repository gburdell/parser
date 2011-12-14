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

import java.io.LineNumberReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import static parser.MessageMgr.message;
import static parser.Utils.abnormalExit;

/**
 * A class used by Preprocessor.  Handles details of reading from files.
 * @author kpfalzer
 */
public class FileReader extends Reader {

    public FileReader(String fname) {
        init(fname);
    }

    private void init(String fname) {
        try {
            m_rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(fname)));
        } catch (Exception ex) {
            message('F', "FILE-2", fname, "read");
        }
        m_loc = new Location(fname);
        m_state = EState.eGetLine;
    }

    /**
     * Get fresh new line.  It is expected that receiver will process
     * line and then update here.
     * @return new line (with NL appended) or null if end of stream.
     */
    public String getNextLine() {
        assert (EState.eGetLine == m_state);
        try {
            m_currLine = m_rdr.readLine();
            m_loc.setLineNum(m_rdr.getLineNumber());
            if (null != m_currLine) {
                m_currLine += stNl;
            }
        } catch (Exception ex) {
            abnormalExit(ex);
        }
        m_currPos = 0;
        m_state = (null != m_currLine) ? EState.eExpectLineUpdate : EState.eEOF;
        return m_currLine;
    }

    /**
     * Get next char.
     * @return next character, or -1 on EOF.
     */
    public int getNextChar() {
        int r = 0;
        if (!isEOF()) {
            final int n = m_currLine.length();
            assert (EState.eGetChar == m_state);
            if (m_currPos < n) {
                r = (int) m_currLine.charAt(m_currPos++);
            }
            if (m_currPos >= n) {
                m_state = EState.eGetLine;
            }
        } else {
            r = -1;
        }
        return r;
    }
    private LineNumberReader m_rdr;
}

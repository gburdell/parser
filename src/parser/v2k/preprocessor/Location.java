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
import  parser.ALocation;

/**
 *
 * @author kpfalzer
 */
public class Location extends ALocation {
    public Location(String fname, int lnum) {
        init(fname, lnum);
    }
    public final void init(String fname, int lnum) {
        m_fileName = fname;
        m_lnum = lnum;
    }
    public Location(String fname) {
        this(fname,1);
    }
    public Location(Location loc) {
        this(loc.m_fileName, loc.m_lnum);
    }
    public Location dup() {
        return new Location(this);
    }
    
    public String getFilename() {
        return m_fileName;
    }
    public int getLineno() {
        return m_lnum;
    }
    public int incrLineNum() {
        return ++m_lnum;
    }
    public int setLineNum(int lnum) {
        m_lnum = lnum;
        return m_lnum;
    }
    private String  m_fileName = null;
    private int     m_lnum = -1;
}

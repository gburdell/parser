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
package parser;

import static java.nio.file.Files.isSameFile;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import antlr.Token;
import gblib.Util;
import java.io.IOException;

/**
 * A base class to encapsulate essence of location (filename + line number). The
 * flexibility here is for parsers w/ preprocessors, such as Verilog. For those,
 * the actual file+line may not be reflected by the token itself.
 *
 * For non-preprocessed parsers (VHDL and .lib), only the (Token) constructor
 * needs to be implemented.
 *
 * @author karl
 */
public abstract class Location extends ALocation {

    abstract protected ILexer getLexer();

    protected Location(Token location) {
        init(location);
    }
    /*Create at current lexer location.*/

    protected Location(int offset) {
        init(offset);
    }
    /*Create using specified location.*/

    protected Location(String fn, int lnum) {
        m_fileName = fn;
        m_lineno = lnum;
    }
    /*Create location by adding offset to specified location.*/

    protected Location(Location loc, int offset) {
        m_fileName = loc.getFilename();
        m_lineno = loc.getLineno() + offset;
    }

    private void init(Token location) {
        m_fileName = getLexer().getFilename();
        m_lineno = location.getLine();
    }

    private void init(int offset) {
        int lnum = getLexer().getLine() + offset;
        if (0 > lnum) {
            lnum = 0;
        }
        setLoc(getLexer().getFilename(), lnum);
    }

    /**
     * Get filename.
     * @return filename.
     */
    public String getFilename() {
        return m_fileName;
    }

    /**
     * Get line number.
     * @return line number.
     */
    public int getLineno() {
        return m_lineno;
    }

    /**
     * Check if objects point to same location.
     * @param obj other Location.
     * @return true if points to same location as this.
     */
    public boolean equals(Location obj) {
        try {
            final Location asLoc = Util.downCast(obj);
            final Path thisPath = FileSystems.getDefault().getPath(getFilename()),
                    objPath = FileSystems.getDefault().getPath(asLoc.getFilename());
            final boolean isSameFile = isSameFile(thisPath, objPath);
            return isSameFile && (getLineno() == obj.getLineno());
        } catch (IOException ex) {
            return false;
        }
    }

    protected void setLoc(String fn, int lnum) {
        m_fileName = fn;
        m_lineno = lnum;
    }
    /**
     * Filename.
     */
    private String m_fileName;
    /**
     * Line number.
     */
    private int m_lineno;
}

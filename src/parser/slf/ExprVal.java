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
package parser.slf;
import  static gblib.Util.invariant;
/**
 *
 * @author karl
 */
public class ExprVal {

    public ExprVal(antlr.Token id) {
        this.m_ele = id.getText();
        this.m_isNum = false;
    }
    public ExprVal(Number n) {
        this.m_ele = n;
        this.m_isNum = true;
    }
    public boolean isIdent() {
        return !m_isNum;
    }
    public String asIdent() {
        invariant(isIdent());
        return (String)m_ele;
    }
    public Number asNumber() {
        invariant(!isIdent());
        return (Number)m_ele;
    }

    private final Object    m_ele;
    private final boolean   m_isNum;
}

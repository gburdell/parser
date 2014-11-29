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
public class Number {

    public Number(String ele) {
        m_isInt = !(ele.contains(".") || ele.contains("e") || ele.contains("E"));
        if (m_isInt) {
            m_ele = Integer.parseInt(ele);
        } else {
            m_ele = Double.parseDouble(ele);
        }
    }
    public boolean isInt() {
        return m_isInt;
    }
    public Integer asInt() {
        invariant(isInt());
        return (Integer)m_ele;
    }
    public Double asDouble() {
        invariant(false == isInt());
        return (Double)m_ele;
    }
    private boolean m_isInt;
    private Object  m_ele;
}

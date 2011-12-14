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
import  java.util.List;
/**
 *
 * @author kpfalzer
 */
public class ValueType {
    public static enum EType {eExpr, eIdent, eString, eNumber, eBool};

    public ValueType(List<Object> expr) {
        m_type = EType.eExpr;
        m_data = expr;
    }
    public ValueType(String id, Bus bus) {
        m_type = EType.eIdent;
        m_data = new Object[]{id, bus};
    }
    public ValueType(String str) {
        m_type = EType.eString;
        m_data = str;
    }
    public ValueType(Number num, String unit) {
        m_type = EType.eNumber;
        m_data = new Object[]{num, unit};
    }
    public ValueType(Boolean bool) {
        m_type = EType.eBool;
        m_data = bool;
    }
    public List<Object> asExpr() {
        return (List<Object>)m_data;
    }
    public String asIdent() {
        Object[] asObj = (Object[])m_data;
        String r = (String)asObj[0];
        if (null != asObj[1]) {
            r += (Bus)asObj[1];
        }
        return r;
    }
    public String asString() {
        return (String)m_data;
    }
    public Object[] asNumber() {
        return (Object[])m_data;
    }
    public boolean asBoolean() {
        return (Boolean)m_data;
    }
    public int asInt() {
        return ((Number)(asNumber()[0])).asInt();
    }
    public final EType  m_type;
    private Object      m_data;
}

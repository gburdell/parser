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
import  parser.Location;
import  java.util.*;

public class MacroVal 
{
    public MacroVal(List<String> parms, String val) {
        init(parms, val);
    }
    private void init(List<String> parms, String val) {
        m_nParms = parms.size();
        //TODO: set m_val w/ </%1%/> ...
        /*
         * Get parameter names in descending order of length
         */
        List<String> sorted = new LinkedList<String>(parms);
        Collections.sort(sorted, new Comparator<String>() {

            public int compare(String t, String t1) {
                //return by descending order
                int n = t.length(), n1 = t1.length();
                return (n == n1) ? 0 : ((n < n1) ? 1 : -1);
            }
        });
        //TODO...  Make sure in DESCENDING ORDER
        for (String p : sorted) {
            if (0 <= val.indexOf(p)) {
                int ix = parms.indexOf(p);  //which parm
                String repl = stDelim[0] + ix + stDelim[1];
                val = val.replace(p, repl);
            } else {
                //TODO: parm not used error?
            }
        }
        m_val = val;
        setWhere();
    }
    public MacroVal(String val) {
        m_val = val;
        setWhere();
    }
    public MacroVal(String val, VlogLocation loc) {
        m_val = val;
        if (null != loc) {
			m_decl = loc;
		} else {
			setWhere();
		}
    }
    private void setWhere() {
       //backup one line to acct for matched newline
       m_decl = new VlogLocation(Preproc.getTheOne().getLexerLoc(), -1);
    }
    public String getVal() {
        return m_val;
    }
	public boolean hasParams() {
		return (0 < m_nParms);
	}
    public String getVal(List<String> parms) {
        assert(m_nParms == parms.size());
        String rval = m_val;
        int i = 0;
        for (String p : parms)
        {
            rval = replace(rval, p, i++);
        }
        return rval;
    }
   	/**Get definition location.*/
	public Location getWhereDefn() {
		return m_decl;
	}
    private static String replace(String val, String pname, int ix) {
        String repl = stDelim[0] + ix + stDelim[1];
        return val.replace(repl, pname);
    }
    private int     m_nParms = 0;
    private String  m_val;
    /**Location of macro definition.*/
	private Location	m_decl;
  
    // {[0],[1]}={prefix,suffix}
    private static final String stDelim[] =
            new String[]{"</%","%/>"};
}

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
import  java.util.*;

/**
 * `define macro definitions.
 * NOTES from LRM:
 * It shall be an
 * error if any of the remaining formal arguments does not have a default specified. 
 * For a macro with arguments, the parentheses are always required in the macro call, 
 * even if all the arguments have defaults. It shall be an error to specify more 
 * actual arguments than the number of formal arguments.
 * @author kpfalzer
 */
public class MacroDefns {
    public MacroDefns() {}
    
    public boolean isDefined(String nm) {
        return (m_valsByName.containsKey(nm));
    }
    
    public boolean hasParams(String key) {
        return isDefined(key) && m_valsByName.get(key).hasParms();
    }
    
    public void add(String nm, String defn) {
        add(nm, null, defn);
    }
    
    public static class ExpansionException extends Exception {
        private ExpansionException(String reason) {
            super(reason);
        }
    }
    
    /**
     * Expand macro with actual arguments.
     * @param macnm name of macro.
     * @param args list of actual arguments.
     * @return expanded macro.
     * @throws parser.v2k.preprocessor.MacroDefns.ExpansionException 
     */
    public String expandMacro(String macnm, List<String> args) 
            throws ExpansionException {
        assert(hasParams(macnm));
        List<Parm> parms = m_valsByName.get(macnm).m_parms;
        int hasN = args.size();
        int expectsN = parms.size();            
        if (hasN > expectsN) {
            StringBuilder msg = new StringBuilder("macro '");
            msg.append(macnm).append("' has ").append(expectsN)
               .append(" arguments, usage has ").append(hasN);
            throw new ExpansionException(msg.toString());
        }
        String defn = m_valsByName.get(macnm).m_defn;
        int pos = 1;
        String with;
        for (String arg : args) {
            if (arg.isEmpty()) {
                with = parms.get(pos-1).getDefault();
                if (null == with) {
                    with = Helper.stNul;    //""
                }
            } else {
                with = arg;
            }
            defn = replace(defn, pos, with);
            pos++;
        }
        //from LRM: if more formal than actual, the remaining formal must have 
        //defaults.
        for (; pos <= hasN; pos++) {
            with = parms.get(pos-1).getDefault();
            if (null == with) {
                StringBuilder msg = new StringBuilder("macro '");
                msg.append(macnm).append("' parameter named '")
                   .append(parms.get(pos-1).getParmName())
                   .append("' has no default value and was not assigned actual value");
                throw new ExpansionException(msg.toString());                
            }
            defn = replace(defn, pos, with);
        }
        return defn;
    }
    
    private static String replace(String s, int pos, String with) {
        String repl = stDelim[0] + pos + stDelim[1];
        s = s.replace(repl, with);
        return s;
    }
    
    /**
     * Get non-parameterized macro value.
     * @param nm macro name.
     * @return non-parameterized macro value.
     */
    public String getVal(String nm) {
        Val val = m_valsByName.get(nm);
        assert (null == val.m_parms);
        return val.m_defn;
    }
    
    public void add(String nm, List<Parm> parms, String defn) {
        //TODO: cleanup dups
        assert(! isDefined(nm));
        if (null == parms) {
            m_valsByName.put(nm, new Val(defn));
        } else {
            /* replace each parameter value w/ </%n%/> where n is
             * parm position, 1-origin.
             */
            //Get parameter names in descending order of length
            List<String> names = new LinkedList<String>();
            for (Parm p : parms) {
                names.add(p.getParmName());
            }
            List<String> sorted = new LinkedList<String>(names);
            Collections.sort(sorted, new Comparator<String>() {
                public int compare(String t, String t1) {
                    //return by descending order
                    int n = t.length(), n1 = t1.length();
                    return (n == n1) ? 0 : ((n < n1) ? 1 : -1);
                }
            });
            for (String p : sorted) {
                int ix = names.indexOf(p) + 1;  //which parm
                String repl = stDelim[0] + ix + stDelim[1];
                defn = defn.replace(p, repl);
            }
            m_valsByName.put(nm, new Val(parms, defn));
        }
    }
        // {[0],[1]}={prefix,suffix}
    private static final String stDelim[] = new String[]{"</%","%/>"};

    private Map<String,Val> m_valsByName = new HashMap<String,Val>();
    
    public static class Val {
        public Val(String defn) {
            this(null, defn);
        }
        public Val(List<Parm> parms, String defn) {
            m_parms = parms;
            m_defn = defn;
        }
        public boolean hasParms() {
            return (null != m_parms);
        }
        public final List<Parm> m_parms;
        public final String     m_defn;
    }
}

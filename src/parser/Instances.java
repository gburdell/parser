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
import  java.util.*;
import  static parser.Utils.invariant;
/**
 * An object for iterating through the module/instance hierarchy.
 *
 * @author kpfalzer
 */
public class Instances {
    public static String stInstNameSeparator = "/";
    
    public Instances(String topModule, Object trackers[]) {
        m_topModule = topModule;
        for (Object ele : trackers) {
            if (null != ele) {
                Tracker trk = (Tracker)ele;
                m_modulesByName.putAll(trk.getModulesByName());
            }
        }
        begin();
    }

    public int getModuleCnt() {
        return m_modulesByName.size();
    }

    private void begin() {
        Module top = m_modulesByName.get(m_topModule);
        invariant(null != top);
        if (!top.isLeaf()) {
            m_stack.addLast(new P(new Insterator(top), null));
        }
    }

    public boolean hasMore() {
        boolean rval = false;
        if (!m_stack.isEmpty()) do {
            Insterator now = m_stack.peekLast().iter();
            if (now.hasNext()) {
                rval = true;
            } else {
                m_stack.removeLast();
            }
        } while (!rval && !m_stack.isEmpty());
        return rval;
    }

    public ModuleInstance next() {
        Insterator now = m_stack.getLast().iter();
        invariant(now.hasNext());
        ModuleInstance inst = now.next();
        //depth first
        String refName = inst.getRefName();
        Module ref = m_modulesByName.get(refName);
        m_stack.addLast(new P(new Insterator(ref), inst.getInstName()));
        return inst;
    }

    public String getFullName() {
        StringBuilder bld = new StringBuilder();
        for (P ele : m_stack) {
            String nm = ele.instName();
            if (null != nm) {
                bld.append(stInstNameSeparator).append(nm);
            }
        }
        return bld.toString();
    }

    private final String            m_topModule;
    private Map<String,Module>      m_modulesByName = new HashMap<String, Module>();
    static class P extends Pair<Insterator,String> {
        P(Insterator iter, String instNm) {
            super(iter, instNm);
        }
        Insterator iter() {
            return v1;
        }
        String instName() {
            return v2;
        }
    }
    private LinkedList<P>  m_stack = new LinkedList<P>();
    
    /**
     * Instance iterator.
     */
    private static class Insterator {
        public Insterator(Module module) {
            m_module = module;
            if ((null != m_module) && !m_module.isLeaf()) {
                m_iter = m_module.getInstances().iterator();
            }
        }
        
        public boolean hasNext() {
            return (null==m_module || null==m_iter) ? false : m_iter.hasNext();
        }
        
        public ModuleInstance next() {
            return m_iter.next();
        }
        
        private final Module              m_module;
        private Iterator<ModuleInstance>  m_iter = null;
    }
}

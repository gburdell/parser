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
import static parser.Message.message;
import  java.util.HashMap;
import  java.util.Map;
/**
 *
 * @author kpfalzer
 */
public abstract class Tracker {
    protected Tracker() {}

    protected void addModule(Module mod) {
        String key = mod.getName();
        if (m_moduleDefnByName.containsKey(key)) {
            Module exists = m_moduleDefnByName.get(key);
            //DEFN-1  %s:%d: module '%s' previously defined (at %s).
            message(mod.getDeclLoc(), 'E', "DEFN-1", key,
                    exists.getDeclLoc().toString());
        } else {
            m_moduleDefnByName.put(key, mod);
        }
        m_currentModule = mod;
    }

    protected void addInstance(ModuleInstance inst) {
        m_currentModule.addInstance(inst);
    }

    public void endModule() {
        m_currentModule = null;
    }

    public Map<String,Module> getModulesByName() {
        return m_moduleDefnByName;
    }
    
    protected Module  m_currentModule;
    private Map<String,Module>  m_moduleDefnByName =
        new HashMap<String,Module>();
}

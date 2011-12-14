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
import static parser.Utils.invariant;
import  java.util.Map;
import  java.util.HashMap;
/**
 *
 * @author karl
 */
public class Library implements KeyValue.Handler, Cell.Factory {

    public Library(String name) {
        this.m_name = name;
    }
    public Library() {}
    
    public void setName(String name) {
        invariant(null == m_name);
        m_name = name;
    }
    public String getName() {
        return m_name;
    }
    public Cell createCell(String name) {
        Cell cell = new Cell(name);
        addCell(cell);
        return cell;
    }
    public Cell createCell(antlr.Token tok) {
        Cell cell = createCell(tok.getText());
        {
            //add to tracker
            SlfParser.stTracker.addModule(tok);
        }
        return cell;
    }

    public Cell getCell(String name) {
        return m_cellsByName.get(name);
    }

    public boolean containsCell(String name) {
        return (null != getCell(name));
    }

    public void addCell(Cell cell) {
        m_cellsByName.put(cell.getName(), cell);
    }
    public String[] getCellNames() {
        return m_cellsByName.keySet().toArray(new String[0]);
    }
    protected String              m_name;
    protected Map<String,Cell>    m_cellsByName = new HashMap<String,Cell>();

    public boolean isKeyValid(antlr.Token key) {
        return false;
    }
    public void saveKeyValue(KeyValue data) {
        //do nothing
    }
    public void valueSetDone()
    {}

}

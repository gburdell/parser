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
import  static parser.Utils.invariant;
import  java.io.*;
import  java.util.*;

/**
 *
 * @author karl
 */
public class LibaryWithPins extends Library implements LibraryFactory {
    public LibaryWithPins(String fname) {
        m_fileName = fname;
        init();
    }

    private final String    m_fileName;

    public Library createLibrary(String name) {
        this.setName(name);
        return this;
    }

    private Map<String,Type>    m_typeByName;
    private static enum EState {eInvalid,eWaitType,eType};
    private EState              m_state = EState.eWaitType;
    private Type                m_currType;

    @Override
    public boolean isKeyValid(antlr.Token key) {
        boolean rval = false;
        switch (m_state) {
            case eWaitType:
                if (key.getText().equals("type")) {
                    rval = true;
                }
                break;
            case eType:
                return m_currType.isKeyValid(key);
            default:
                invariant(false);
        }
        return rval;
    }
    public void saveKeyValue(KeyValue data) {
        switch (m_state) {
            case eWaitType:
                String typeNm = data.getValueList().get(0).asIdent();
                m_currType = new Type(typeNm);
                m_state = EState.eType;
                break;
            case eType:
                m_currType.saveKeyValue(data);
                break;
            default:
                invariant(false);
        }
    }
    public void valueSetDone() {
        switch (m_state) {
            case eType:
                if (null == m_typeByName) {
                    m_typeByName = new HashMap<String,Type>();
                }
                m_typeByName.put(m_currType.m_name, m_currType);
                m_currType = null;
                m_state = EState.eWaitType;
        }
    }

    private void init() {
        try {
            FileReader rdr = new FileReader(m_fileName);
            SlfLexer lexer = new SlfLexer(rdr);
            lexer.setFilename(m_fileName);
            SlfParser parser = new SlfParser(lexer);
            parser.source_text(this);
            rdr.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    static class Type {
        Type(String name) {
            m_name = name;
        }
        boolean isKeyValid(antlr.Token key) {
            return true;
        }
        void saveKeyValue(KeyValue data) {
            String key = data.getKey();
            if (key.equals("base_type")) {
                m_baseType = data.getValue().asIdent();
            } else if (key.equals("data_type")) {
                m_dataType = data.getValue().asIdent();
            } else if (key.equals("bit_width")) {
                m_width = data.getValue().asInt();
            } else if (key.equals("bit_from")) {
                m_from = data.getValue().asInt();
            } else if (key.equals("bit_to")) {
                m_to = data.getValue().asInt();
            }
        }
        final String  m_name;
        String  m_baseType, m_dataType;
        int m_width = -1, m_from = -1, m_to = -1;
    }
    public static void main(String[] args) {
        for (String fn : args) {
            System.out.printf("Info: \"%s\": processing ...\n",fn);
            new LibaryWithPins(fn);
        }
    }

}

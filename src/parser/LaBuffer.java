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

/**
 * A buffer with lookahead.
 * 
 * @author kpfalzer
 */
public class LaBuffer<T> {
    public static interface Producer<T> {
        /**
         * Get next available.
         * @param <T>
         * @return next available.
         */
        public T getNext();
    }
    
    public LaBuffer(Producer<T> rdr, T[] buf) {
        this(rdr, buf, null);
    }
    public LaBuffer(Producer<T> rdr, T[] buf, T v) {
        m_rdr = rdr;
        m_n = buf.length;
        m_la = buf;
        init(v);
    }
    private void init(T v) {
        for (int i = 0; i < m_n; ) {
            m_la[i++] = v;
        }        
    }
    public T accept() {
        T ele = la(0);
        accept(1);
        return ele;
    }
    public void accept(int m) {
        assert(m <= m_used);
        m_used -= m;
        m_head = (m_head + m) % m_n;
        fill();
    }
    public T la(int i) {
        assert(i < m_n);
        fill();
        int j = (m_head + i) % m_n;
        return m_la[j];
    }
    /**
     * Always keep it full.
     */
    private void fill() {
        for (; m_used < m_n; m_tail = (m_tail + 1) % m_n, m_used++) {
            m_la[m_tail] = m_rdr.getNext();
        }
    }
    private Producer<T> m_rdr;
    private final int   m_n;
    private T           m_la[];
    /**
     * Pointer to top to pop.
     */
    private int     m_head = 0;
    /**
     * Pointer to next slot to fill.
     */
    private int     m_tail = 0;
    /**
     * Number of used slots.
     */
    private int     m_used = 0;
}


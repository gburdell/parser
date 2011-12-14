/*
 *  The MIT License
 * 
 *  Copyright 2011 George P. Burdell
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package parser.v2k.preprocessor.antlrx;
import  antlr.CharQueue;

/**
 * Extend antlr.CharQueue to support prepend operation needed for 
 * macros expansion.
 * @author gburdell
 */
public class CharQueueX extends CharQueue {
    public CharQueueX(int sz) {
        super(sz);
    }
    
    public static final char stNul = (char)-1;
    
    public void prepend(String s, final int offset) {
        //Grab current contents
        char currBuff[] =  new char[buffer.length];
        int i = 0;
        while ((i < currBuff.length) && (0 <= getNbrEntries())) {
            currBuff[i++] = super.elementAt(offset);
            super.removeFirst();            
        }
        int valid = i;
        while (i < currBuff.length) {
            currBuff[i++] = stNul;
        }
        int n = s.length() + super.buffer.length;
        int used = n;
        //NOTE: Since CharQueue uses sizeLessOne as bit-mask;
        //very important that n be power of 2.
        int nn = 1;
        while (nn < n) {
            nn <<= 1;
        }
        super.init(nn);
        for (i = 0; i < s.length(); i++) {
            super.append(s.charAt(i));
        }
        for (i = 0; i < currBuff.length; i++) {
            super.append(currBuff[i]);
        }
        for (; used < nn; used++) {
            super.append(stNul);
        }
        nbrEntries = valid + s.length() - 1; //rest are nul
    }
    
    public int getNbrEntries() {
        return super.nbrEntries;
    }
}

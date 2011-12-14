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
import antlr.CharStreamException;
import  antlr.InputBuffer;
import  static parser.Utils.downCast;

/**
 *
 * @author gburdell
 */
public abstract class InputBufferX extends InputBuffer {
    public InputBufferX() {
        super.queue = new CharQueueX(16);
    }
    public void prepend(String s) throws CharStreamException {
        /*
         * TODO: Empirically, need to do this LA(1) to get the InputBuffer
         * realigned before we prepend, in case of the parameterized
         * macro.  This seems too mysterious to be a good thing.
         */
        LA(1);
        getQ().prepend(s, 0);
        nMarkers = 0;
        markerOffset = 0;
        numToConsume = 0;
    }   
    public CharQueueX getQ() {
        return downCast(queue);
    }
}

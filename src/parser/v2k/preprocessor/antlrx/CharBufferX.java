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
import  antlr.CharStreamException;
import  antlr.CharStreamIOException;
import  java.io.Reader;
import  java.io.IOException;

/**
 *
 * @author gburdell
 */
public class CharBufferX extends InputBufferX {
    //Note: body copied verbatim from antlr.CharBuffer
    // char source
    public transient Reader input;

    /** Create a character buffer */
    public CharBufferX(Reader input_) { // SAS: for proper text i/o
        super();
        input = input_;
    }

    /** Ensure that the character buffer is sufficiently full */
    public void fill(int amount) throws CharStreamException {
        try {
            syncConsume();
            // Fill the buffer sufficiently to hold needed characters
            while (super.getQ().getNbrEntries() < amount + markerOffset) {
                // Append the next character
                queue.append((char)input.read());
            }
        }
        catch (IOException io) {
            throw new CharStreamIOException(io);
        }
    }
}

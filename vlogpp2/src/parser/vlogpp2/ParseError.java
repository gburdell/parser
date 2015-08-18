/*
 * The MIT License
 *
 * Copyright 2015 gburdell.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package parser.vlogpp2;

import gblib.FileLocation;
import gblib.FileCharReader;
import parser.vlogpp2.Messages.Message;

/**
 *
 * @author gburdell
 */
public class ParseError extends Exception {

    public ParseError(final String code) {
        this(code, (String) null);
    }

    public ParseError(final String code, final FileCharReader loc, final Object... args) {
        this(code, loc.getLocation(), args);
    }

    public ParseError(final String code, final SourceFile loc, final Object... args) {
        this(code, loc.getLocation(), args);
    }

    public ParseError(final String code, final FileLocation loc, final Object... args) {
        this(code, loc.toString(), args);
    }

    public ParseError(final String code, final String loc, final int[] loc2) {
        this(code, loc, loc2[0], loc2[1]);
    }

    public ParseError(final String code, final String loc, final Object... args) {
        Object nargs[] = null;
        if (null != loc) {
            nargs = new Object[1 + args.length];
            nargs[0] = loc;
            System.arraycopy(args, 0, nargs, 1, args.length);
        } else {
            assert null == args;
        }
        m_msg = Messages.getMessage('E', code, nargs);
    }

    @Override
    public String getMessage() {
        return m_msg.getMessage();
    }

    public void print() {
        Messages.print(m_msg);
    }

    private final Message m_msg;
    }

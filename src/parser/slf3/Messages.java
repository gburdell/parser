/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *
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
package parser.slf3;

import gblib.MessageMgr;
import static gblib.MessageMgr.addMessage;

/**
 *
 * @author gburdell
 */
public class Messages {
    public static void message(final String code, Object... args) {
        MessageMgr.message(code, args);
    }
    
    private static boolean init() {
        addMessage('E', "SLF-1", "%s: nested block comments not allowed");
        addMessage('E', "SLF-2", "%s: premature <EOF> detected");
        addMessage('E', "SLF-3", "%s: syntax error at '%s'");
        addMessage('E', "SLF-4", "%s: syntax error at '%s', expecting '%s'");
        addMessage('I', "SLF-5", "%s: %d cell(s) found");
        addMessage('E', "SLF-6", "%s: syntax error at '%s', %s");
        return true;
    }
    
    private static final boolean stInit = init();
}

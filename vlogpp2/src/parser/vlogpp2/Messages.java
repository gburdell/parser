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

import gblib.MessageMgr;
import static gblib.MessageMgr.addMessage;

/**
 *
 * @author gburdell
 */
public class Messages {

    public static void message(char severity, String code, Object... args) {
        MessageMgr.message(severity, code, args);
    }

    public static Message getMessage(char severity, String code, Object... args) {
        return new Message(severity, code, args);
    }

    public static void print(final Message msg) {
        MessageMgr.print(msg);
    }

    public static class Message extends MessageMgr.Message {

        public Message(char severity, String code, Object... args) {
            super(severity, code, args);
        }

    }

    private static boolean init() {
        addMessage('E', "VPP-ARGS-1", "%s: macro '%s' definition specified arguments. '()' required");
        addMessage('E', "VPP-CMNT-2", "%s: nested block comment");
        addMessage('E', "VPP-COND-1", "%s: malformed '<tic-conditional>'");
        addMessage('E', "VPP-COND-2", "%s: unexpected '%s' after '%s'");
        addMessage('E', "VPP-COND-3", "%s: depth of nested conditional directives > %d");
        addMessage('E', "VPP-COND-4", "%s: unexpected '%s' conditional directive.\n       %s: start of `if(n)def");
        addMessage('E', "VPP-COND-5", "%s: '`endif' without opening '`ifdef' or '`ifndef'");
        addMessage('E', "VPP-EOF-1", "%s: unexpected end-of-file while processing '%s'");
        addMessage('E', "VPP-EOF-2", "%s: unexpected end-of-file while processing '%s' (started at %d:%d (line:col))");
        addMessage('E', "VPP-EXIT", "Exiting due to previous error(s)");
        addMessage('E', "VPP-FARG-1", "%s: empty formal arg. while processing '`define' (started at %d:%d (line:col))");
        addMessage('E', "VPP-NETTYPE-1", "%s: '%s' invalid value for `default_nettype");
        addMessage('E', "VPP-NODEFN", "%s: no definition for '`%s'");
        addMessage('E', "VPP-REDEFN-1", "%s: macro '%s' redefined.\n       %s: previously defined");
        addMessage('E', "VPP-REDEFN-2", "%s: macro '%s' redefined with different value.\n       %s: previously defined");
        addMessage('E', "VPP-REDEFN-3", "%s: macro '%s' redefined at different location.\n       %s: previously defined");
        addMessage('E', "VPP-STRING", "%s: unterminated string (started at %d:%d (line:col))");
        addMessage('E', "VPP-SYNTAX-1", "%s: unexpected '%s'");
        addMessage('E', "VPP-SYNTAX-2", "%s: '%s' syntax error at '%s'");
        addMessage('E', "VPP-SYNTAX-3", "%s: syntax error after '%s'");
        addMessage('E', "VPP-UNDEF-1", "%s: '`undef %s' has no effect since macro '%s' was never defined");
        return true;
    }

    private static final boolean stInit = init();
}

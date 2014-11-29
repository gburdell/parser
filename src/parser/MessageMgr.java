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
 * Scaffold over gblib.MessageMgr. Make sure we align with jruby side too.
 *
 * @author gburdell
 */
public class MessageMgr {
    public static int setMessageLevel(int lvl) {
        return gblib.MessageMgr.setMessageLevel(lvl);
    }
    
    public static void message(char severity, String code, Object... args) {
        gblib.MessageMgr.message(severity, code, args);
    }

    public static void message(String severity, String code, Object... args) {
        gblib.MessageMgr.message(severity.charAt(0), code, args);
    }

    public static void addMessages(String fname) {
        gblib.MessageMgr.addMessages(fname);
    }
    
    public static int getErrorCnt() {
        return gblib.MessageMgr.getErrorCnt();
    }

    /**
     * Method to allow other factions to load messages.
     * @return true if messages loaded here.
     */
    public static boolean loadMessages() {
        if (stLoadMessages) {return false;}
        String fn = System.getProperty("parser.messages");
        if (null != fn) {
            gblib.MessageMgr.addMessages(fn);
        }
        return true;
    }

    private static final boolean stLoadMessages = loadMessages();
}

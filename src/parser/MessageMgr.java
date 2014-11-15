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
import  java.util.*;
import  java.io.*;

/**
 *
 * @author karl
 */
public class MessageMgr {
    
    public static void initialize() {
        if (null == getTheOne()) {
            stTheOne = new MessageMgr();
        }
    }
    /**Encapsulate message.*/
    public static class Message {
    	/**Create message.
    	 * 
    	 * @param severity	one of 'I', 'W', 'E' (for Info, Warning, Error, respectively).
    	 * @param code		a valid message code used to find message format.
    	 * @param args		arguments to pass to format.
    	 */
    	public Message(char severity, String code, Object ... args) {
    		initialize();
            m_type = getTheOne().getMessenger().factory(severity);
            m_message = format(m_type, code, args);
    	}
    	public void print() {
    		MessageMgr.print(this);
    	}
    	/**Get the message type.*/
    	IMessenger.EType getType() {
    		return m_type;
    	}
    	/**Get formatted message.*/
    	String getMessage() {
    		return m_message;
    	}
    	/**Message type*/
    	private final IMessenger.EType	m_type;
    	/**Formatted message.*/
    	private final String	m_message;
    }
    /**Format message.*/
    private static String format(IMessenger.EType type, String code, Object ... args) {
        String fmt = getTheOne().getFormat(code);
    	Utils.invariant(null != fmt);
        StringBuilder buf = new StringBuilder(type.getPfx());
        buf.append(": ");
        buf.append(String.format(fmt, args));
        buf.append(String.format("  (%s)", code));
		// fix filenames a\b to a/b
		String s = buf.toString().replace('\\', '/');
        return s;
    }
    /**Conditionally display message.*/
    public static void message(boolean doMsg, char severity, String code, Object ... args) {
        if (doMsg) {
        	Message msg = new Message(severity, code, args);
        	print(msg);
        }
    }

    public static void message(char severity, String code, Object ... args) {
        Message msg = new Message(severity, code, args);
        print(msg);
    }
    public static void message(String severity, String code, Object ... args) {
        message(severity.charAt(0), code, args);
    }
    public static void print(Message msg) {
        getTheOne().getMessenger().message(msg);
        getTheOne().m_msgCnts[msg.getType().getIx()]++;
    }
    
    public static int getErrorCnt() {
        MessageMgr t = getTheOne();
		return (null == t) ? 0 : t.m_msgCnts[2];
    }
    
    private static MessageMgr getTheOne() {
        return stTheOne;
    }
    
    /**
     * Get reference to counters to be shared.
     * @return counter references.
     */
    public static int[] getMessageCounters() {
        return getTheOne().m_msgCnts;
    }
    
    /** Creates a new instance of MessageMgr */
    private MessageMgr() {
        init();
    }
    
    /**
     * Add new messages.
     * @param fname new messages.
     */
    public static void addMessages(String fname) {
        initialize();
        MessageMgr mgr = getTheOne();
        mgr.init(new File(fname));
    }

    private void init() {
        String root = Utils.getToolRoot();
        File f = new File(new File(root, stSubdir), stFname);
        init(f);
    }

    private void init(File f) {
        try {
            f = new File(f.getCanonicalPath());
            LineNumberReader rdr = new LineNumberReader(new FileReader(f));
            String line;
            int mark;
            String msgCode;
            String msg;
            StringTokenizer toks;
            while (null != (line = rdr.readLine())) {
                if (0 <= (mark = line.indexOf("//"))) {
                    line = line.substring(0, mark);
                }
                if (1 > line.length()) {
                    continue;
                }
                line = line.trim();
                mark = line.indexOf(' ');
                msgCode = line.substring(0, mark);
                msg = line.substring(mark).trim();
                Utils.invariant(null == m_msgs.put(msgCode, msg));
            }
        }
        catch (IOException ex) {
            Utils.abnormalExit(ex);
        }
    }
    
    public static abstract class IMessenger {
        public static enum EType {
            eInfo (System.out, "Info ", 0), 
            eWarn (System.out, "Warn ", 1), 
            eError(System.out, "Error", 2);
            /**Create An Enum based on code.*/
            public static EType factory(char code) {
            	return EType.values()[m_charMapToEnum.indexOf(code)];
            }
            public PrintStream getOstrm() {
            	return m_os;
            }
            public String getPfx() {
            	return m_pfx;
            }
            public int getIx() {
            	return m_ix;
            }
            
            EType(PrintStream os, String pfx, int ix) {
            	m_os = os;
            	m_pfx = pfx;
            	m_ix = ix;
            }
            /**PrintStream for type/severity of message.
             * @see java.io.PrintStream
             */
            private final PrintStream	m_os;
            /**Message prefix based on severity of message.*/
            private final String 		m_pfx;
            /**Index of code.*/
            private final int 			m_ix;
            /**The following in order as EType ordinals.
             * @see java.lang.Enum
             */
            private final static String m_charMapToEnum = "IWE";
        }
        public EType factory(char code) {
        	return EType.factory(code);
        }
        public abstract void message(Message msg);
    }
    
    public static class DefaultMessenger extends IMessenger {
        @Override
        public void message(Message msg) {
            PrintStream os = msg.getType().getOstrm();
            os.println(msg.getMessage());
            os.flush();
        }
     }

    public IMessenger getMessenger() {
        return m_messenger;
    }
    public String getFormat(String code) {
    	return m_msgs.get(code);
    }
    private static final String stSubdir = "props";
    private static final String stFname  = "parser.msg.txt";
    private static MessageMgr stTheOne = null;

    private final Map<String, String> m_msgs = new HashMap<>();
    private final IMessenger m_messenger = new DefaultMessenger();
    private final int m_msgCnts[] = new int[] {0, 0, 0};
}

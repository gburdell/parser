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


package parser.sv;

import  java.util.*;
import  parser.v2k.Parser;
import  parser.ProcArgs;
import  parser.ProcArgs.Spec.EType;
import  parser.Pair;
import  parser.Message;
import  static parser.Utils.asEmpty;
import  static parser.Utils.nl;
import  static parser.Utils.fatal;
import  static parser.Utils.getPropertyAsInt;

/**
 *
 * @author karl
 */
public class Main
{
    private void init(String argv[]) {
        try {
            m_args = new Args(argv);
            //SysVlogParser.stDebug = true;
            //SysVlogParser.stQuick = false;
            parse();
        }
        catch (ProcArgs.ArgException ex) {
            fatal(ex);
        }
    }

    public Main(String argv[]) {
        init(argv);
    }
    public Main() {
        //nothing
    }

    void initAndParse(String argv[]) {
        init(argv);
    }

    public static void main(String argv[])  {
        Main notUsed = new Main(argv);
        int code = Message.hasParseError() ? 1 : 0;
        System.exit(code);
    }
    private static void usageErr(String msg) {
        System.err.println("Error: " + msg);
        usage();
        System.exit(1);
    }

    private static void usage() {
        final String progName = "parser.sv.Main";
        final String usage =
              "Usage: " + progName + " [options] infile..." + nl()
            + nl()
            + "Options:" + nl()
            + nl()
            + "  -I <dir>    Add <dir> (must be readable) to `include file search path." + nl()
            + "" + nl()
            + "  -D name     Predefine name as a macro with value 1." + nl()
            + "" + nl()
            + "  -D name=defn    Predefine name as a macro with value defn." + nl()
			+ "" + nl()
			+ "  -f cmdfile  \"cmdfile\" contains -I, -D and/or infile options." + nl()
            ;
        System.err.println(usage);
    }

    private SvParser  m_parser = null;

    private void parse() {
        setAntlrLineFormatter();    //we do in Message.
        m_srcFiles = m_args.getOptVals(".");
        List<String> inclDirs = m_args.getOptVals("-I");
        List<Pair<String,String> > defs = Pair.factory(m_args.getOptVals("-D"));
        m_parser = new SvParser();
        m_parser.parse(m_srcFiles, inclDirs, defs);
    }

    public void parse(String srcs[]) {
        m_parser.parse(srcs);
    }

    public Parser getParser() {
        return m_parser;
    }

/**/
    private static void setAntlrLineFormatter() {
        antlr.FileLineFormatter.setFormatter(
            new antlr.FileLineFormatter() {
                public String getFormatString(String fileName,
                                              int line, int column) {
                    return "";
                }
            }
        );
    }
/**/

    private Args            m_args;
    private List<String>    m_srcFiles;

    private static class Args extends ProcArgs {
        public Args(final String argv[]) throws ArgException {
            this.add(new ProcArgs.Spec("-I*", EType.eReadableDir))
                .add(new ProcArgs.Spec("-D*", EType.eString))
                .add(new ProcArgs.Spec(".+", EType.eReadableFile))  //filenames
                ;
			if ((2==argv.length) && argv[0].equals("-f")) {
				m_args = parse(argv[1]);
			} else {
				m_args = parse(argv);
			}
        }

        public boolean hasOpt(String opt) {
            return (null != m_args) ? m_args.containsKey(opt) : false;
        }

        public String getOptVal(String opt) {
            String rval = null;
            List<String> vals = getOptVals(opt);
            if (null != vals) {
                assert(1 == vals.size());
                rval = vals.get(0);
            }
            return rval;
        }

        public List<String> getOptVals(String opt) {
            return asEmpty(m_args.get(opt), new LinkedList<String>());
        }

        private Map<String, List<String>> m_args;
    }

    /**
     * Configure properties used to control parser, debug, ...
     */
    private static class Config extends Properties {
        Config() {
            super(m_defaults);
        }
        private final static Properties m_defaults = new Properties();
        static {
            m_defaults.setProperty("dumpSymbols", "0");
            m_defaults.setProperty("symbolsFname", "symbols.txt");
            m_defaults.setProperty("enableSSI", "0");
        }
    }

    private static Config   m_config = new Config();

    public static int getIntProperty(String nm) {
        int rval = getPropertyAsInt(nm);
        if (Integer.MIN_VALUE == rval) {
            rval = 0;
            String str = m_config.getProperty(nm);
            if (null != str) {
                rval = Integer.parseInt(str);
            }
        }
        return rval;
    }
    public static String getStringProperty(String nm) {
        String v = System.getProperty(nm);
        if (null == v) {
            v = m_config.getProperty(nm);
        }
        return v;
    }
}

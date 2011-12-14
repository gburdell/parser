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

package parser.vhdl;
import  parser.Message;
import  parser.ProcArgs;
import  static parser.Utils.fatal;
import  static parser.Utils.nl;
import  static parser.Utils.asEmpty;
import  java.util.List;
import  java.util.LinkedList;
import  java.util.Map;

/**
 *
 * @author kpfalzer
 */
public class Main {
    private void init(String argv[]) {
        try {
            m_args = new Args(argv);
            //VhdlParser.stDebug = true;
            //VhdlParser.stQuick = false;
            parse();
        }
        catch (Exception ex) {
            fatal(ex);
        }
    }

    public Main(String argv[]) {
        init(argv);
    }
    protected Main() {
        //nothing
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
        final String progName = "parser.vhdl.Main";
        final String usage =
              "Usage: " + progName + " [options] infile..." + nl()
            + nl()
            + "Options:" + nl()
            + nl()
			+ "  -f cmdfile  \"cmdfile\" contains infile ..." + nl()
            ;
        System.err.println(usage);
    }

    private Parser  m_parser = null;

    private void parse() {
        //TODO? setAntlrLineFormatter();    //we do in Message.
        m_srcFiles = m_args.getOptVals(".");
        if (1 > m_srcFiles.size()) {
            usageErr("Must specify at least one \"infile\".");
        }
        m_parser = new Parser();
        m_parser.parse(m_srcFiles.toArray(new String[0]));
    }

/*TODO?
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
*/

    private Args            m_args;
    private List<String>    m_srcFiles;

    private static class Args extends ProcArgs {
        public Args(final String argv[]) throws ArgException {
            this.add(new ProcArgs.Spec(".+", ProcArgs.Spec.EType.eReadableFile))  //filenames
                ;
			if ((2==argv.length) && argv[0].equals("-f")) {
				m_args = parse(argv[1]);
			} else {
				m_args = parse(argv);
			}
        }

		//TODO: this is exactly same as sv.Main.  So perhaps puch into base?

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
}

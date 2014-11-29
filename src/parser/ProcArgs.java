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

import java.util.*;
import java.io.*;

public class ProcArgs {
	public ProcArgs() {
	}
	
	public ProcArgs add(Spec spec) {
		m_specs.put(spec.getName(), spec);
		return this;
	}
	
	public static class ArgException extends Exception {
		ArgException(String msg) {
			super(msg);
		}
	}
	/**
	 * Parse arguments specified in file.
	 * File can also contain line comments, which are lines which start
	 * with character # or //.
	 * @param fname name of file with one argument per line.
	 * @return map of arguments by option name.
	 * @throws parser.ProcArgs.ArgException
	 */
	public Map<String, List<String>> parse(String fname) throws ArgException {
		List<String> args = new LinkedList();
		try {
			String ln;
			LineNumberReader rdr = new LineNumberReader(new FileReader(fname));
			StringTokenizer toks;
			while (rdr.ready()) {
				ln = rdr.readLine();
				if (null == ln)
					break;
				//if (ln.matches("^\\s*(#|//)"))
				if (ln.startsWith("//") || ln.startsWith("#"))
					continue;
				toks = new StringTokenizer(ln);
				while (toks.hasMoreElements())
					args.add(toks.nextToken());
			}
		} catch (IOException ex) {
			gblib.Util.fatal(ex);
		}
		return parse(args.toArray(new String[0]));
	}
	public Map<String, List<String>> parse(final String argv[]) throws ArgException {
		String ai;
		m_errors = new StringBuffer();
		Spec speci;
		for (int i = 0; i < argv.length; i++) {
			ai = argv[i];
			if (false == ai.startsWith("-")) {
				speci = m_specs.get(".");	//for arg w/ no option specifier
			}
			else {
				speci = m_specs.get(ai);
			}
			if (null == speci) {
				m_errors.append(String.format("Error: \"%s\": invalid option.\n", ai));
				if (++i < argv.length) {
					if (argv[i].startsWith("-")) {
						i--;
					}
					//else skip over arg to invalid option
				}
				continue;	//for
			}
			if (Spec.EType.eNone == speci.getType()) {
				update(speci);
			}
			else if (speci.getName().equals(".")) {
				update(speci, ai);
			}
			else if (++i >= argv.length) {
				m_errors.append(String.format("Error: \"%s\": missing argument.\n", ai));
			}
			else {
				update(speci, argv[i]);
			}
		}
		for (Spec opt : m_specs.values()) {
			switch (opt.getRepeat()) {
				case eOne: case eOneOrMore:
					if ( !opt.getName().equals(".") && (false == m_opts.containsKey(opt.getName()))) {
						m_errors.append(String.format("Error: \"%s\": option must be specified.\n", 
								opt.getName()));
					}
					break;
				default:
			}
		}
        /**NOTE: seems a bit extreme to throw exception.
		if (0 < m_errors.length()) {
			throw new ArgException(m_errors.toString());
		}
        **/
		return m_opts;
	}
	
	private void update(Spec spec, String val) {
		List<String> vals = m_opts.get(spec.getName());
		if (null == vals) {
			vals = new LinkedList<String>();
			m_opts.put(spec.getName(), vals);
		}
		switch (spec.getRepeat()) {
			case eOne: case eZeroOrOne:
				if (0 < vals.size()) {
					m_errors.append(String.format("Error: \"%s\": option specified >1 time.\n", spec.getName()));
				}
				break;
		}
		vals.add(val);
		File file, dir;
		switch (spec.getType()) {
			case eNumber:
				try {
					Integer.parseInt(val);
				}
				catch (Exception ex) {
					m_errors.append(String.format("Error: \"%s\": argument of \"%s\" is not a number.\n", 
							val, spec.getName()));
				}
				break;
			case eReadableFile: case eReadableDir:
				file = new File(val);
				if (false == file.canRead()) {
					if (spec.getName().equals(".")) {
						m_errors.append(String.format("Error: \"%s\": is not readable.\n",
								val));
					}
					else {
						m_errors.append(String.format("Error: \"%s\": argument of \"%s\" is not readable.\n",
								val, spec.getName()));
					}
				}
				else {
					String badWhat = null;
					switch (spec.getType()) {
						case eReadableFile:
							if (false == file.isFile()) {
								badWhat = "file";
							}
							break;
						case eReadableDir:
							if (false == file.isDirectory()) {
								badWhat = "directory";
							}
							break;
					}
					if (null != badWhat) {
						final String fmt = "Error: \"%s\": argument of \"%s\" is not readable (%s).\n";
						m_errors.append(String.format(fmt, val, spec.getName(), badWhat));
					}
				}
				break;
			case eWriteableFile:
				file = new File(val);
				if (file.isFile() && !file.canWrite()) {
					m_errors.append(String.format("Error: \"%s\": argument of \"%s\" is not writeable.\n",
							val, spec.getName()));
				}
				else if (file.isDirectory()) {
					m_errors.append(String.format("Error: \"%s\": argument of \"%s\" is existing directory, expecting file.\n",
							val, spec.getName()));
				}
				else if (false == file.exists()) {
					boolean ok = true;
					if (null != file.getParent()) {
						dir = new File(file.getParent());
						if (false == dir.isDirectory()) {
							ok = dir.mkdirs();
						}
						ok = ok & dir.canWrite();
					}
					if (false == ok) {
						m_errors.append(String.format("Error: \"%s\": argument of \"%s\" is not writeable.\n",
								val, spec.getName()));
					}
				}
				break;
			default:
		}
	}
	
	private void update(Spec spec) {
		update(spec, null);
	}
	
	public static class Spec {
		
		public static enum EType {
			eString, eNumber, 
			eReadableFile, eReadableDir,
			eWriteableFile,
			eNone
		}
		
		public Spec(String nm, EType type, String usage) {
			int lix = nm.length() - 1;
			switch (nm.charAt(nm.length()-1)) {
				case '*':
					m_repeat = ERepeat.eZeroOrMore;
					break;
				case '+':
					m_repeat = ERepeat.eOneOrMore;
					break;
				case '?':
					m_repeat = ERepeat.eZeroOrOne;
					break;
				default:
					m_repeat = ERepeat.eOne;
					lix++;
			}
			m_optNm = nm.substring(0, lix);
			m_type = type;
			m_usage = usage;
		}
		
		public Spec(String nm, EType type) {
			this(nm, type, null);
		}

		public Spec(String nm) {
			this(nm, EType.eNone, null);
		}

		String getName() {
			return m_optNm;
		}
		
		ERepeat getRepeat() {
			return m_repeat;
		}
		
		EType getType() {
			return m_type;
		}
		
		static enum ERepeat {
			eOne, eZeroOrMore, eOneOrMore, eZeroOrOne
		}
		
		private String	m_optNm;
		private ERepeat	m_repeat;
		private EType	m_type;
		private String	m_usage;
	}
	
	private StringBuffer m_errors;
	private Map<String, List<String>> m_opts = new HashMap<String, List<String>>();
	private Map<String, Spec> m_specs = new HashMap<String, Spec>();
	
	public static void main(String argv[]) {
		ProcArgs pargs = new ProcArgs();
		pargs.add(new Spec("--sw1"))
			 .add(new Spec("--num1+", Spec.EType.eNumber))
			 .add(new Spec("-I*", Spec.EType.eReadableDir))
			 .add(new Spec("-o", Spec.EType.eWriteableFile))
			 .add(new Spec("--gcc", Spec.EType.eString))
			 ;
		try {
			pargs.parse(argv);
		}
		catch (ProcArgs.ArgException ex) {
			System.err.print(ex.getMessage());
		}
	}
}

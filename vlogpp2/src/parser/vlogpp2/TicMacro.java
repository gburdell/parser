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

import parser.vlogpp2.MacroDefns.FormalArg;
import parser.vlogpp2.MacroDefns.FormalArgList;
import gblib.FileLocation;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import static gblib.FileCharReader.EOF;
import static gblib.FileCharReader.NL;
import gblib.Pair;

/**
 * Handle `define and `macroUsage.
 *
 * @author gburdell
 */
public class TicMacro {

    /**
     * Process `define.
     *
     * @param src source file.
     * @return macro definition.
     * @throws ParseError
     */
    static TicMacro processDefn(final SourceFile src) throws ParseError {
        TicMacro ticDefn = new TicMacro(src);
        ticDefn.parse();
        src.getMatched().clear();
        return ticDefn;
    }

    static void processMacroUse(final SourceFile src) throws ParseError {
        TicMacro macroUse = new TicMacro(src, false);
        macroUse.processMacroUse();
        src.getMatched().clear();        
    }

    MacroDefns.Defn getDefn() {
        return new MacroDefns.Defn(m_loc, m_macroName, m_formalArgs, m_macroText);
    }

    
    private static final String stNCWS = SourceFile.stNCWS;
    static final Pattern stDefine
            = Pattern.compile("(`define(?=\\W))(" + stNCWS + "([a-zA-Z_]\\w*)(?=\\W))?");

    //LRM: White space shall be allowed between the text macro name 
    //and the left parenthesis in the macro usage.
    static final Pattern stMacroUsage
            = Pattern.compile(
                    "(`[a-zA-Z_]\\w*)(((?:[ \t]|/\\*.*?\\*/)*\\()|(?=\\W))");
                    

    private TicMacro(final SourceFile src, final boolean isDefn) throws ParseError {
        m_src = src;
        m_echoOn = m_src.setEchoOn(false);
        m_isDefn = isDefn;
    }

    private TicMacro(final SourceFile src) throws ParseError {
        this(src, true);
    }

    private int[] m_started;
    private String m_macroName;
    private final SourceFile m_src;
    private FormalArgList m_formalArgs;
    private final boolean m_echoOn;
    private String m_macroText;
    // Where `define begins
    private FileLocation m_loc;
    // true on define, false on usage.
    private final boolean m_isDefn;

    private void processMacroUse() throws ParseError {
        m_loc = m_src.getMatched().peek().e1;
        //drop leading `
        m_macroName = m_src.getMatched().remove().e2.trim().substring(1);
        final MacroDefns.Defn defn = m_src.getDefn(m_macroName);
        if (null == defn) {
            throw new ParseError("VPP-NODEFN", m_loc, m_macroName);
        }
        final boolean hasParams;
        if (m_src.getMatched().isEmpty()) {
            hasParams = false;
        } else {
            final String s = m_src.getMatched().remove().e2.trim();
            hasParams = s.isEmpty() ? false : (s.charAt(0)=='(');
        }
        assert m_src.getMatched().isEmpty();
        if (hasParams) {
            formalArguments();
        } else {
            //todo
        }        
    }
    
    private void parse() throws ParseError {
        m_started = m_src.getMatched().remove().e1.getLineColNum();
        m_loc = m_src.getMatched().peek().e1;
        m_macroName = m_src.getMatched().remove().e2;
        //we are just past macroNm
        //The left parenthesis shall follow the text macro name immediately
        //The `define macro text can also include `", `\`", and ``
        if ('(' == la()) {
            next();
            formalArguments();
        }
        m_arg.setLength(0);
        boolean loop = true;
        int c;
        while (loop) {
            c = next();
            if ('\\' == c) {
                c = next();
                if (NL == c) {
                    append(NL);
                } else {
                    append('\\').append(c);
                }
            } else {
                loop = (NL != c);
                if (loop) {
                    if (EOF == c) {
                        throw new ParseError("VPP-EOF-2", getLocation(),
                                "`define", m_started);
                    }
                    append(c);
                }
            }
        }
        m_macroText = m_arg.toString().trim();
        if (null != m_formalArgs) {
            addMarkers();
        }
        m_src.setEchoOn(m_echoOn);
    }

    private final StringBuilder m_arg = new StringBuilder();

    private static final String[] stBalanced = new String[]{
        "({[\"", ")}]\""
    };

    /**
     * Split (...) contents at commas.
     */
    private void formalArguments() throws ParseError {
        List<String> args = new LinkedList<>();
        char c;
        String arg;
        boolean loop = true;
        while (loop) {
            c = parse(new char[]{',', ')'}, 0);
            arg = m_arg.toString().trim();
            if (arg.isEmpty()) {
                throw new ParseError("VPP-FARG-1", getLocation(), m_started);
            }
            args.add(arg);
            m_arg.setLength(0);
            loop = (')' != c);
            assert (!m_src.isEOF());
        }
        m_formalArgs = new FormalArgList();
        for (final Pair<String, String> farg : Pair.factory(args)) {
            m_formalArgs.add(new FormalArg(farg));
        }
    }

    private int next() {
        return m_src.next();
    }

    private int la(int n) {
        return m_src.la(n);
    }

    private int la() {
        return la(0);
    }

    private StringBuilder append(final int c) {
        return m_arg.append((char) c);
    }

    private String getLocation() {
        return m_src.getLocation();
    }

    private char parse(final char[] returnOn, final int depth) {
        int c, n;
        while (true) {
            c = next();
            switch (c) {
                case EOF:
                    //todo: throw...
                    break;
                case NL:
                    //todo: throw...
                    break;
                case '\\':
                    append(c).append(next());
                    break;
                case '`':
                    // `", `\`", and ``
                    if ('`' == la() || '"' == la()) {
                        append(c).append(next());
                    } else if ('\\' == la() && '`' == la(1) && '"' == la(2)) {
                        append(c).append(next()).append(next()).append(next());
                    } else {
                        append(c);
                    }
                    break;
                default:
                    n = search(returnOn, (char) c);
                    if (0 <= n) {
                        return (char) c;
                    }
                    append(c);
                    n = stBalanced[0].indexOf(c);
                    if (0 <= n) {
                        parse(new char[]{stBalanced[1].charAt(n)}, depth + 1);
                        append(next());
                    }
            }
        }
    }

    private static int search(final char[] eles, final int c) {
        for (int i = 0; i < eles.length; i++) {
            if (eles[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private static final String stParmMarks[] = new String[]{
        "<'/~@", "@:/'>" //gibberish characters
    };

    /**
     * Replace each occurence of named paramter in macro text with its position
     * (1-origin).
     */
    private void addMarkers() {
        //Convert to pair of name by position
        List<Pair<String, Integer>> nmPos = new LinkedList<>();
        int i = 1;
        for (final FormalArg farg : m_formalArgs) {
            nmPos.add(new Pair(farg.getIdent(), i++));
        }
        //Sort in descending order by arg name length
        nmPos.sort((Pair<String, Integer> o1, Pair<String, Integer> o2) -> {
            final int len[] = new int[]{o1.v1.length(), o2.v1.length()};
            return (len[0] == len[1]) ? 0 : ((len[0] < len[1]) ? 1 : -1);
        });
        //Go through macroText by parms (longest to shortest).
        String replaced = m_macroText;
        String repl;
        for (final Pair<String, Integer> ele : nmPos) {
            repl = stParmMarks[0] + ele.v2.toString() + stParmMarks[1];
            replaced = replaced.replace(ele.v1, repl);
        }
        m_macroText = replaced;
    }
}

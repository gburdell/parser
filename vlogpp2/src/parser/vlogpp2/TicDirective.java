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

import java.util.regex.Pattern;

/**
 * Other compiler directives. `__FILE__ `__LINE__ `begin_keywords `celldefine
 * `default_nettype `end_keywords `endcelldefine `nounconnected_drive `pragma
 * `resetall `timescale `unconnected_drive `undef `undefineall TODO: `include
 *
 * @author gburdell
 */
public class TicDirective {

    private static final Pattern stFile = Pattern.compile("(`__FILE__)(?=\\W)");
    private static final Pattern stLine = Pattern.compile("(`__LINE__)(?=\\W)");
    private static final Pattern stProtect = Pattern.compile("(`protect(ed)?)(?=\\W)");
    private static final Pattern stEndProtect = Pattern.compile("(`endprotect(ed)?)(?=\\W)");
    private static final Pattern stCellDefine = Pattern.compile("(`(end)?celldefine?)(?=\\W)");
    private static final Pattern stResetAll = Pattern.compile("(`resetall)(?=\\W)");
    private static final Pattern stUndefineAll = Pattern.compile("(`undefineall)(?=\\W)");
    //

    /**
     * Attempt to match line to a compiler directive.
     *
     * @param src source file.
     * @param line current line in source file.
     * @return true on match. If true, caller should rescan line.
     */
    static boolean process(final SourceFile src) throws ParseError {
        boolean accepted = true;
        if (src.matches(stFile)) {
            //`__FILE__ expands to the name of the current input file, 
            //in the form of a string literal. This is the path by
            //which a tool opened the file, not the short name specified 
            //in `include or as a tool’s input file name argument.
            update(src, src.getSpan(1),
                    "\"" + src.getFileLocation().getFile().getCanonicalPath() + "\"");
        } else if (src.matches(stLine)) {
            //`__LINE__ expands to the current input line number, 
            //in the form of a simple decimal number.
            update(src, src.getSpan(1),
                    Integer.toString(src.getFileLocation().getLineNum()));
        } else if (src.matches(stProtect)) {
            /**
             * NOTE: We unconditionally process the `protect block.
             */
            final int[] started = src.getStartMark();
            final String startTok = src.getMatched(1);
            boolean loop = true;
            String line = src.setRemainder();
            while (loop) {
                src.printNL();
                if (src.isEOF()) {
                    throw new ParseError("VPP-EOF-2", src.getLocation(),
                            startTok, started[0], started[1]);
                }
                src.accept(line.length());
                line = src.setRemainder();
                loop = (!src.matches(stEndProtect));
            }
            update(src, src.getSpan(1), stEmpty);
        } else if (src.matches(stCellDefine)) {
            //from LRM: It is advisable to pair each
            //`celldefine with an `endcelldefine, but it is not required.
            src.accept(1);
        } else if (src.matches(stResetAll)) {
            src.accept(1);
        } else if (src.matches(stUndefineAll)) {
            src.accept(1);
        } else {
            accepted = false;
        }
        return accepted;
    }

    /**
     * Update source buffer with replacement text if source is doing echo. Else,
     * just update the source buffer directly to skip over this match.
     *
     * @param src source.
     * @param span span of match to replace (if applicable).
     * @param repl replacement text (if applicable).
     */
    private static void update(final SourceFile src, final int[] span,
            final String repl) {
        if (src.getEchoOn()) {
            src.replace(span[0], span[1], repl, false);
        } else {
            src.accept(span[1]);
        }
    }

    private static final String stEmpty = " ";
}

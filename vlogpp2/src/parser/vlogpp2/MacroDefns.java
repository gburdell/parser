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
import gblib.Pair;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author gburdell
 */
public class MacroDefns {

    public static enum EStrictness {

        eAllowRedefn, //false is most strict
        eRedefnSameValue,
        eRedefnSameLocation,
        eRedefnWarning, //show any redefn as warning (except when error)
        eUndefWarning, //undef of not-defined is warning
        eNotUsed    //mark last ordinal
    }

    public static void setStrictness(final boolean val, final EStrictness... flags) {
        for (EStrictness flag : flags) {
            stStrictness.set(flag.ordinal(), val);
        }
    }

    public static void setStrictness(final EStrictness... flags) {
        setStrictness(true, flags);
    }

    public static void clrStrictness(final EStrictness... flags) {
        setStrictness(false, flags);
    }

    public static boolean getStrictness(final EStrictness flag) {
        return stStrictness.get(flag.ordinal());
    }

    public boolean hasDefn(final String macroNm) {
        return m_defns.containsKey(macroNm);
    }

    static class FormalArg {

        public String getIdent() {
            return m_arg.v1;
        }

        public boolean hasDefaultText() {
            return (null != getDefaultText());
        }

        public String getDefaultText() {
            return m_arg.v2;
        }

        public FormalArg(final Pair<String, String> ele) {
            m_arg = ele;
        }
        private final Pair<String, String> m_arg;
    }

    static class FormalArgList extends LinkedList<FormalArg> {
    }

    public static class Defn {

        Defn(final FileLocation loc, final String macroNm, final FormalArgList formalArgs,
                final String macroText) {
            m_loc = loc;
            m_macroNm = macroNm;
            m_formalArgs = formalArgs;
            m_macroText = macroText;
        }

        Defn(final String macroNm, final FormalArgList formalArgs,
                final String macroText) {
            this(null, macroNm, formalArgs, macroText);
        }

        Defn(final String macroNm) {
            this(null, macroNm, null, null);
        }

        FileLocation getLocation() {
            return m_loc;
        }
        
        int getNumFormalArgs() {
            return (null != m_formalArgs) ? m_formalArgs.size() : 0;
        }
        
        // Location where defined.  null if cmdline.
        private final FileLocation m_loc;
        private final String m_macroNm;
        private final FormalArgList m_formalArgs;
        private final String m_macroText;
    }

    public Defn getDefn(final String macNm) {
        return hasDefn(macNm) ? m_defns.get(macNm) : null;
    }
    
    public void addDefn(final TicMacro defn) throws ParseError {
        addDefn(defn.getDefn());
    }

    public void addDefn(final Defn defn) throws ParseError {
        if (m_defns.containsKey(defn.m_macroNm)) {
            redefnDetected(defn);
        }
        m_defns.put(defn.m_macroNm, defn);
    }

    public void undef(final String loc, final String macroNm) {
        if (!m_defns.containsKey(macroNm) && getStrictness(EStrictness.eUndefWarning)) {
            Messages.message('W', "VPP-UNDEF-1", loc, macroNm, macroNm);
        }
    }

    public static final String stCmdLine = "<cmdline>";

    private void redefnDetected(final Defn defn) throws ParseError {
        final Defn prev = m_defns.get(defn.m_macroNm);
        final String prevLoc = getLocation(prev.m_loc),
                currLoc = getLocation(defn.m_loc);
        if (false == getStrictness(EStrictness.eAllowRedefn)) {
            throw new ParseError("VPP-REDEFN-1", currLoc, defn.m_macroNm, prevLoc);
        }
        //OK. We allow redefinition, ...
        final String prevVal = (null != prev.m_macroText) ? squeeze(prev.m_macroText) : "";
        final String val = (null != defn.m_macroText) ? squeeze(defn.m_macroText) : "";
        if (!val.equals(prevVal) && getStrictness(EStrictness.eRedefnSameValue)) {
            throw new ParseError("VPP-REDEFN-2", currLoc, defn.m_macroNm, prevLoc);
        }
        if (!FileLocation.equals(prev.m_loc, defn.m_loc) && getStrictness(EStrictness.eRedefnSameLocation)) {
            throw new ParseError("VPP-REDEFN-3", currLoc, defn.m_macroNm, prevLoc);
        }
        if (getStrictness(EStrictness.eRedefnWarning)) {
            Messages.message('W', "VPP-REDEFN-1", currLoc, defn.m_macroNm, prevLoc);
        }
    }

    private static String getLocation(final FileLocation loc) {
        return (null != loc) ? loc.toString() : stCmdLine;
    }

    private static String squeeze(String s) {
        return s.replaceAll("\\s", "");
    }

    private final Map<String, Defn> m_defns = new HashMap<>();
    private static final BitSet stStrictness = new BitSet(EStrictness.eNotUsed.ordinal());

    static {    //default: allow redefn with warning
        setStrictness(
                EStrictness.eAllowRedefn,
                EStrictness.eRedefnWarning,
                EStrictness.eUndefWarning);
        clrStrictness(EStrictness.eRedefnSameLocation, EStrictness.eRedefnSameValue);
    }

}

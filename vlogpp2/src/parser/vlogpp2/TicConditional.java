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
import gblib.Util;
import static gblib.Util.invariant;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 *
 * @author gburdell
 */
class TicConditional {

    public static enum EType {

        eIfdef, eIfndef, eElsif, eElse, eEndif
    }

    private static class State {

        private static enum EAlive {

            eNotYet, //nothing taken yet
            eActive, //currently taking/alive
            eDone, //eActive->eDone; or start new if(n)def w/in !eActive
            eInvalid //not expected
        }

        private State(final FileLocation started, final EType type,
                final EAlive alive) {
            m_where = type;
            m_alive = alive;
            m_started = started;
        }
        private EType m_where;
        private EAlive m_alive;
        private final FileLocation m_started;
    }

    static final String stNCWS = SourceFile.stNCWS;
    //`ifdef, `else, `elsif, `endif, `ifndef
    static final Pattern stIfxdefElsif
            = Pattern.compile("[ \t]*(`ifn?def|`elsif)("+stNCWS+"([a-zA-Z_]\\w*)(?=\\W))?");
    static final Pattern stElseEndif
            = Pattern.compile("[ \t]*(`else|`endif)(?=\\W)");

    private TicConditional(final SourceFile src) throws ParseError {
        m_src = src;
    }

    private final SourceFile m_src;
    private EType m_type;
    private String m_macroNm, m_directive;
    private FileLocation m_started;

    static boolean process(final SourceFile src) throws ParseError {
        TicConditional cond = new TicConditional(src);
        cond.parse();
        src.getMatched().clear();
        return cond.update();
    }

    public EType getType() {
        return m_type;
    }

    public String getMacroNm() {
        return m_macroNm;
    }

    private void parse() throws ParseError {
        m_started = m_src.getMatched().peek().e1;
        m_directive = m_src.getMatched().remove().e2;
        switch (m_directive) {
            case "`ifdef":
                m_type = EType.eIfdef;
                break;
            case "`ifndef":
                m_type = EType.eIfndef;
                break;
            case "`elsif":
                m_type = EType.eElsif;
                break;
            case "`else":
                m_type = EType.eElse;
                break;
            case "`endif":
                m_type = EType.eEndif;
                break;
            default:
                invariant(false);
        }
        if (!m_src.getMatched().isEmpty()) {
            m_macroNm = m_src.getMatched().remove().e2;
        }
        if ((null != m_macroNm) && !m_macroNm.isEmpty() && (EType.eEndif == m_type || EType.eElse == m_type)) {
            throw new ParseError("VPP-COND-2", m_src, m_macroNm, m_directive);
        }
    }

    private State.EAlive getIfdefAlive() {
        boolean ok = m_src.hasDefn(m_macroNm);
        ok = (EType.eIfndef == getType()) ? !ok : ok;
        return (ok) ? State.EAlive.eActive : State.EAlive.eNotYet;
    }

    private boolean update() throws ParseError {
        //update SourceFile state
        Stack<State> stack;
        if (null != m_src.m_cond) {
            stack = (Stack<State>) m_src.m_cond;
        } else {
            m_src.m_cond = stack = new Stack<>();
        }
        //Account for nested ifdef.
        //A new if(n)def inherits liveliness from enclosing.
        State.EAlive alive = State.EAlive.eInvalid;
        if (oneOf(getType(), EType.eIfdef, EType.eIfndef)) {
            if (stack.empty() || (stack.peek().m_alive == State.EAlive.eActive)) {
                alive = getIfdefAlive();
            } else {
                assert State.EAlive.eInvalid != stack.peek().m_alive;
                //This new if(n)def is totally dead
                alive = State.EAlive.eDone;
            }
            if (stack.size() >= stMaxConditionalDepth) {
                throw new ParseError("VPP-COND-3", m_started, stMaxConditionalDepth);
            }
            stack.push(new State(m_started, getType(), alive));
        } else if (stack.empty()) {
            throw new ParseError("VPP-COND-1", m_started);
        } else {
            final State top = stack.peek();
            switch (getType()) {
                case eElse:
                    switch (top.m_where) {
                        case eIfdef:  // if(ndef) ... else_
                        case eIfndef:
                            if (State.EAlive.eNotYet == top.m_alive) {
                                alive = State.EAlive.eActive;
                            } else {
                                assert State.EAlive.eActive == top.m_alive;
                                alive = State.EAlive.eDone;
                            }
                            update(top, alive);
                            break;
                        case eElsif:    // elsif ... else_
                            switch (top.m_alive) {
                                case eActive:
                                case eDone:
                                    alive = State.EAlive.eDone;
                                    break;
                                case eNotYet:
                                    alive = State.EAlive.eActive;
                                    break;
                                default:
                                    assert false;
                            }
                            update(top, alive);
                            break;
                        default:
                            throw new ParseError("VPP-COND-4", m_src, m_directive, top.m_started);
                    }
                    break;
                case eElsif:
                    switch (top.m_where) {
                        case eIfdef:    //ifdef ...  elsif_
                        case eIfndef:   //ifndef ... elsif_
                        case eElsif:    //elsif ... elsif_
                            switch (top.m_alive) {
                                case eActive:
                                case eDone:
                                    alive = State.EAlive.eDone;
                                    break;
                                case eNotYet:
                                    alive = getIfdefAlive();
                                    break;
                                default:
                                    assert false;
                            }
                            update(top, alive);
                            break;
                        default:
                            throw new ParseError("VPP-COND-4", m_src, m_directive, top.m_started);
                    }
                    break;
                case eEndif:
                    if (stack.empty()) {
                        throw new ParseError("VPP-COND-5", m_src);
                    }
                    stack.pop();
                    break;
                default:
                    invariant(false);
            }
        }
        final boolean echo
                = ((!stack.empty()) ? (State.EAlive.eActive == stack.peek().m_alive) : true);
        return echo;
    }

    private void update(final State top, final State.EAlive alive) {
        top.m_where = getType();
        top.m_alive = alive;
    }

    private static boolean oneOf(final EType ele, final EType... in) {
        return (0 <= Util.linearSearch(in, ele));
    }

    public static final int stMaxConditionalDepth = 256;
}

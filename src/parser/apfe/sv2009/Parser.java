//The MIT License
//
//Copyright (c) 2014-      George P. Burdell
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
package parser.apfe.sv2009;

import apfe.runtime.Acceptor;
import apfe.runtime.Acceptor.Listener;
import apfe.runtime.PrioritizedChoice;
import apfe.runtime.Repetition;
import apfe.runtime.Terminal;
import apfe.runtime.Token;
import apfe.runtime.Util;
import static apfe.runtime.Util.extractEle;
import static apfe.runtime.Util.extractList;
import apfe.sv2009.generated.hierarchical_instance;
import apfe.sv2009.generated.module_identifier;
import apfe.sv2009.generated.udp_identifier;
import apfe.sv2009.generated.module_declaration;
import apfe.sv2009.generated.module_instantiation;
import apfe.sv2009.generated.identifier;
import apfe.sv2009.generated.name_of_instance;
import apfe.sv2009.generated.interface_identifier;
import static gblib.Util.downCast;
import java.util.LinkedList;
import java.util.List;

public class Parser {

    public Parser() {
        init();
    }
    
    public Tracker getTracker() {
        return m_tracker;
    }

    /**
     * Initialize Parser by settings up Listeners to apfe.sv2009.
     */
    private void init() {
        module_identifier.addListener(stModuleIdent);
        module_declaration.addListener(stModuleDecl);
        module_instantiation.addListener(stModuleInst);
        udp_identifier.addListener(stUdpIdent);
        interface_identifier.addListener(stInterfaceIdent);
    }

    /**
     * Track definitions for this parser.
     */
    private final Tracker m_tracker = new Tracker();

    /**
     * Get Token from identifier.
     *
     * @param id identifier.
     * @return Token from identifier.
     */
    private static Token getToken(identifier id) {
        PrioritizedChoice pc = downCast(id.getBaseAccepted());
        Terminal asTerm = downCast(pc.getAccepted().getBaseAccepted());
        return asTerm.getAccepted();
    }

    /**
     * Get instance name as Token.
     *
     * @param inst instance.
     * @return instance name as Token.
     */
    private static Token getInstName(hierarchical_instance inst) {
        identifier instId = extractEle(Util.<name_of_instance>extractEle(
                inst.getBaseAccepted(), 0).getBaseAccepted(), 0);
        return getToken(instId);
    }

    private class ModuleIdentifierListener implements Listener {

        @Override
        public void onAccept(Acceptor accepted) {
            Token tok = getToken((identifier) accepted);
            m_tracker.addModule(tok);
        }

    }

    private class UdpIdentifierListener implements Listener {

        @Override
        public void onAccept(Acceptor accepted) {
            Token tok = getToken((identifier) accepted);
            m_tracker.addUdp(tok);
        }

    }

    private class InterfaceIdentifierListener implements Listener {

        @Override
        public void onAccept(Acceptor accepted) {
            Token tok = getToken((identifier) accepted);
            m_tracker.addInterface(tok);
        }

    }

    private class ModuleDeclarationListener implements Listener {

        /**
         * At end of a module declaration.
         *
         * @param unused not used.
         */
        @Override
        public void onAccept(Acceptor unused) {
            m_tracker.endModule();
        }

    }

    private class ModuleInstantiationListener implements Listener {

        /**
         * Add instances from module instantiation.
         *
         * @param acc module_instantiation.
         */
        @Override
        public void onAccept(Acceptor acc) {
            identifier ref = extractEle(acc, 0);
            Token refTok = getToken(ref);
            hierarchical_instance inst = extractEle(acc, 2);
            List<hierarchical_instance> insts = new LinkedList<>();
            insts.add(inst);
            extractList(Util.<Repetition>extractEle(acc, 3), 1, insts);
            for (hierarchical_instance hi : insts) {
                Token instTok = getInstName(hi);
                m_tracker.addInstance(refTok, instTok);
            }
        }

    }

    private final Listener stModuleIdent = new ModuleIdentifierListener(),
            stModuleDecl = new ModuleDeclarationListener(),
            stModuleInst = new ModuleInstantiationListener(),
            stUdpIdent = new UdpIdentifierListener(),
            stInterfaceIdent = new InterfaceIdentifierListener();
}

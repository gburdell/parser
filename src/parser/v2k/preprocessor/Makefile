TARGS := PreprocLexer.java PreprocLexerTokenTypes.java

${TARGS}: preproc.g
	antlr ${<}

.PHONY: clean
clean:
	-rm -f ${TARGS}

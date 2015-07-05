ANTLR ?= antlr-4.5
GRAM ?= Sv2012.g4
OPTS ?= -no-listener -no-visitor -package Sv2012 -o src/Sv2012

default: ${GRAM}
	${ANTLR} ${OPTS} ${GRAM}

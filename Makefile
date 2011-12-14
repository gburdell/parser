#The MIT License
#
#Copyright (c) 2006-2010  Karl W. Pfalzer
#
#Permission is hereby granted, free of charge, to any person obtaining a copy
#of this software and associated documentation files (the "Software"), to deal
#in the Software without restriction, including without limitation the rights
#to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#copies of the Software, and to permit persons to whom the Software is
#furnished to do so, subject to the following conditions:
#
#The above copyright notice and this permission notice shall be included in
#all copies or substantial portions of the Software.
#
#THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
#IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
#FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
#AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
#LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
#OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
#THE SOFTWARE.

ANTLR := antlr

SV_ROOT 	:= src/parser/sv
VHDL_ROOT	:= src/parser/vhdl
V2K_ROOT	:= src/parser/v2k
SLF_ROOT	:= src/parser/slf

.PHONY: all
all: ${SV_ROOT}/SysVlogParser.java \
     ${VHDL_ROOT}/VhdlParser.java  \
	 ${V2K_ROOT}/VlogppLexer.java   \
	 ${SLF_ROOT}/SlfParser.java 

.PHONY: clean
clean:
	rm -f ${V2K_ROOT}/Vlogpp*
	rm -f ${SV_ROOT}/Sys*
	rm -f ${VHDL_ROOT}/Vhdl*
	rm -f ${SLF_ROOT}/Slf*

${V2K_ROOT}/VlogParser.java: ${V2K_ROOT}/vlog.g
	(cd ${V2K_ROOT}; ${ANTLR} vlog.g)

${V2K_ROOT}/VlogppLexer.java: ${V2K_ROOT}/vlogpp.g
	(cd ${V2K_ROOT}; ${ANTLR} vlogpp.g)

${SV_ROOT}/SysVlogParser.java: ${SV_ROOT}/sysvlog.g
	(cd ${SV_ROOT}; ${ANTLR} sysvlog.g)

${VHDL_ROOT}/VhdlParser.java: ${VHDL_ROOT}/vhdl.g
	(cd ${VHDL_ROOT}; ${ANTLR} vhdl.g)

${SLF_ROOT}/SlfParser.java: ${SLF_ROOT}/slf.g
	(cd ${SLF_ROOT}; ${ANTLR} slf.g)

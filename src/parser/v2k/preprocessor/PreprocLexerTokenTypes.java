// $ANTLR 2.7.7 (2006-11-01): "preproc.g" -> "PreprocLexer.java"$

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
package parser.v2k.preprocessor;
import  antlr.LexerSharedInputState;
import  java.io.FileReader;
import  java.io.Reader;
import	java.util.HashMap;
import  static parser.Utils.abnormalExit;
import  parser.v2k.preprocessor.antlrx.*;

public interface PreprocLexerTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int MACRO_EXPANDED = 4;
	int TIC_MACRO = 5;
	int TIC_FILENM = 6;
	int TIC_LINENUM = 7;
	int TIC_BEGIN_KEYWORDS = 8;
	int TIC_CELLDEFINE = 9;
	int TIC_DEFAULT_NETTYPE = 10;
	int TIC_DEFINE = 11;
	int TIC_ELSE = 12;
	int TIC_ELSIF = 13;
	int TIC_END_KEYWORDS = 14;
	int TIC_ENDCELLDEFINE = 15;
	int TIC_ENDIF = 16;
	int TIC_ENDPROTECTED = 17;
	int TIC_IFDEF = 18;
	int TIC_IFNDEF = 19;
	int TIC_INCLUDE = 20;
	int TIC_LINE = 21;
	int TIC_NOUNCONNECTED_DRIVE = 22;
	int TIC_PRAGMA = 23;
	int TIC_PROTECTED = 24;
	int TIC_RESETALL = 25;
	int TIC_TIMESCALE = 26;
	int TIC_UNCONNECTED_DRIVE = 27;
	int TIC_UNDEF = 28;
	int TIC_UNDEFINEALL = 29;
	int TIC_DEFAULT_DECAY_TIME = 30;
	int TIC_DEFAULT_TRIREG_STRENGTH = 31;
	int TIC_DELAY_MODE_DISTRIBUTED = 32;
	int TIC_DELAY_MODE_PATH = 33;
	int TIC_DELAY_MODE_UNIT = 34;
	int TIC_DELAY_MODE_ZERO = 35;
	int TIC_PROTECT = 36;
	int TIC_ENDPROTECT = 37;
	int TIC_SUPPRESS_FAULTS = 38;
	int TIC_ENABLE_PORTFAULTS = 39;
	int TIC_DISENABLE_PORTFAULTS = 40;
	int TIC_NOSUPPRESS_FAULTS = 41;
	int LPAREN = 42;
	int RPAREN = 43;
	int LBRACK = 44;
	int RBRACK = 45;
	int LCURLY = 46;
	int RCURLY = 47;
	int EQ = 48;
	int COMMA = 49;
	int TIC_DIRECTIVE = 50;
	int IDENT = 51;
	int STRING = 52;
	int IMPL_FILENAME = 53;
	int UNSIZED_NUMBER = 54;
	int DIGIT = 55;
	int ESC_NL = 56;
	int WS = 57;
	int NL = 58;
	int CNTRL = 59;
	int SL_COMMENT = 60;
	int ML_COMMENT = 61;
	int COMMENT = 62;
	int DUMMY = 63;
}

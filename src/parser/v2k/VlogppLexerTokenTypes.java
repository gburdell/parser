// $ANTLR 2.7.7 (2006-11-01): "vlogpp.g" -> "VlogppLexer.java"$

//The MIT License
//
//Copyright (c) 2006-2010  Karl W. Pfalzer
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
package parser.v2k;
import	java.util.LinkedList;

public interface VlogppLexerTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int TIC_DIRECTIVE = 4;
	int MACRO_EXPAND = 5;
	int MACRO_EXPAND_PARMS = 6;
	int MACRO_EXPAND_PARMS2 = 7;
	int PAREN_CLOSURE = 8;
	int PAREN_CLOSURE2 = 9;
	int CURLY_CLOSURE = 10;
	int CURLY_CLOSURE2 = 11;
	int IDENT = 12;
	int STRING = 13;
	int UNSIZED_NUMBER = 14;
	int DIGIT = 15;
	int TIC_TIMESCALE = 16;
	int TIMESCALE_VALUE = 17;
	int TIC_DEFINE = 18;
	int TIC_DEFINE_PARMS = 19;
	int TIC_INCLUDE = 20;
	int DEFINE = 21;
	int WS = 22;
	int CNTRL = 23;
	int SKIP_DIRECTIVE2 = 24;
	int SKIP_DIRECTIVE = 25;
	int SL_COMMENT = 26;
	int TIC_PROTECTED = 27;
	int ML_COMMENT = 28;
	int COMMENT = 29;
	int DUMMY = 30;
}

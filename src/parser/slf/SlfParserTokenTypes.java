// $ANTLR 2.7.7 (2006-11-01): "slf.g" -> "SlfParser.java"$

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
package parser.slf;
import  java.io.*;
import  java.util.*;

public interface SlfParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int IDENT = 4;
	int STRING = 5;
	int LITERAL_library = 6;
	int LPAREN = 7;
	int RPAREN = 8;
	int LCURLY = 9;
	int RCURLY = 10;
	int LITERAL_cell = 11;
	int COLON = 12;
	int SEMI = 13;
	int LBRACK = 14;
	int RBRACK = 15;
	int COMMA = 16;
	int LITERAL_true = 17;
	int LITERAL_false = 18;
	int STAR = 19;
	int PLUS = 20;
	int NUMBER = 21;
	int INTEGER = 22;
	int FLOAT = 23;
	int DIGIT = 24;
	int WS2 = 25;
	int WS3 = 26;
	int WS = 27;
	int CNTRL = 28;
	int SL_COMMENT = 29;
	int ML_COMMENT = 30;
}

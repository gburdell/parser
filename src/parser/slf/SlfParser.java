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

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class SlfParser extends antlr.LLkParser       implements SlfParserTokenTypes
 {

	/**
	 *	Track cell declarations.
	 */
	public static Tracker	stTracker = new Tracker();

protected SlfParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public SlfParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected SlfParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public SlfParser(TokenStream lexer) {
  this(lexer,2);
}

public SlfParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final List<Library>  source_text(
		LibraryFactory lf
	) throws RecognitionException, TokenStreamException {
		List<Library> libs;
		
		Library lib = null; libs = new LinkedList<Library>();
		
		try {      // for error handling
			{
			_loop3:
			do {
				if ((LA(1)==LITERAL_library)) {
					lib=library(lf);
					if ( inputState.guessing==0 ) {
						libs.add(lib);
					}
				}
				else {
					break _loop3;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		return libs;
	}
	
	public final Library  library(
		LibraryFactory lf
	) throws RecognitionException, TokenStreamException {
		Library lib;
		
		lib = null; String libNm=null;
		
		try {      // for error handling
			match(LITERAL_library);
			match(LPAREN);
			libNm=libraryName();
			if ( inputState.guessing==0 ) {
				lib = lf.createLibrary(libNm);
			}
			match(RPAREN);
			match(LCURLY);
			{
			_loop7:
			do {
				if ((LA(1)==IDENT||LA(1)==LITERAL_cell)) {
					library_ele(lib);
				}
				else {
					break _loop7;
				}
				
			} while (true);
			}
			match(RCURLY);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		return lib;
	}
	
	public final String  libraryName() throws RecognitionException, TokenStreamException {
		String nm;
		
		Token  id = null;
		Token  str = null;
		nm=null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				id = LT(1);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					nm = id.getText();
				}
				break;
			}
			case STRING:
			{
				str = LT(1);
				match(STRING);
				if ( inputState.guessing==0 ) {
					
							String s = str.getText();
							nm = s.substring(1,s.length()-1);
						
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		return nm;
	}
	
	public final void library_ele(
		Library lib
	) throws RecognitionException, TokenStreamException {
		
		Cell cell = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				key_value(lib);
				break;
			}
			case LITERAL_cell:
			{
				cell(lib);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
	}
	
	public final void key_value(
		KeyValue.Handler kvh
	) throws RecognitionException, TokenStreamException {
		
		Token  id = null;
		Token  id2 = null;
		ValueType vt = null; List<ValueType> vtl = null; KeyValue kv = null;
		
		try {      // for error handling
			if ((LA(1)==IDENT) && (LA(2)==COLON)) {
				id = LT(1);
				match(IDENT);
				match(COLON);
				vt=value_type();
				{
				switch ( LA(1)) {
				case SEMI:
				{
					match(SEMI);
					break;
				}
				case IDENT:
				case RCURLY:
				case LITERAL_cell:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
						if (kvh.isKeyValid(id)) {
									kvh.saveKeyValue(new KeyValue(id,vt));
								}
							
				}
			}
			else if ((LA(1)==IDENT) && (LA(2)==LPAREN)) {
				id2 = LT(1);
				match(IDENT);
				match(LPAREN);
				{
				switch ( LA(1)) {
				case IDENT:
				case STRING:
				case LITERAL_library:
				case LITERAL_cell:
				case LITERAL_true:
				case LITERAL_false:
				case NUMBER:
				{
					vtl=value_type_list();
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RPAREN);
				if ( inputState.guessing==0 ) {
						if (kvh.isKeyValid(id2)) {
									kvh.saveKeyValue(new KeyValue(id2,vtl));
								}
							
				}
				{
				switch ( LA(1)) {
				case LCURLY:
				{
					value_set(kvh);
					break;
				}
				case SEMI:
				{
					match(SEMI);
					break;
				}
				case IDENT:
				case RCURLY:
				case LITERAL_cell:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
	}
	
	public final void cell(
		Library lib
	) throws RecognitionException, TokenStreamException {
		
		Token  id = null;
		Token  str = null;
		Cell cell = null; Token ctk = null;
		
		try {      // for error handling
			match(LITERAL_cell);
			match(LPAREN);
			{
			switch ( LA(1)) {
			case IDENT:
			{
				id = LT(1);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					ctk = id;
				}
				break;
			}
			case STRING:
			{
				str = LT(1);
				match(STRING);
				if ( inputState.guessing==0 ) {
					
									String s = str.getText();
									String nm = s.substring(1,s.length()-1);
									str.setText(nm);
									str.setType(SlfParserTokenTypes.IDENT);
									ctk = str;
								
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				cell = lib.createCell(ctk);
			}
			match(RPAREN);
			value_set(cell);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
	}
	
	public final void value_set(
		KeyValue.Handler kvh
	) throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LCURLY);
			{
			_loop13:
			do {
				if ((LA(1)==IDENT)) {
					key_value(kvh);
				}
				else {
					break _loop13;
				}
				
			} while (true);
			}
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				kvh.valueSetDone();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
	}
	
	public final ValueType  value_type() throws RecognitionException, TokenStreamException {
		ValueType vt;
		
		Token  id2 = null;
			vt = null; 
			List<Object> expr = null;
			String str = null;
			Bus bus = null;
			Number num = null;
			Boolean bool = null;
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case STRING:
			{
				id2 = LT(1);
				match(STRING);
				if ( inputState.guessing==0 ) {
					str = id2.getText();
				}
				if ( inputState.guessing==0 ) {
					vt = new ValueType(str);
				}
				break;
			}
			case LITERAL_true:
			case LITERAL_false:
			{
				bool=bool();
				if ( inputState.guessing==0 ) {
					vt = new ValueType(bool);
				}
				break;
			}
			default:
				boolean synPredMatched21 = false;
				if (((LA(1)==IDENT||LA(1)==NUMBER) && (LA(2)==STAR||LA(2)==PLUS))) {
					int _m21 = mark();
					synPredMatched21 = true;
					inputState.guessing++;
					try {
						{
						{
						switch ( LA(1)) {
						case IDENT:
						{
							match(IDENT);
							break;
						}
						case NUMBER:
						{
							number();
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						expr_op();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched21 = false;
					}
					rewind(_m21);
inputState.guessing--;
				}
				if ( synPredMatched21 ) {
					expr=expr();
					if ( inputState.guessing==0 ) {
						vt = new ValueType(expr);
					}
				}
				else if ((LA(1)==IDENT||LA(1)==LITERAL_library||LA(1)==LITERAL_cell) && (_tokenSet_4.member(LA(2)))) {
					str=kident();
					{
					switch ( LA(1)) {
					case LBRACK:
					{
						bus=bus();
						break;
					}
					case IDENT:
					case RPAREN:
					case RCURLY:
					case LITERAL_cell:
					case SEMI:
					case COMMA:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					if ( inputState.guessing==0 ) {
						vt = new ValueType(str, bus);
					}
				}
				else if ((LA(1)==NUMBER) && (_tokenSet_5.member(LA(2)))) {
					num=number();
					{
					if ((LA(1)==IDENT) && (_tokenSet_5.member(LA(2)))) {
						str=unit();
					}
					else if ((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2)))) {
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
					if ( inputState.guessing==0 ) {
						vt = new ValueType(num, str);
					}
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		return vt;
	}
	
	public final List<ValueType>  value_type_list() throws RecognitionException, TokenStreamException {
		List<ValueType> vtl;
		
		vtl = new LinkedList<ValueType>(); ValueType vt = null;
		
		try {      // for error handling
			vt=value_type();
			if ( inputState.guessing==0 ) {
				vtl.add(vt);
			}
			{
			_loop29:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					vt=value_type();
					if ( inputState.guessing==0 ) {
						vtl.add(vt);
					}
				}
				else {
					break _loop29;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		return vtl;
	}
	
	public final Number  number() throws RecognitionException, TokenStreamException {
		Number num;
		
		Token  id = null;
		num = null;
		
		try {      // for error handling
			id = LT(1);
			match(NUMBER);
			if ( inputState.guessing==0 ) {
				num = new Number(id.getText());
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		return num;
	}
	
	public final ExprOp  expr_op() throws RecognitionException, TokenStreamException {
		ExprOp op;
		
		Token  id = null;
		Token  id2 = null;
		op = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case STAR:
			{
				id = LT(1);
				match(STAR);
				break;
			}
			case PLUS:
			{
				id2 = LT(1);
				match(PLUS);
				if ( inputState.guessing==0 ) {
					id=id2;
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				op = new ExprOp(id);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_8);
			} else {
			  throw ex;
			}
		}
		return op;
	}
	
	public final List<Object>  expr() throws RecognitionException, TokenStreamException {
		List<Object> e;
		
		e = new LinkedList<Object>(); ExprVal ev = null; ExprOp op = null;
		
		try {      // for error handling
			ev=expr_val();
			if ( inputState.guessing==0 ) {
				e.add(ev);
			}
			{
			int _cnt33=0;
			_loop33:
			do {
				if ((LA(1)==STAR||LA(1)==PLUS)) {
					op=expr_op();
					ev=expr_val();
					if ( inputState.guessing==0 ) {
						e.add(op); e.add(ev);
					}
				}
				else {
					if ( _cnt33>=1 ) { break _loop33; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt33++;
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		return e;
	}
	
	public final String  kident() throws RecognitionException, TokenStreamException {
		String s;
		
		Token  id = null;
		Token  id2 = null;
		Token  id3 = null;
		s = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case IDENT:
			{
				id = LT(1);
				match(IDENT);
				break;
			}
			case LITERAL_cell:
			{
				id2 = LT(1);
				match(LITERAL_cell);
				if ( inputState.guessing==0 ) {
					id=id2;
				}
				break;
			}
			case LITERAL_library:
			{
				id3 = LT(1);
				match(LITERAL_library);
				if ( inputState.guessing==0 ) {
					id=id3;
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				s = id.getText();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		return s;
	}
	
	public final Bus  bus() throws RecognitionException, TokenStreamException {
		Bus bus;
		
		bus = null; Number n = null;
		
		try {      // for error handling
			match(LBRACK);
			n=number();
			if ( inputState.guessing==0 ) {
				bus = new Bus(n);
			}
			{
			switch ( LA(1)) {
			case COLON:
			{
				match(COLON);
				n=number();
				if ( inputState.guessing==0 ) {
					bus.add(n);
				}
				break;
			}
			case RBRACK:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RBRACK);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		return bus;
	}
	
	public final String  unit() throws RecognitionException, TokenStreamException {
		String v;
		
		Token  id = null;
		v = null;
		
		try {      // for error handling
			id = LT(1);
			match(IDENT);
			if ( inputState.guessing==0 ) {
				v = id.getText();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		return v;
	}
	
	public final Boolean  bool() throws RecognitionException, TokenStreamException {
		Boolean b;
		
		b = true;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_true:
			{
				match(LITERAL_true);
				break;
			}
			case LITERAL_false:
			{
				match(LITERAL_false);
				if ( inputState.guessing==0 ) {
					b = false;
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		return b;
	}
	
	public final ExprVal  expr_val() throws RecognitionException, TokenStreamException {
		ExprVal val;
		
		Token  id = null;
		val = null; Number n = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				id = LT(1);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					val = new ExprVal(id);
				}
				break;
			}
			case NUMBER:
			{
				n=number();
				if ( inputState.guessing==0 ) {
					val = new ExprVal(n);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		return val;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"IDENT",
		"STRING",
		"\"library\"",
		"LPAREN",
		"RPAREN",
		"LCURLY",
		"RCURLY",
		"\"cell\"",
		"COLON",
		"SEMI",
		"LBRACK",
		"RBRACK",
		"COMMA",
		"\"true\"",
		"\"false\"",
		"STAR",
		"PLUS",
		"NUMBER",
		"INTEGER",
		"FLOAT",
		"DIGIT",
		"WS2",
		"WS3",
		"WS",
		"CNTRL",
		"SL_COMMENT",
		"ML_COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 66L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 256L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 3088L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 93456L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 77072L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 2506482L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 1686800L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 2097168L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 1649936L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	
	}

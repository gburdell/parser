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

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class VlogppLexer extends antlr.CharScanner implements VlogppLexerTokenTypes, TokenStream
 {

	//Embelish VlogppLexer class

	static void setPP(Preproc pp) {
		stPP = pp;
	}

	private static Preproc stPP = null;

	@Override
	public void uponEOF() throws TokenStreamException, CharStreamException {
		stPP.uponEOF();
	}

	private static String scrub2nl(String s) {
		String t = "";
		int i = 0;
		while (true) {
			i = s.indexOf("\n", i);
			if (0 <= i) {
				t += "\n";
				i++;
			} else {
				break; //while
			}
		}
		return t;
	}
public VlogppLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public VlogppLexer(Reader in) {
	this(new CharBuffer(in));
}
public VlogppLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public VlogppLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				if ((LA(1)=='`') && (_tokenSet_0.member(LA(2)))) {
					mTIC_DIRECTIVE(true);
					theRetToken=_returnToken;
				}
				else if ((LA(1)=='"') && (_tokenSet_1.member(LA(2)))) {
					mSTRING(true);
					theRetToken=_returnToken;
				}
				else if ((LA(1)=='/') && (LA(2)=='*'||LA(2)=='/')) {
					mCOMMENT(true);
					theRetToken=_returnToken;
				}
				else if ((_tokenSet_0.member(LA(1))) && (true)) {
					mIDENT(true);
					theRetToken=_returnToken;
				}
				else if ((_tokenSet_2.member(LA(1))) && (true)) {
					mWS(true);
					theRetToken=_returnToken;
				}
				else if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && (true)) {
					mDUMMY(true);
					theRetToken=_returnToken;
				}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mTIC_DIRECTIVE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TIC_DIRECTIVE;
		int _saveIndex;
		Token id=null;
		Token td=null;
		Token id5=null;
		Token id7=null;
		Token id9=null;
		Token id10=null;
		Token id11=null;
		
		match('`');
		mIDENT(true);
		id=_returnToken;
		{
		if (((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2))))&&(id.getText().equals("define"))) {
			mTIC_DEFINE(true);
			td=_returnToken;
			if ( inputState.guessing==0 ) {
				
									String s = td.getText();
									if (Parser.stDumpDefn) {
										text.setLength(_begin); text.append("/*@DEFINE@ "+s+"*/");
									} else {
										String t = scrub2nl(s);
										text.setLength(_begin); text.append(t);
									}
								
			}
		}
		else if (((_tokenSet_2.member(LA(1))) && (_tokenSet_4.member(LA(2))))&&(id.getText().equals("include"))) {
			mTIC_INCLUDE(false);
		}
		else if ((((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff')))&&(id.getText().equals("protected"))) {
			mTIC_PROTECTED(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (((_tokenSet_2.member(LA(1))) && (_tokenSet_5.member(LA(2))))&&(id.getText().equals("timescale"))) {
			mTIC_TIMESCALE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2))))&&(id.getText().equals("ifdef"))) {
			{
			int _cnt4=0;
			_loop4:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					mWS(false);
				}
				else {
					if ( _cnt4>=1 ) { break _loop4; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt4++;
			} while (true);
			}
			mIDENT(true);
			id5=_returnToken;
			if ( inputState.guessing==0 ) {
				stPP.ticIfdef(id5.getText()); _ttype = Token.SKIP;
			}
		}
		else if (((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2))))&&(id.getText().equals("ifndef"))) {
			{
			int _cnt6=0;
			_loop6:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					mWS(false);
				}
				else {
					if ( _cnt6>=1 ) { break _loop6; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt6++;
			} while (true);
			}
			mIDENT(true);
			id7=_returnToken;
			if ( inputState.guessing==0 ) {
				stPP.ticIfndef(id7.getText()); _ttype = Token.SKIP;
			}
		}
		else if (((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2))))&&(id.getText().equals("elsif"))) {
			{
			int _cnt8=0;
			_loop8:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					mWS(false);
				}
				else {
					if ( _cnt8>=1 ) { break _loop8; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt8++;
			} while (true);
			}
			mIDENT(true);
			id9=_returnToken;
			if ( inputState.guessing==0 ) {
				stPP.ticElsif(id9.getText()); _ttype = Token.SKIP;
			}
		}
		else if (((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2))))&&(id.getText().equals("undef"))) {
			{
			int _cnt10=0;
			_loop10:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					mWS(false);
				}
				else {
					if ( _cnt10>=1 ) { break _loop10; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt10++;
			} while (true);
			}
			mIDENT(true);
			id10=_returnToken;
			if ( inputState.guessing==0 ) {
				stPP.undef(id10.getText()); _ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("else"))) {
			if ( inputState.guessing==0 ) {
				stPP.ticElse(); _ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("endif"))) {
			if ( inputState.guessing==0 ) {
				stPP.ticEndif(); _ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("celldefine"))) {
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("endcelldefine"))) {
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("default_nettype"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("begin_keywords"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("end_keywords"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("line"))) {
			mSKIP_DIRECTIVE2(true);
			id11=_returnToken;
			if ( inputState.guessing==0 ) {
					
									String txt = "`line "+id11.getText();
									text.setLength(_begin); text.append(txt);
									//$setType(Token.SKIP);
								
			}
		}
		else if (( true )&&(id.getText().equals("unconnected_drive"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("nounconnected_drive"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("pragma"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("resetall"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("default_decay_time"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("default_trireg_strength"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("delay_mode_distributed"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("delay_mode_path"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("delay_mode_unit"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("delay_mode_zero"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("protect"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("endprotect"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("suppress_faults"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("enable_portfaults"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("disable_portfaults"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else if (( true )&&(id.getText().equals("nosuppress_faults"))) {
			mSKIP_DIRECTIVE(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else {
			mMACRO_EXPAND(false,id.getText());
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IDENT;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			matchRange('a','z');
			break;
		}
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':
		{
			matchRange('A','Z');
			break;
		}
		case '_':
		{
			match('_');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		_loop50:
		do {
			if (((LA(1) >= 'a' && LA(1) <= 'z')) && (true)) {
				matchRange('a','z');
			}
			else if (((LA(1) >= 'A' && LA(1) <= 'Z')) && (true)) {
				matchRange('A','Z');
			}
			else if ((LA(1)=='_') && (true)) {
				match('_');
			}
			else if ((LA(1)=='$') && (true)) {
				match('$');
			}
			else if (((LA(1) >= '0' && LA(1) <= '9')) && (true)) {
				matchRange('0','9');
			}
			else {
				break _loop50;
			}
			
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTIC_DEFINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TIC_DEFINE;
		int _saveIndex;
		Token id=null;
		Token def=null;
		
		{
		int _cnt72=0;
		_loop72:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				mWS(false);
			}
			else {
				if ( _cnt72>=1 ) { break _loop72; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt72++;
		} while (true);
		}
		mIDENT(true);
		id=_returnToken;
		{
		boolean synPredMatched75 = false;
		if (((LA(1)=='(') && (_tokenSet_6.member(LA(2))))) {
			int _m75 = mark();
			synPredMatched75 = true;
			inputState.guessing++;
			try {
				{
				match('(');
				}
			}
			catch (RecognitionException pe) {
				synPredMatched75 = false;
			}
			rewind(_m75);
inputState.guessing--;
		}
		if ( synPredMatched75 ) {
			mTIC_DEFINE_PARMS(false,id.getText());
		}
		else {
			mDEFINE(true);
			def=_returnToken;
			if ( inputState.guessing==0 ) {
					
									stPP.addDefine(id.getText(), def.getText());
								
			}
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTIC_INCLUDE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TIC_INCLUDE;
		int _saveIndex;
		Token fn=null;
		
		{
		int _cnt90=0;
		_loop90:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				mWS(false);
			}
			else {
				if ( _cnt90>=1 ) { break _loop90; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt90++;
		} while (true);
		}
		mSTRING(true);
		fn=_returnToken;
		if ( inputState.guessing==0 ) {
				
						stPP.includeFile(fn.getText());
					
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTIC_PROTECTED(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TIC_PROTECTED;
		int _saveIndex;
		
		{
		int _cnt120=0;
		_loop120:
		do {
			if ((((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff')))&&(LA(1) != '`')) {
				{
				if ((LA(1)=='\r') && (LA(2)=='\n')) {
					match('\r');
					match('\n');
					if ( inputState.guessing==0 ) {
						newline();
					}
				}
				else if ((LA(1)=='\r') && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
					match('\r');
					if ( inputState.guessing==0 ) {
						newline();
					}
				}
				else if ((LA(1)=='\n')) {
					match('\n');
					if ( inputState.guessing==0 ) {
						newline();
					}
				}
				else if ((_tokenSet_7.member(LA(1)))) {
					{
					match(_tokenSet_7);
					}
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
			}
			else {
				if ( _cnt120>=1 ) { break _loop120; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt120++;
		} while (true);
		}
		match("`endprotected");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTIC_TIMESCALE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TIC_TIMESCALE;
		int _saveIndex;
		
		{
		int _cnt62=0;
		_loop62:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				mWS(false);
			}
			else {
				if ( _cnt62>=1 ) { break _loop62; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt62++;
		} while (true);
		}
		mTIMESCALE_VALUE(false);
		{
		_loop64:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				mWS(false);
			}
			else {
				break _loop64;
			}
			
		} while (true);
		}
		match('/');
		{
		_loop66:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				mWS(false);
			}
			else {
				break _loop66;
			}
			
		} while (true);
		}
		mTIMESCALE_VALUE(false);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WS;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case ' ':
		{
			match(' ');
			break;
		}
		case '\r':
		{
			match('\r');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		case '\n':
		{
			match('\n');
			if ( inputState.guessing==0 ) {
				newline();
			}
			break;
		}
		default:
			if ((_tokenSet_8.member(LA(1)))) {
				mCNTRL(false);
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSKIP_DIRECTIVE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SKIP_DIRECTIVE;
		int _saveIndex;
		
		mSKIP_DIRECTIVE2(false);
		{
		switch ( LA(1)) {
		case '\n':
		{
			match('\n');
			break;
		}
		case '\r':
		{
			match('\r');
			{
			if ((LA(1)=='\n')) {
				match('\n');
			}
			else {
			}
			
			}
			break;
		}
		default:
			{
			}
		}
		}
		if ( inputState.guessing==0 ) {
			newline();
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSKIP_DIRECTIVE2(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SKIP_DIRECTIVE2;
		int _saveIndex;
		
		{
		_loop106:
		do {
			if ((_tokenSet_7.member(LA(1)))) {
				{
				match(_tokenSet_7);
				}
			}
			else {
				break _loop106;
			}
			
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mMACRO_EXPAND(boolean _createToken,
		String key
	) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MACRO_EXPAND;
		int _saveIndex;
		
		if (((_tokenSet_9.member(LA(1))))&&(stPP.hasParams(key))) {
			mMACRO_EXPAND_PARMS(false,key);
		}
		else {
			if ( inputState.guessing==0 ) {
				
								if (stPP.okToDefine(key)) {
									stPP.expandMacro(key);
								}
							
			}
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mMACRO_EXPAND_PARMS(boolean _createToken,
		String key
	) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MACRO_EXPAND_PARMS;
		int _saveIndex;
		
			LinkedList<String> parms = null;
		
		
		{
		_loop15:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				mWS(false);
				{
				if ((_tokenSet_2.member(LA(1))) && (_tokenSet_9.member(LA(2)))) {
					_saveIndex=text.length();
					mWS(false);
					text.setLength(_saveIndex);
				}
				else if ((_tokenSet_9.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
			}
			else {
				break _loop15;
			}
			
		} while (true);
		}
		match('(');
		{
		if ((_tokenSet_2.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
			_saveIndex=text.length();
			mWS(false);
			text.setLength(_saveIndex);
		}
		else if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && (true)) {
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		parms=mMACRO_EXPAND_PARMS2(false);
		{
		if ((_tokenSet_2.member(LA(1)))) {
			_saveIndex=text.length();
			mWS(false);
			text.setLength(_saveIndex);
		}
		else if ((LA(1)==')')) {
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		match(')');
		{
		if ((_tokenSet_2.member(LA(1)))) {
			_saveIndex=text.length();
			mWS(false);
			text.setLength(_saveIndex);
		}
		else {
		}
		
		}
		if ( inputState.guessing==0 ) {
			
						if (stPP.okToDefine(key)) {
							stPP.expandMacro(key, parms);
						}
					
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final LinkedList<String>  mMACRO_EXPAND_PARMS2(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		LinkedList<String> parms;
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MACRO_EXPAND_PARMS2;
		int _saveIndex;
		Token s1=null;
		
			parms = new LinkedList<String>();
			String s = null;
			StringBuffer sbuf = new StringBuffer();
		
		
		{
		_loop22:
		do {
			if ((_tokenSet_10.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
				mPAREN_CLOSURE2(true);
				s1=_returnToken;
				{
				if ((_tokenSet_2.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
					_saveIndex=text.length();
					mWS(false);
					text.setLength(_saveIndex);
				}
				else if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && (true)) {
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
				if ( inputState.guessing==0 ) {
						s = s1.getText();
									if (',' == s.charAt(0)) {
										if (0 < sbuf.length()) {
											parms.add(sbuf.toString());
										}
										sbuf = new StringBuffer();
									} else {
										sbuf.append(s);
									}
								
				}
			}
			else {
				break _loop22;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
				if (0 < sbuf.length()) {
							parms.add(sbuf.toString());
						}
					
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
		return parms;
	}
	
	protected final void mPAREN_CLOSURE2(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PAREN_CLOSURE2;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '{':
		{
			mCURLY_CLOSURE(false);
			break;
		}
		case '"':
		{
			mSTRING(false);
			break;
		}
		case '(':
		{
			mPAREN_CLOSURE(false);
			break;
		}
		default:
			if ((_tokenSet_11.member(LA(1)))) {
				matchNot(')');
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mPAREN_CLOSURE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PAREN_CLOSURE;
		int _saveIndex;
		
		match('(');
		{
		if ((_tokenSet_2.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
			_saveIndex=text.length();
			mWS(false);
			text.setLength(_saveIndex);
		}
		else if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		{
		_loop27:
		do {
			if ((_tokenSet_10.member(LA(1)))) {
				mPAREN_CLOSURE2(false);
				{
				if ((_tokenSet_2.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
					_saveIndex=text.length();
					mWS(false);
					text.setLength(_saveIndex);
				}
				else if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
			}
			else {
				break _loop27;
			}
			
		} while (true);
		}
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCURLY_CLOSURE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CURLY_CLOSURE;
		int _saveIndex;
		
		match('{');
		{
		if ((_tokenSet_2.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
			_saveIndex=text.length();
			mWS(false);
			text.setLength(_saveIndex);
		}
		else if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		{
		_loop39:
		do {
			if ((_tokenSet_12.member(LA(1)))) {
				mCURLY_CLOSURE2(false);
				{
				if ((_tokenSet_2.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
					_saveIndex=text.length();
					mWS(false);
					text.setLength(_saveIndex);
				}
				else if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
			}
			else {
				break _loop39;
			}
			
		} while (true);
		}
		match('}');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STRING;
		int _saveIndex;
		
		match('"');
		{
		_loop54:
		do {
			if ((_tokenSet_13.member(LA(1)))) {
				{
				match(_tokenSet_13);
				}
			}
			else {
				break _loop54;
			}
			
		} while (true);
		}
		match('"');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCURLY_CLOSURE2(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CURLY_CLOSURE2;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '(':
		{
			mPAREN_CLOSURE(false);
			break;
		}
		case '"':
		{
			mSTRING(false);
			break;
		}
		case '{':
		{
			mCURLY_CLOSURE(false);
			break;
		}
		default:
			if ((_tokenSet_14.member(LA(1)))) {
				matchNot('}');
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mUNSIZED_NUMBER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = UNSIZED_NUMBER;
		int _saveIndex;
		
		mDIGIT(false);
		{
		_loop57:
		do {
			if ((LA(1)=='_') && (_tokenSet_15.member(LA(2)))) {
				match('_');
			}
			else if (((LA(1) >= '0' && LA(1) <= '9'))) {
				mDIGIT(false);
			}
			else {
				break _loop57;
			}
			
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mDIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIGIT;
		int _saveIndex;
		
		{
		matchRange('0','9');
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTIMESCALE_VALUE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TIMESCALE_VALUE;
		int _saveIndex;
		
		mUNSIZED_NUMBER(false);
		{
		_loop69:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				mWS(false);
			}
			else {
				break _loop69;
			}
			
		} while (true);
		}
		mIDENT(false);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTIC_DEFINE_PARMS(boolean _createToken,
		String id
	) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TIC_DEFINE_PARMS;
		int _saveIndex;
		Token id2=null;
		Token id3=null;
		Token def=null;
		
			LinkedList<String> parms = new LinkedList<String>();
		
		
		match('(');
		{
		if ((_tokenSet_3.member(LA(1))) && (_tokenSet_16.member(LA(2)))) {
			{
			_loop79:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					mWS(false);
				}
				else {
					break _loop79;
				}
				
			} while (true);
			}
			mIDENT(true);
			id2=_returnToken;
			if ( inputState.guessing==0 ) {
				parms.add(id2.getText());
			}
			{
			_loop85:
			do {
				if ((_tokenSet_17.member(LA(1))) && (_tokenSet_18.member(LA(2)))) {
					{
					_loop82:
					do {
						if ((_tokenSet_2.member(LA(1)))) {
							mWS(false);
						}
						else {
							break _loop82;
						}
						
					} while (true);
					}
					match(',');
					{
					_loop84:
					do {
						if ((_tokenSet_2.member(LA(1)))) {
							mWS(false);
						}
						else {
							break _loop84;
						}
						
					} while (true);
					}
					mIDENT(true);
					id3=_returnToken;
					if ( inputState.guessing==0 ) {
						parms.add(id3.getText());
					}
				}
				else {
					break _loop85;
				}
				
			} while (true);
			}
		}
		else if ((_tokenSet_19.member(LA(1))) && (true)) {
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		{
		_loop87:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				mWS(false);
			}
			else {
				break _loop87;
			}
			
		} while (true);
		}
		match(')');
		mDEFINE(true);
		def=_returnToken;
		if ( inputState.guessing==0 ) {
			
						stPP.addDefine(id, parms, def.getText());
					
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mDEFINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DEFINE;
		int _saveIndex;
		
		{
		_loop97:
		do {
			boolean synPredMatched95 = false;
			if (((LA(1)=='"') && (_tokenSet_1.member(LA(2))))) {
				int _m95 = mark();
				synPredMatched95 = true;
				inputState.guessing++;
				try {
					{
					match('"');
					}
				}
				catch (RecognitionException pe) {
					synPredMatched95 = false;
				}
				rewind(_m95);
inputState.guessing--;
			}
			if ( synPredMatched95 ) {
				mSTRING(false);
			}
			else if ((LA(1)=='\\')) {
				match('\\');
				{
				switch ( LA(1)) {
				case '\r':
				{
					match('\r');
					break;
				}
				case '\n':
				{
					break;
				}
				default:
				{
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}
				}
				match('\n');
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
			else if (((_tokenSet_20.member(LA(1))) && (true))&&(LA(1) != EOF_CHAR)) {
				{
				match(_tokenSet_20);
				}
			}
			else {
				break _loop97;
			}
			
		} while (true);
		}
		{
		if ((LA(1)=='\r')) {
			match('\r');
		}
		else {
		}
		
		}
		{
		if ((LA(1)=='\n')) {
			match('\n');
			if ( inputState.guessing==0 ) {
				newline();
			}
		}
		else {
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCNTRL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CNTRL;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '\u0000':  case '\u0001':  case '\u0002':  case '\u0003':
		case '\u0004':  case '\u0005':  case '\u0006':  case '\u0007':
		case '\u0008':
		{
			matchRange('\u0000','\u0008');
			break;
		}
		case '\u000b':  case '\u000c':
		{
			matchRange('\u000B','\u000C');
			break;
		}
		case '\u000e':  case '\u000f':  case '\u0010':  case '\u0011':
		case '\u0012':  case '\u0013':  case '\u0014':  case '\u0015':
		case '\u0016':  case '\u0017':  case '\u0018':  case '\u0019':
		case '\u001a':  case '\u001b':  case '\u001c':  case '\u001d':
		case '\u001e':  case '\u001f':
		{
			matchRange('\u000E','\u001F');
			break;
		}
		default:
			if (((LA(1) >= '\u007f' && LA(1) <= '\u00ff'))) {
				matchRange('\u007F','\u00FF');
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSL_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SL_COMMENT;
		int _saveIndex;
		
		match("//");
		{
		_loop113:
		do {
			if ((_tokenSet_7.member(LA(1)))) {
				{
				match(_tokenSet_7);
				}
			}
			else {
				break _loop113;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case '\n':
		{
			match('\n');
			break;
		}
		case '\r':
		{
			match('\r');
			{
			if ((LA(1)=='\n')) {
				match('\n');
			}
			else {
			}
			
			}
			break;
		}
		default:
			{
			}
		}
		}
		if ( inputState.guessing==0 ) {
			newline();
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mML_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ML_COMMENT;
		int _saveIndex;
		
		match("/*");
		{
		_loop124:
		do {
			if (((LA(1)=='*') && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff')))&&( LA(2)!='/' )) {
				match('*');
			}
			else if ((LA(1)=='\r') && (LA(2)=='\n')) {
				match('\r');
				match('\n');
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
			else if ((LA(1)=='\r') && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff'))) {
				match('\r');
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
			else if ((LA(1)=='\n')) {
				match('\n');
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
			else if ((_tokenSet_21.member(LA(1)))) {
				{
				match(_tokenSet_21);
				}
			}
			else {
				break _loop124;
			}
			
		} while (true);
		}
		match("*/");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMENT;
		int _saveIndex;
		Token id=null;
		Token id2=null;
		Token tok = null;
		
		{
		if ((LA(1)=='/') && (LA(2)=='/')) {
			mSL_COMMENT(true);
			id=_returnToken;
			if ( inputState.guessing==0 ) {
				tok=id;
			}
		}
		else if ((LA(1)=='/') && (LA(2)=='*')) {
			mML_COMMENT(true);
			id2=_returnToken;
			if ( inputState.guessing==0 ) {
				tok=id2;
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( inputState.guessing==0 ) {
			
						if (false == Parser.stShowComments) {
							String s = tok.getText();
							String t = scrub2nl(s);
							text.setLength(_begin); text.append(t);
						}
					
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDUMMY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DUMMY;
		int _saveIndex;
		
		matchNot(EOF_CHAR);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 0L, 576460745995190270L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[8];
		data[0]=-1025L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[8];
		data[0]=8589934591L;
		data[1]=-9223372036854775808L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[8];
		data[0]=8589934591L;
		data[1]=-8646911290859585538L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[8];
		data[0]=25769803775L;
		data[1]=-9223372036854775808L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = new long[8];
		data[0]=287948909764935679L;
		data[1]=-9223372036854775808L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[8];
		data[0]=2207613190143L;
		data[1]=-8646911290859585538L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = new long[8];
		data[0]=-9217L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = new long[8];
		data[0]=4294957567L;
		data[1]=-9223372036854775808L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = new long[8];
		data[0]=1108101562367L;
		data[1]=-9223372036854775808L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = new long[8];
		data[0]=-2199023255553L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = new long[8];
		data[0]=-3315714752513L;
		data[1]=-576460752303423489L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = new long[8];
		data[0]=-1L;
		data[1]=-2305843009213693953L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = new long[8];
		data[0]=-17179870209L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = new long[8];
		data[0]=-1116691496961L;
		data[1]=-2882303761517117441L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = new long[8];
		data[0]=287948909764935679L;
		data[1]=-8646911290859585538L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = new long[8];
		data[0]=287968769693712383L;
		data[1]=-8646911290859585538L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = new long[8];
		data[0]=17600775979007L;
		data[1]=-9223372036854775808L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = new long[8];
		data[0]=17600775979007L;
		data[1]=-8646911290859585538L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = new long[8];
		data[0]=2207613190143L;
		data[1]=-9223372036854775808L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = new long[8];
		data[0]=-9217L;
		data[1]=-268435457L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = new long[8];
		data[0]=-4398046520321L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	
	}

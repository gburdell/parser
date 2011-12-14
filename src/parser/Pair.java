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
package parser;

import	java.util.StringTokenizer;
import	java.util.LinkedList;
import	java.util.List;

/**
 *
 * @author karl
 */
public class Pair<T1,T2> {
	/**Create list of Pair parsing key(=val)?
	 */
	public static List<Pair<String,String> > factory(
		List<String> keyVals, String delim) {
		List<Pair<String,String> > rval = 
			new LinkedList<Pair<String,String> >();
		for (String kv : keyVals) {
			StringTokenizer toks = new StringTokenizer(kv, delim);
			String key = toks.nextToken();
			String val = toks.hasMoreTokens() ? toks.nextToken() : null;
			rval.add(new Pair<String,String>(key,val));
		}
		return rval;
	}

	public static List<Pair<String,String> > factory(List<String> keyVals) {
		return factory(keyVals, "=");
	}

    public Pair(T1 v1, T2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    public T1   v1;
    public T2   v2;
}

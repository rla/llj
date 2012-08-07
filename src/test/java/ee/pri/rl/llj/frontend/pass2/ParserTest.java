/*
Copyright (C) 2008-2010 Raivo Laanemets

This file is part of Logic Language for Java (LLJ).

LLJ is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

LLJ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with LLJ.  If not, see <http://www.gnu.org/licenses/>.
*/
package ee.pri.rl.llj.frontend.pass2;

import java.io.BufferedReader;
import java.io.StringReader;

import junit.framework.TestCase;
import ee.pri.rl.llj.common.term.FloatAtom;
import ee.pri.rl.llj.common.term.LongAtom;
import ee.pri.rl.llj.common.term.StringAtom;
import ee.pri.rl.llj.common.term.Struct;
import ee.pri.rl.llj.common.term.Term;
import ee.pri.rl.llj.frontend.pass1.Tokenizer;

public class ParserTest extends TestCase {
	
	public void testInteger() {
		LongAtom t = (LongAtom) parseSingleTerm("123");
		assertEquals(123, t.value.intValue());
	}
	
	public void testNegInteger() {
		LongAtom t = (LongAtom) parseSingleTerm("-123");
		assertEquals(-123, t.value.intValue());
	}
	
	
	public void testFloat() {
		FloatAtom t = (FloatAtom) parseSingleTerm("123.0");
		assertEquals(123.0f, t.value.floatValue());
	}
	
	public void testNegFloat() {
		FloatAtom t = (FloatAtom) parseSingleTerm("-123.0");
		assertEquals(-123.0f, t.value.floatValue());
	}
	
	public void testString() {
		StringAtom t = (StringAtom) parseSingleTerm("abc");
		assertEquals("abc", t.value);
	}
	
	public void testStruct() {
		Struct t = (Struct) parseSingleTerm("abc(def,ghj)");
		assertEquals("abc", t.name);
		assertEquals(2, t.getArity());
		
		StringAtom a1 = (StringAtom) t.first();
		StringAtom a2 = (StringAtom) t.second();
		
		assertEquals("def", a1.value);
		assertEquals("ghj", a2.value);
	}

	private Term parseSingleTerm(String input) {
		Tokenizer tokenizer = new Tokenizer(new BufferedReader(new StringReader(input)));
		Parser parser = new Parser(tokenizer);
		Term term = parser.parseSingleTerm();
		assertTrue(tokenizer.isEndOfInput());

		return term;
	}
}

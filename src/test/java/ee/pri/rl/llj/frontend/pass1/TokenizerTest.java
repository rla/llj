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
package ee.pri.rl.llj.frontend.pass1;

import java.io.BufferedReader;
import java.io.StringReader;

import junit.framework.TestCase;

public class TokenizerTest extends TestCase {
	
	public void testInteger() {
		Token token = singleToken("123");
		assertEquals(TokenType.INTEGER, token.type);
		assertEquals("123", token.data);
	}
	
	public void testInteger2() {
		Token token = singleToken("1234567890");
		assertEquals(TokenType.INTEGER, token.type);
		assertEquals("1234567890", token.data);
	}
	
	public void testFloat() {
		Token token = singleToken("123.123");
		assertEquals(TokenType.FLOAT, token.type);
		assertEquals("123.123", token.data);
	}
	
	public void testString() {
		Token token = singleToken("abc");
		assertEquals(TokenType.STRING, token.type);
		assertEquals("abc", token.data);
	}
	
	public void testString2() {
		Token token = singleToken("@abc");
		assertEquals(TokenType.STRING, token.type);
		assertEquals("@abc", token.data);
	}
	
	public void testQuotedString() {
		Token token = singleToken("\"abc\"");
		assertEquals(TokenType.STRING, token.type);
		assertEquals("abc", token.data);
	}
	
	public void testEscapedString() {
		Token token = singleToken("\"\\n\\r\\t\\\\\"");
		assertEquals(TokenType.STRING, token.type);
		assertEquals("\n\r\t\\", token.data);
	}
	
	public void testLParen() {
		Token token = singleToken("(");
		assertEquals(TokenType.LPAREN, token.type);
		assertEquals("(", token.data);
	}
	
	public void testRParen() {
		Token token = singleToken(")");
		assertEquals(TokenType.RPAREN, token.type);
		assertEquals(")", token.data);
	}
	
	public void testDot() {
		Token token = singleToken(".");
		assertEquals(TokenType.DOT, token.type);
		assertEquals(".", token.data);
	}
	
	public void testListStart() {
		Token token = singleToken("[");
		assertEquals(TokenType.LIST_START, token.type);
		assertEquals("[", token.data);
	}
	
	public void testListEnd() {
		Token token = singleToken("]");
		assertEquals(TokenType.LIST_END, token.type);
		assertEquals("]", token.data);
	}
	
	public void testEmptyList() {
		Token token = singleToken("[]");
		assertEquals(TokenType.LIST_TAIL, token.type);
		assertEquals("[]", token.data);
	}
	
	public void testListCons() {
		Token token = singleToken("|");
		assertEquals(TokenType.LIST_CONS, token.type);
		assertEquals("|", token.data);
	}
	
	public void testVariable() {
		Token token = singleToken("V1");
		assertEquals(TokenType.VARIABLE, token.type);
		assertEquals("V1", token.data);
	}
	
	public void testOperator() {
		Token token = singleToken("+");
		assertEquals(TokenType.OPERATOR, token.type);
		assertEquals("+", token.data);
	}
	
	public void testDivOperator() {
		Token token = singleToken("/");
		assertEquals(TokenType.OPERATOR, token.type);
		assertEquals("/", token.data);
	}
	
	public void testLineComment() {
		Token token = singleToken("%aaaaa");
		assertEquals(TokenType.COMMENT, token.type);
	}
	
	// FIXME fix tests
	//public void testBlockComment() {
	//	Token token = singleToken("/*aaaaa*/");
	//	assertEquals(TokenType.COMMENT, token.type);
	//	assertEquals("aaaaa", token.data);
	//}
	
	/*
	public void testUnterminatedString() {
		try {
			Tokenizer tokenizer = new Tokenizer("\"aaaaa");
			tokenizer.read();
		} catch (LLJException e) {
			// OK, must throw exception
			return;
		}
		
		fail("Unterminated string did not fail");
	}
	
	public void testUnterminatedBlockComment() {
		try {
			Tokenizer tokenizer = new Tokenizer("/*aaaaa");
			tokenizer.read();
		} catch (LLJException e) {
			// OK, must throw exception
			return;
		}
		
		fail("Unterminated block comment did not fail");
	}
	
	public void testInvalidInteger() {
		try {
			Tokenizer tokenizer = new Tokenizer("12345678901234");
			tokenizer.read();
		} catch (LLJException e) {
			// OK, must throw exception
			return;
		}
		
		fail("Invalid int did not fail");
	}
	
	public void testInvalidInteger2() {
		try {
			Tokenizer tokenizer = new Tokenizer("001");
			tokenizer.read();
		} catch (LLJException e) {
			// OK, must throw exception
			return;
		}
		
		fail("Invalid int did not fail");
	}
	
	public void testInvalidFloat() {
		try {
			Tokenizer tokenizer = new Tokenizer("12345678901000.1111");
			tokenizer.read();
		} catch (LLJException e) {
			// OK, must throw exception
			return;
		}
		
		fail("Invalid float did not fail");
	}
	
	public void testInvalidFloat2() {
		try {
			Tokenizer tokenizer = new Tokenizer("0001.1111");
			tokenizer.read();
		} catch (LLJException e) {
			// OK, must throw exception
			return;
		}
		
		fail("Invalid float did not fail");
	}
	
	public void testCommas() throws LLJException {
		Tokenizer tokenizer = new Tokenizer("a,b");
		
		Token t = tokenizer.read();
		assertEquals("a", t.data);
		
		t = tokenizer.read();
		assertEquals(",", t.data);
		
		t = tokenizer.read();
		assertEquals("b", t.data);
	}*/
	
	private Token singleToken(String code) {
		Tokenizer tokenizer = new Tokenizer(new BufferedReader(new StringReader(code)));
		Token token = tokenizer.read();
		assertTrue(tokenizer.isEndOfInput());
		
		return token;
	}
}

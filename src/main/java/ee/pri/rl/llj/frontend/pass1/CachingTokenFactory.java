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

import java.util.HashMap;
import java.util.Map;

import ee.pri.rl.llj.common.operators.Operator;

// FIXME comment, flyweight usage
public class CachingTokenFactory implements TokenFactory {
	private static final Map<String, Token> OPERATORS;
	
	private static final Token TOKEN_LPAREN = new Token(TokenType.LPAREN, "(");
	private static final Token TOKEN_RPAREN = new Token(TokenType.RPAREN, ")");
	private static final Token TOKEN_DOT = new Token(TokenType.DOT, ".");
	
	private static final Token TOKEN_LIST_START = new Token(TokenType.LIST_START, "[");
	private static final Token TOKEN_LIST_END = new Token(TokenType.LIST_END, "]");
	private static final Token TOKEN_LIST_TAIL = new Token(TokenType.LIST_TAIL, "[]");
	private static final Token TOKEN_LIST_CONS = new Token(TokenType.LIST_CONS, "|");
	
	private static final Token TOKEN_COMMENT = new Token(TokenType.COMMENT, "");
	
	static {
		OPERATORS = new HashMap<String, Token>();
		for (Operator operator : Operator.values()) {
			OPERATORS.put(operator.getName(), new Token(TokenType.OPERATOR, operator.getName()));
		}
	}
	
	private static Map<String, Token> variables = new HashMap<String, Token>();

	@Override
	public Token makeToken(TokenType type, String data, int lineNumber) {
		switch (type) {
		case COMMENT:
			return TOKEN_COMMENT;

		case DOT:
			return TOKEN_DOT;
			
		case FLOAT:
			return new Token(TokenType.FLOAT, data);
			
		case INTEGER:
			return new Token(TokenType.INTEGER, data);
			
		case LIST_CONS:
			return TOKEN_LIST_CONS;
			
		case LIST_END:
			return TOKEN_LIST_END;
			
		case LIST_START:
			return TOKEN_LIST_START;
			
		case LIST_TAIL:
			return TOKEN_LIST_TAIL;
			
		case LPAREN:
			return TOKEN_LPAREN;
			
		case RPAREN:
			return TOKEN_RPAREN;
			
		case OPERATOR:
			return OPERATORS.get(data);
			
		case STRING:
			return new Token(TokenType.STRING, data);
			
		case VARIABLE:
			if (!variables.containsKey(data)) {
				variables.put(data, new Token(TokenType.VARIABLE, data));
			}
			return variables.get(data);
			
		default:
			throw new RuntimeException("Unhandled token type: " + type);
		}
	}

}

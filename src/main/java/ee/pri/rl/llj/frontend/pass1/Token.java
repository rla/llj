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

import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.operators.Operator;

/**
 * Class for LLJ tokens. Tokens are integers, strings, parentheses etc.
 * 
 * @see TokenType
 * 
 * @author Raivo Laanemets
 */
public final class Token {
	/**
	 * Type of the token.
	 * @see TokenType
	 */
	public final TokenType type;
	
	/**
	 * String representation of the token.
	 */
	public final String data;
	
	public final int lineNumber;
	
	/**
	 * Constructor for the new token.
	 * 
	 * @param type Type of the token.
	 * @param data String data for this token.
	 * @param lineNumber Position of the token.
	 */
	public Token(TokenType type, String data, int lineNumber) {
		this.type = type;
		this.data = data;
		this.lineNumber = lineNumber;
	}
	
	public Token(TokenType type, String data) {
		this(type, data, LLJException.UNKNOWN_LINE);
	}
	
	/**
	 * Returns whether the given token represents the given operator.
	 */
	public boolean represents(Operator operator) {
		return TokenType.OPERATOR == type && operator.getName().equals(data);
	}

	@Override
	public String toString() {
		return data;
	}

}

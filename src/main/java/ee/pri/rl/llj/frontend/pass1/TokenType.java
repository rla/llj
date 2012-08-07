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

/**
 * Enum of LLJ tokens.
 * 
 * @author Raivo Laanemets
 */
public enum TokenType {
	COMMENT,    // /*...*/, '%...\n
	INTEGER,    // (1...9 | (0...9)*) | 0
	FLOAT,      // INTEGER . (0...9)+
	STRING,
	LPAREN,     // (
	RPAREN,     // )
	DOT,        // .
	OPERATOR,   // one of the operators (including :- and ,)
	LIST_START, // [
	LIST_END,   // ]
	LIST_TAIL,  // []
	LIST_CONS,  // |
	VARIABLE
}

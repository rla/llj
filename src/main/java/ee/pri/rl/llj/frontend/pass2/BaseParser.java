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

import java.util.Iterator;

import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.LLJRuntimeException;
import ee.pri.rl.llj.common.transit.TransformingIterator;
import ee.pri.rl.llj.frontend.pass1.Token;
import ee.pri.rl.llj.frontend.pass1.TokenType;

/**
 * Base class for LLJ parser. Contains primitive functionality.
 * The implementation of primitive functionality is similar to
 * BaseTokenizer.
 * 
 * @author Raivo Laanemets
 */
abstract class BaseParser extends TransformingIterator<Token, ParsedTerm> {
	
	private Token current = null;
	private boolean hasBeenRead = false;
	private boolean end = false;
	private Token next = null;
	private int lineNumber = 0;
	
	protected BaseParser(Iterator<Token> tokenizer) {
		super(tokenizer);
	}
	
	protected Token current() {
		if (end) {
			throw new RuntimeException("At the end");
		} else {
			if (!hasBeenRead) {
				current = getInput().next();
				hasBeenRead = true;
			}
			
			return current;
		}
	}
	
	protected void advance() {
		if (next != null) {
			current = next;
			next = null;
		} else {
			if (!getInput().hasNext()) {
				end = true;
			} else {
				current = getInput().next();
				lineNumber = current.lineNumber;
			}
		}
	}
	
	/**
	 * Returns lookahead token.
	 */
	protected Token lookAhead() {
		if (next == null) {
			next = getInput().next();
		}
		
		return next;
	}
	
	protected boolean canLookAhead() {
		if (next == null) {
			if (!getInput().hasNext()) {
				return false;
			} else {
				next = getInput().next();
				return true;
			}
		} else {
			return true;
		}
	}
	
	/**
	 * Checks whether the end of input has been reached.
	 */
	protected boolean isEnd() {
		return end;
	}
	
	/**
	 * Checks if the end of input has reached. If it is then throws exception
	 * with the explaining message.
	 */
	protected void assumeNoEnd(String message) throws LLJException {
		if (isEnd()) {
			throw error(message);
		}
	}
	
	protected void skipComments() {
		while (!isEnd() && current().type == TokenType.COMMENT) {
			advance();
		}
	}
	
	/**
	 * Throws ParserException containing the given message. Uses information
	 * about the line number if possible.
	 */
	protected LLJRuntimeException error(String message) {
		return new LLJRuntimeException(message, lineNumber);
	}
	
	/**
	 * Throws ParserException containing the given message. Uses information
	 * about the line number if possible.
	 */
	protected LLJRuntimeException error(String message, Throwable cause) {
		return new LLJRuntimeException(message, cause, lineNumber);
	}
	
	protected LLJRuntimeException errorUnexpectedToken() {
		return error("unexpected token: " + current());
	}
	
	public boolean isEndOfInput()  {
		skipComments();
		return isEnd();
	}
	
	protected int getLineNumber() {
		return lineNumber;
	}
}

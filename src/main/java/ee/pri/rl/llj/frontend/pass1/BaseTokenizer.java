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
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import ee.pri.rl.llj.common.LLJRuntimeException;
import ee.pri.rl.llj.common.NonRemovableItemIterator;
import ee.pri.rl.llj.common.operators.Operator;

/**
 * Base class for LLJ tokenizer. Contains primitive functionality.
 * 
 * @author Raivo Laanemets
 */
abstract class BaseTokenizer extends NonRemovableItemIterator<Token> {
	private Reader reader;
	
	private int lineNumber;
	private char current = (char) -1;
	private boolean hasBeenRead = false;
	private boolean end = false;
	
	private final int maxOpLength;
	private final char[] opBuf;
	
	/**
	 * Constructs the new tokenizer using input source code.
	 * 
	 * @param reader Reader for the source code.
	 * @param maxOpLength Maximum length of operator.
	 */
	protected BaseTokenizer(Reader reader, int maxOpLength) {
		this.lineNumber = 1;
		this.reader = assureBuffered(reader);
		this.maxOpLength = maxOpLength;
		this.opBuf = new char[maxOpLength + 1];
	}
	
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Advances the current position by one.
	 */
	protected void advance() {
		if (current() == '\n') {
			lineNumber++;
		}

		int r = read();
		if (r == -1) {
			end = true;
			return;
		}
		current = (char) r;
	}

	/**
	 * Returns true if there is at least one character after the current character.
	 */
	protected boolean canLookAhead() {
		try {
			reader.mark(1);
			int r = reader.read();
			reader.reset();
			
			return r != -1;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the character at the current position. Does not check
	 * for the end condition.
	 */
	protected char current() {
		if (end) {
			throw new RuntimeException("At the end");
		}
		if (!hasBeenRead) {
			int r = read();
			if (r == -1) {
				throw new RuntimeException("At the end");
			}
			current = (char) r;
			hasBeenRead = true;
		}
		return current;
	}
	
	private int read() {
		try {
			return reader.read();
		} catch (IOException e) {
			throw new RuntimeException("Error", e);
		}
	}

	/**
	 * Returns exception for throwing when some error occurs.
	 * 
	 * @param message Message explaining the error..
	 * @return Exception for throwing.
	 */
	protected LLJRuntimeException error(String message) {
		return new LLJRuntimeException(message, lineNumber);
	}

	/**
	 * Returns exception for throwing when some error occurs.
	 * 
	 * @param message Message explaining the error.
	 * @param pos Custom position for calculating the line number.
	 * @return Exception for throwing.
	 */
	protected LLJRuntimeException error(String message, int pos) {
		return new LLJRuntimeException(message, lineNumber);
	}
	
	/**
	 * Checks whether the given character is a digit (0..9).
	 */
	protected boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}
	
	/**
	 * Checks whether the end of input has been reached.
	 */
	protected boolean isEnd() {		
		return end;
	}
	
	/**
	 * Returns the character that comes after the current character. Does not check
	 * whether such character exists.
	 */
	protected char lookAhead() {
		try {
			reader.mark(1);
			int r = reader.read();
			if (r == -1) {
				throw new RuntimeException("At end");
			}
			reader.reset();
			
			return (char) r;
		} catch (IOException e) {
			throw new RuntimeException("Error", e);
		}
	}
	
	protected int matchesOp(List<Operator> ops) {
		int read = 1;
		
		opBuf[0] = current;
		try {
			reader.mark(maxOpLength);
			while (read < maxOpLength) {
				int r = reader.read(opBuf, read, maxOpLength - read);
				if (r == -1) {
					break;
				}
				read += r;
			}
			reader.reset();
		} catch (Exception e) {
			throw new RuntimeException("Error", e);
		}
		
		int start = hasBeenRead ? 0 : 1;
		
		outer: for (Operator op : ops) {
			String name = op.name;
			for (int i = start; i - start < name.length(); i++) {
				if (opBuf[i] != name.charAt(i - start)) {
					continue outer;
				}
			}
			
			return name.length();
		}
		
		return -1;
	}
	
	/**
	 * Skips all consequent whitespace characters starting from
	 * the current position.
	 */
	protected void skipWhiteSpace() {
		while (!isEnd() && Character.isWhitespace(current())) {
			advance();
		}
	}
	
	/**
	 * Checks whether the input has reached the end or some more
	 * tokens can be read.
	 */
	public boolean isEndOfInput() {
		skipWhiteSpace();
		return isEnd();
	}
	
	/**
	 * Helper method to buffer the input reader if necessary.
	 * Reader that is already BufferedReader or StringReader,
	 * is not changed otherwise it's wrapped inside the
	 * BufferedReader.
	 */
	private static Reader assureBuffered(Reader reader) {
		if (reader instanceof BufferedReader) {
			return reader;
		} else if (reader instanceof StringReader) {
			return reader;
		} else {
			return new BufferedReader(reader);
		}
	}
	
}

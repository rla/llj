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

import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.LLJRuntimeException;
import ee.pri.rl.llj.common.operators.Operator;
import ee.pri.rl.llj.common.operators.OperatorLengthComparator;

/**
 * LLJ tokenizer.
 * 
 * @see Token
 * @see TokenType
 * 
 * @author Raivo Laanemets
 */
public final class Tokenizer extends BaseTokenizer implements Iterator<Token> {
	/**
	 * List of operators, sorted by the string length of the operator.
	 */
	private static final List<Operator> OPERATORS;
	
	private static final int MAX_OP_LENGTH;
	
	static {
		// Constructs list of operators sorted by their string length
		OPERATORS = new LinkedList<Operator>(Arrays.asList(Operator.values()));
		Collections.sort(OPERATORS, new OperatorLengthComparator());
		
		int maxLength = 0;
		for (Operator op : OPERATORS) {
			if (op.name.length() > maxLength) {
				maxLength = op.name.length();
			}
		}
		
		MAX_OP_LENGTH = maxLength;
	}
	
	private TokenFactory tokenFactory;

	public Tokenizer(Reader reader) {
		this(reader, new CachingTokenFactory());
	}

	public Tokenizer(Reader reader, TokenFactory tokenFactory) {
		super(reader, MAX_OP_LENGTH);
		this.tokenFactory = tokenFactory;
	}

	/**
	 * Reads integer token. This can be an integer or a part of negative integer
	 * or a part of float number.
	 * 
	 * @return String of consequentive digit characters (0..9).
	 * 
	 * FIXME 'E' hack here
	 */
	private String readInteger() {
		StringBuilder builder = new StringBuilder();
		// FIXME !!! + and - blows for 1+2
		while (!isEnd() && (isDigit(current()) || current() == 'E'
			|| current() == 'e' || current() == '+'
			|| current() == '-')) {
			
			builder.append(current());
			advance();
		}

		return builder.toString().replace("+", "").replace("-", "");
	}

	/**
	 * Reads next token from the input source.
	 * 
	 * @return LLJ token.
	 * @throws LLJException When the source code is invalid.
	 */
	public Token read() {
		skipWhiteSpace();
		
		if (isEnd()) {
			throw error("End of input");
		}
		
		int opLength = -1;

		// Reads integer or float token.
		final char current = current();
		if (current == '/') {
			if (canLookAhead() && lookAhead() == '*') {
				return readBlockComment();
			} else {
				advance();
				return makeToken(TokenType.OPERATOR, "/");
			}
		} else if (current == '%') {
			advance();
			StringBuilder builder = new StringBuilder();
			while (!isEnd() && current() != '\n' && current() != '\r') {
				builder.append(current());
				advance();
			}

			return makeToken(TokenType.COMMENT, builder.toString());
		} else if (current == '"') {
			return readQuotedString();
		} else if (current == '\'') {
			return readSingleQuotedString();
		} else if (current == '(') {
			advance();
			return makeToken(TokenType.LPAREN, "(");
		} else if (current == ')') {
			advance();
			return makeToken(TokenType.RPAREN, ")");
		} else if (current == '.') {
			advance();
			return makeToken(TokenType.DOT, ".");
		} else if (current == ']') {
			advance();
			return makeToken(TokenType.LIST_END, "]");
		} else if (current == '[') {
			advance();
			if (!isEnd() && current() == ']') {
				advance();
				return makeToken(TokenType.LIST_TAIL, "[]");
			} else {
				return makeToken(TokenType.LIST_START, "[");
			}
		} else if (current == '|') {
			advance();
			return makeToken(TokenType.LIST_CONS, "|");
		} else if (current == '_' || Character.isUpperCase(current())) {
			return readVariable();
		} else if (current == '!') {
			advance();
			return makeToken(TokenType.STRING, "!");
		} else if (Character.isLowerCase(current) || current == '@') {
			return readString();
		} else if (isDigit(current)) {
			return readIntegerOrFloat();
		} else if ((opLength = matchesOp(OPERATORS)) > 0) {
			StringBuilder builder = new StringBuilder();
			while (opLength > 0) {
				builder.append(current());
				advance();
				opLength--;
			}
			return makeToken(TokenType.OPERATOR, builder.toString());
		} else {
			throw error("Unexpected input: " + current);
		}
	}

	/**
	 * Reads LLJ variable name. Variable name can start with underscore ('_') or lowercase
	 * letter. Variable name can contain underscore ('_') or letter.
	 * 
	 * @return Token representing LLJ variable.
	 */
	private Token readVariable() {
		
		StringBuilder builder = new StringBuilder();
		while (!isEnd() && (current() == '_' || Character.isLetter(current()) || isDigit(current()))) {
			builder.append(current());
			advance();
		}
		
		return makeToken(TokenType.VARIABLE, builder.toString());
	}

	/**
	 * Reads quoted string. Same as readString() but starts and ends with double
	 * quote ('"') and can contain valid escape sequences.
	 * 
	 * @return Token representing LLJ string.
	 * @throws LLJException When the string is unterminated or contains
	 * 	invalid escape sequences.
	 */
	private Token readQuotedString() {
		advance(); // "
		
		StringBuilder builder = new StringBuilder();
		while (true) {
			if (isEnd()) {
				throw errorUnterminatedString(getLineNumber());
			} else if (current() == '\\') {
				if (!canLookAhead()) {
					errorUnterminatedString(getLineNumber());
				} else {
					advance(); // \
					builder.append(resolveEscapedSymbol());
					advance();
				}
			} else if (current() == '"') {
				advance(); // "
				return makeToken(TokenType.STRING, builder.toString());
			} else {
				builder.append(current());
				advance();
			}
		}
	}
	
	private Token readSingleQuotedString() {
		advance(); // "
		
		StringBuilder builder = new StringBuilder();
		while (true) {
			if (isEnd()) {
				throw errorUnterminatedString(getLineNumber());
			} else if (current() == '\\') {
				if (!canLookAhead()) {
					errorUnterminatedString(getLineNumber());
				} else {
					advance(); // \
					builder.append(resolveEscapedSymbol());
					advance();
				}
			} else if (current() == '\'') {
				advance(); // "
				return makeToken(TokenType.STRING, builder.toString());
			} else {
				builder.append(current());
				advance();
			}
		}
	}
	
	/**
	 * Helper procedure to construct suitable Exception for
	 * unterminated string.
	 * 
	 * @param pos Start of the string.
	 */
	private LLJRuntimeException errorUnterminatedString(int pos) {
		return error("Unterminated string literal", pos);
	}
	
	/**
	 * Reads escaped symbol inside quoted string.
	 * 
	 * @return Character symbol representing quoted sequence.
	 * @throws LLJException When the escape symbol is invalid.
	 */
	private char resolveEscapedSymbol() {
		char ch = current();
		if (ch == '"') {
			return '"';
		} else if (ch == 'n') {
			return '\n';
		} else if (ch == 'r') {
			return '\r';
		} else if (ch == 't') {
			return '\t';
		} else if (ch == '\\') {
			return '\\';
		} else if (ch == '\'') { // FIXME in both quote styles?
			return '\'';
		} else {
			throw error("Unknown escaped character: " + ch);
		}
	}

	/**
	 * Reads LLJ string. String can contain everything for which
	 * <code>Character.isLetterOrDigit</code> returns true or
	 * underscore ('_') in the middle or at the end.
	 * 
	 * @return Token representing LLJ string.
	 */
	private Token readString() {
		StringBuilder builder = new StringBuilder();

		while (!isEnd() && (Character.isLetterOrDigit(current()) || current() == '_' || current() == '@')) {
			builder.append(current());
			advance();
		}

		return makeToken(TokenType.STRING, builder.toString());
	}

	/**
	 * Reads LLJ block comment.
	 * 
	 * @return Token representing LLJ block comment.
	 * @throws TokenizerException When the comment is unterminated.
	 */
	private Token readBlockComment() {
		advance(); // /
		advance(); // *
		
		StringBuilder builder = new StringBuilder();

		while (true) {
			if (current() != '*') {
				builder.append(current());
			}
			if (isEnd()) {
				throw error("Unterminated block comment");
			} else if (current() == '*') {
				if (canLookAhead() && lookAhead() == '/') {
					advance(); // *
					advance(); // /
					return makeToken(TokenType.COMMENT, builder.toString());
				} else {
					advance();
				}
			} else {
				advance();
			}
		}
	}

	/**
	 * Reads LLJ integer or float.
	 * 
	 * @return Token representing LLJ integer or float.
	 * @throws TokenizerException When the float or integer is invalid.
	 */
	private Token readIntegerOrFloat() {
		String integer = readInteger();
		
		// FIXME hack for float 6e21-typed floats
		if (integer.contains("e")) {
			validateFloat(integer);
			return new Token(TokenType.FLOAT, integer, getLineNumber());
		}
		
		validateInteger(integer);

		if (isEnd()) {
			return makeToken(TokenType.INTEGER, integer);
		} else if (current() == '.') {
			if (canLookAhead()) {
				if (isDigit(lookAhead())) {
					advance();
					String floatPart = readInteger();
					String floatNumber = integer + "." + floatPart;
					validateFloat(floatNumber);
					return makeToken(TokenType.FLOAT, floatNumber);
				} else {
					return makeToken(TokenType.INTEGER, integer);
				}
			} else {
				return makeToken(TokenType.INTEGER, integer);
			}
		} else {
			return makeToken(TokenType.INTEGER, integer);
		}
	}
	
	/**
	 * Validates the given float number.
	 * 
	 * @throws TokenizerException When the integer is invalid.
	 */
	private void validateFloat(String floatNumber) {
		try {
			Float.valueOf(floatNumber);
		} catch (NumberFormatException e) {
			throw error("invalid number", getLineNumber());
		}
	}

	/**
	 * Validates the given integer.
	 * 
	 * @throws TokenizerException When the integer is invalid.
	 */
	private void validateInteger(String integer) {
		if (integer.length() > 1 && integer.charAt(0) == '0') {
			throw error("invalid number " + integer, getLineNumber());
		}
	}
	
	/**
	 * Helper method for creating a new token.
	 * 
	 * @param type Type of the token.
	 * @param data String data of the token.
	 */
	private Token makeToken(TokenType type, String data) {
		return tokenFactory.makeToken(type, data, getLineNumber());
	}

	@Override
	public boolean hasNext() {
		return !isEndOfInput();
	}

	@Override
	public Token next() {
		return read();
	}

}

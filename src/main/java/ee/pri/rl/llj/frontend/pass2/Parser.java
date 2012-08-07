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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ee.pri.rl.llj.common.LLJRuntimeException;
import ee.pri.rl.llj.common.operators.Associativity;
import ee.pri.rl.llj.common.operators.Operator;
import ee.pri.rl.llj.common.term.FloatAtom;
import ee.pri.rl.llj.common.term.ListStruct;
import ee.pri.rl.llj.common.term.Struct;
import ee.pri.rl.llj.common.term.Term;
import ee.pri.rl.llj.common.term.Variable;
import ee.pri.rl.llj.frontend.pass1.Token;
import ee.pri.rl.llj.frontend.pass1.TokenType;

/**
 * LLJ parser. For syntax similar to Prolog.
 * 
 * Notes: Unary operators bind stronger than binary operators.
 * 
 * @author Raivo Laanemets
 */
public final class Parser extends BaseParser {
	/**
	 * Precalculated set of unary operator names.
	 */
	private static final Set<String> UNARY_OPERATORS;
	
	static {
		UNARY_OPERATORS = new HashSet<String>();
		for (Operator op : Operator.values()) {
			if (op.isUnary()) {
				UNARY_OPERATORS.add(op.getName());
			}
		}
	}
	
	private TermFactory termFactory;
	
	/**
	 * Constructs new parser using the given tokenizer.
	 * 
	 * @param tokenizer The tokenizer that supplies the tokens.
	 */
	public Parser(Iterator<Token> tokenizer) {
		this(tokenizer, new SimpleTermFactory());
	}

	/**
	 * Constructs new parser using the given tokenizer.
	 */
	public Parser(Iterator<Token> tokenizer, TermFactory termFactory) {
		super(tokenizer);
		this.termFactory = termFactory;
	}
	
	public ParsedTerm parse() {
		skipComments();
		
		if (isEnd()) {
			throw error("End of input");
		}
		
		ParsedTerm term = new ParsedTerm(parseSingleTerm(), getLineNumber());
		if (current().type == TokenType.DOT) {
			advance();
		} else {
			throw error("Expecting .");
		}
		
		return term;
		
	}

	public Term parseSingleTerm() {
		return parseBinaryOpChain(ParserBinaryOpLevel.CHAIN, true);
	}

	// Assumes that struct parts 'name', '(' exist.
	private Struct parseStruct() {
		String name = current().data;

		advance(); // 'name'
		advance(); // '('

		List<Term> arguments = new LinkedList<Term>();
		while (true) {
			arguments.add(parseBinaryOpChain(ParserBinaryOpLevel.CHAIN, false));
			if (current().represents(Operator.COMMA)) {
				advance(); // ','
			} else if (TokenType.RPAREN == current().type) {
				advance(); // ')'
				return new Struct(name, arguments);
			} else {
				throw errorUnexpectedToken();
			}
		}
	}

	private Term parseTail() {
		if (current().type == TokenType.LIST_END) {
			advance();
			return termFactory.createStringAtom("[]");
		} else if (current().type == TokenType.LIST_CONS) {
			advance();
			if (current().type == TokenType.LIST_TAIL) {
				advance();
				if (current().type == TokenType.LIST_END) {
					advance();
					return termFactory.createStringAtom("[]");
				} else {
					throw errorUnexpectedToken();
				}
			} else if (current().type == TokenType.LIST_START) {
				advance();
				Term head = parseBinaryOpChain(ParserBinaryOpLevel.CHAIN, false);
				ListStruct list = new ListStruct(head, parseTail());
				if (current().type == TokenType.LIST_END) {
					advance();
				} else {
					throw errorUnexpectedToken();
				}
				return list;
			} else if (current().type == TokenType.VARIABLE) {
				String name = current().data;
				advance();
				if (current().type == TokenType.LIST_END) {
					advance();
					return new Variable(name);
				} else {
					errorUnexpectedToken();
					return null;
				}
			} else if (current().type == TokenType.STRING) {
				// FIXME allows improper lists
				String atom = current().data;
				advance();
				if (current().type == TokenType.LIST_END) {
					advance();
				} else {
					throw errorUnexpectedToken();
				}
				return termFactory.createStringAtom(atom);
			} else {
				throw errorUnexpectedToken();
			}
		} else if (current().represents(Operator.COMMA)) {
			advance(); // ','
			Term head = parseBinaryOpChain(ParserBinaryOpLevel.CHAIN, false);
			return new ListStruct(head, parseTail());
		} else {
			throw errorUnexpectedToken();
		}
	}

	private Term parseBinaryOpChain(ParserBinaryOpLevel chain, boolean commaIsOp) {
		if (chain == null) {
			return parseTerm();
		} else {
			List<Term> terms = new LinkedList<Term>();
			List<String> operators = new LinkedList<String>();

			terms.add(parseBinaryOpChain(chain.tail, commaIsOp));
			int count = 1;
			while (!isEnd()) {
				String token = current().data;
				if (Operator.COMMA.getName().equals(token) && !commaIsOp) {
					break;
				}
				if (chain.hasToken(token)) {
					if (count > 2 && chain.associativity == Associativity.NONE) {
						throw error("Operators " + operators + " are non-associative");
					}
					operators.add(token);
					advance();
					terms.add(parseBinaryOpChain(chain.tail, commaIsOp));
				} else {
					break;
				}
				count++;
			}
			
			if (chain.associativity == Associativity.LEFT) {
				Term term = terms.get(0);
				for (int i = 1; i < terms.size(); i++) {
					term = new Struct(operators.get(i - 1), term, terms.get(i));
				}

				return term;
			} else if (chain.associativity == Associativity.RIGHT) {
				Term term = terms.get(terms.size() - 1);
				for (int i = terms.size() - 2; i >= 0; i--) {
					term = new Struct(operators.get(i), terms.get(i), term);
				}

				return term;
			} else {
				if (terms.size() > 1) {
					return new Struct(operators.get(0), terms.get(0), terms.get(1));
				} else {
					return terms.get(0);
				}
			}
		}
	}

	private Term parseTerm() {
		skipComments();
		if (current().type == TokenType.STRING) {
			if (canLookAhead() && lookAhead().type == TokenType.LPAREN) {
				return parseStruct();
			} else {
				String data = current().data;
				advance();
				return termFactory.createStringAtom(data);
			}
		} else if ("-".equals(current().data)
			&& canLookAhead() && lookAhead().type == TokenType.INTEGER) {
			advance();
			Long integer = -Long.valueOf(current().data);
			advance();
			return termFactory.createLongAtom(integer);
		} else if ("-".equals(current().data)
			&& canLookAhead() && lookAhead().type == TokenType.FLOAT) {
			advance();
			Float value = Float.valueOf(current().data);
			advance();
			return new FloatAtom(-value);
		} else if (UNARY_OPERATORS.contains(current().data)) {
			String op = current().data;
			advance();
			return new Struct(op, parseTerm());
		} else if (current().type == TokenType.INTEGER) {
			long value = 0L;
			try {
				value = Long.valueOf(current().data);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Invalid number, using 0 as default!");
			}
			advance();
			return termFactory.createLongAtom(value);
		} else if (current().type == TokenType.FLOAT) {
			// FIXME this check into tokenizer
			try {
				float value = Float.valueOf(current().data);
				advance();
				return new FloatAtom(value);
			} catch (NumberFormatException e) {
				throw new LLJRuntimeException("Invalid number " + current().data, getLineNumber());
			}
		} else if (current().type == TokenType.VARIABLE) {
			String name = current().data;
			advance();
			return new Variable(name);
		} else if (current().type == TokenType.LIST_TAIL) {
			advance();
			return termFactory.createStringAtom("[]");
		} else if (current().type == TokenType.LIST_START) {
			advance();
			Term head = parseBinaryOpChain(ParserBinaryOpLevel.CHAIN, false);
			return termFactory.processListStruct(new ListStruct(head, parseTail()));
		} else if (current().type == TokenType.LPAREN) {
			advance();
			Term term = parseBinaryOpChain(ParserBinaryOpLevel.CHAIN, true);
			if (current().type == TokenType.RPAREN) {
				advance();
				return term;
			} else {
				throw errorUnexpectedToken();
			}
		} else {
			throw errorUnexpectedToken();
		}
	}

	@Override
	public boolean hasNext() {
		return !isEnd();
	}

	@Override
	public ParsedTerm next() {
		return parse();
	}
	
}

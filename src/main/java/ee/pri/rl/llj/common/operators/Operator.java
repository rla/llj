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
package ee.pri.rl.llj.common.operators;

import ee.pri.rl.llj.common.term.Functor;

public enum Operator {
	UNARY_COLONMINUS(":-", true),
	UNARY_MINUS("-", true),
	MULT("*", 0, Associativity.LEFT),
	DIV("/", 0, Associativity.LEFT),
	MOD("mod", 0, Associativity.LEFT),
	INT_DIV("div", 0, Associativity.LEFT),
	PLUS("+", 1, Associativity.LEFT),
	MINUS("-", 1, Associativity.LEFT),
	COLON(":", 2, Associativity.RIGHT),
	GREATER_EQUAL(">=", 3, Associativity.NONE),
	LESS_EQUAL("=<", 3, Associativity.NONE),
	GREATER(">", 3, Associativity.NONE),
	LESS("<", 3, Associativity.NONE),
	NOT_EQUAL("=\\=", 3, Associativity.NONE),
	EQUAL_ARITH("=:=", 3, Associativity.NONE),
	UNIFIABLE("=", 3, Associativity.NONE),
	GREATER_EQUAL_TERM("@>=", 3, Associativity.NONE),
	LESS_EQUAL_TERM("@=<", 3, Associativity.NONE),
	GREATER_TERM("@>", 3, Associativity.NONE),
	LESS_TERM("@<", 3, Associativity.NONE),
	NOT_UNIFIABLE("\\=", 3, Associativity.NONE),
	IS("is", 3, Associativity.NONE),
	IF_THEN("->", 4, Associativity.RIGHT),
	COMMA(",", 5, Associativity.RIGHT),
	SEMICOLON(";", 6, Associativity.RIGHT),
	COLONMINUS(":-", 7, Associativity.NONE);
	
	// FIXME make all public finaö
	public final String name;
	private boolean unary;
	private int priority;
	private Associativity associativity;
	
	private Operator(String name, int priority, Associativity associativity) {
		this(name, false);
		this.priority = priority;
		this.associativity = associativity;
	}
	
	private Operator(String name, boolean unary) {
		this.name = name;
		this.unary = unary;
		this.priority = 0;
		this.associativity = Associativity.NONE;
	}

	public String getName() {
		return name;
	}

	public boolean isUnary() {
		return unary;
	}

	public int getPriority() {
		return priority;
	}
	
	public Associativity getAssociativity() {
		return associativity;
	}
	
	public static boolean isArithmeticsComparision(String op) {
		return GREATER_EQUAL.getName().equals(op)
			|| LESS_EQUAL.getName().equals(op)
			|| GREATER.getName().equals(op)
			|| LESS.getName().equals(op)
			|| EQUAL_ARITH.getName().equals(op)
			|| NOT_EQUAL.getName().equals(op);
	}
	
	public static boolean isTermComparision(String op) {
		return GREATER_EQUAL_TERM.getName().equals(op)
			|| LESS_EQUAL_TERM.getName().equals(op)
			|| GREATER_TERM.getName().equals(op)
			|| LESS_TERM.getName().equals(op)
			|| NOT_UNIFIABLE.getName().equals(op);
	}

	public static boolean isUnary(String op) {
		for (Operator value : values()) {
			if (value.name.equals(op)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Functor getFunctor() {
		return new Functor(name, unary ? 1 : 2);
	}
	
}

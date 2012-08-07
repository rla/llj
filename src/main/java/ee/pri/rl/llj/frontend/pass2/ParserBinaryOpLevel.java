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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ee.pri.rl.llj.common.operators.Associativity;
import ee.pri.rl.llj.common.operators.Operator;
import ee.pri.rl.llj.common.operators.OperatorPriorityComparator;

/**
 * Grouping of operators by their priority and associativity.
 * 
 * @author Raivo Laanemets
 */
final class ParserBinaryOpLevel {
	final static ParserBinaryOpLevel CHAIN;
	
	static {
		List<Operator> binOps = new ArrayList<Operator>();
		for (Operator op : Operator.values()) {
			if (!op.isUnary()) {
				binOps.add(op);
			}
		}
		
		Collections.sort(binOps, new OperatorPriorityComparator());
		
		ParserBinaryOpLevel tail = null;
		int lastPriority = Integer.MIN_VALUE;
		List<String> lastOps = null;
		Associativity lastAssociativity = null;
		
		for (Operator op : binOps) {
			if (op.getPriority() != lastPriority) {
				if (lastOps != null) {
					// Verify that all operators have same associativity
					tail = new ParserBinaryOpLevel(tail, lastAssociativity, lastOps);
				}
				lastOps = new ArrayList<String>();
				lastPriority = op.getPriority();
				lastAssociativity = op.getAssociativity();
			}
			if (lastAssociativity != op.getAssociativity()) {
				throw new Error("Operator " + op + " has different associativity than " + lastOps);
			}
			lastOps.add(op.getName());
		}
		
		if (lastOps != null) {
			tail = new ParserBinaryOpLevel(tail, lastAssociativity, lastOps);
		}
		
		CHAIN = tail;
	}
	
	private final Set<String> tokens;
	public final Associativity associativity;
	public final ParserBinaryOpLevel tail;

	private ParserBinaryOpLevel(ParserBinaryOpLevel tail, Associativity associativity, List<String> tokens) {
		this.tokens = new HashSet<String>(tokens);
		this.tail = tail;
		this.associativity = associativity;
	}

	boolean hasToken(String token) {
		return tokens.contains(token);
	}

}
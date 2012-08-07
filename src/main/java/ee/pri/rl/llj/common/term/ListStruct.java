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
package ee.pri.rl.llj.common.term;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ee.pri.rl.llj.common.IntGenerator;
import ee.pri.rl.llj.common.term.visitor.TermVisitor;

/**
 * Represents head-tail list structure.
 */
public final class ListStruct implements Term {
	private static final long serialVersionUID = 1L;
	
	public final Term head;
	public final Term tail;

	public ListStruct(Term head, Term tail) {
		this.head = head;
		this.tail = tail;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		Term term = this;
		
		builder.append('[');
		boolean first = true;
		while (term instanceof ListStruct) {
			ListStruct cur = (ListStruct) term;
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}
			builder.append(cur.head);
			term = cur.tail;
		}
		
		if (term instanceof Variable) {
			builder.append('|').append(term);
		}
		
		builder.append(']');
		
		return builder.toString();
	}
	
	public List<Term> toTermList() {
		List<Term> terms = new LinkedList<Term>();
		Term term = this;
		while (term instanceof ListStruct) {
			ListStruct cur = (ListStruct) term;
			terms.add(cur.head);
			term = cur.tail;
		}
		
		terms.add(term);
		
		return terms;
	}

	@Override
	public int hashCode() {
		return head.hashCode() ^ tail.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ListStruct other = (ListStruct) obj;
		if (!head.equals(other.head)) {
			return false;
		}
		if (!tail.equals(other.tail)) {
			return false;
		}
		
		return true;
	}

	@Override
	public void visitWith(TermVisitor visitor) {
		visitor.visitListStruct(this);
	}

	@Override
	public boolean isGround() {
		return head.isGround() && tail.isGround();
	}

	@Override
	public Set<Variable> getVariables() {
		Set<Variable> variables = head.getVariables();
		variables.addAll(tail.getVariables());
		
		return variables;
	}
	
	@Override
	public Set<Variable> getNamedVariables() {
		Set<Variable> variables = head.getNamedVariables();
		variables.addAll(tail.getNamedVariables());
		
		return variables;
	}

	@Override
	public Term replaceAnonVariables(IntGenerator intGenerator) {
		return new ListStruct(head.replaceAnonVariables(intGenerator), tail.replaceAnonVariables(intGenerator));
	}

}

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

import java.util.HashSet;
import java.util.Set;

import ee.pri.rl.llj.common.IntGenerator;
import ee.pri.rl.llj.common.term.visitor.TermVisitor;

/**
 * Represents a variable.
 */
public final class Variable implements Term, Single {
	private static final long serialVersionUID = 1L;
	
	public final String name;

	public Variable(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == Variable.class && ((Variable) obj).name.equals(name);
	}
	
	/**
	 * Checks whether the current variable is anonymous variable.
	 * A variable is anonymous when its name starts with '_' (underscore).
	 */
	public boolean isAnonymous() {
		return name.startsWith("_");
	}

	@Override
	public void visitWith(TermVisitor visitor) {
		visitor.visitVariable(this);
	}

	@Override
	public boolean isGround() {
		return false;
	}

	@Override
	public Set<Variable> getVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		variables.add(this);
		
		return variables;
	}

	@Override
	public Set<Variable> getNamedVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		variables.add(this);
		
		return variables;
	}

	@Override
	public Term replaceAnonVariables(IntGenerator intGenerator) {
		if (isAnonymous()) {
			return new Variable("_" + intGenerator.getAndIncrement());
		} else {
			return this;
		}
	}

}

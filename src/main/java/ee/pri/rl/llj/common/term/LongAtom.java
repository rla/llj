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
 * Represents signed integer number.
 */
public class LongAtom implements Term, Single {
	private static final long serialVersionUID = 1L;
	
	public final Long value;

	public LongAtom(Long value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == LongAtom.class && ((LongAtom) obj).value.equals(value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}



	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public void visitWith(TermVisitor visitor) {
		visitor.visitIntAtom(this);
	}

	@Override
	public boolean isGround() {
		return true;
	}

	@Override
	public Set<Variable> getVariables() {
		return new HashSet<Variable>();
	}
	
	@Override
	public Set<Variable> getNamedVariables() {
		return new HashSet<Variable>();
	}

	@Override
	public Term replaceAnonVariables(IntGenerator intGenerator) {
		return this;
	}

}

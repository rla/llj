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

import java.io.Serializable;
import java.util.Set;

import ee.pri.rl.llj.common.IntGenerator;
import ee.pri.rl.llj.common.term.visitor.TermVisitor;

/**
 * Base interface of program elements.
 */
public interface Term extends Serializable {
	
	/**
	 * Visit the term with given visitor.
	 */
	void visitWith(TermVisitor visitor);
	
	/**
	 * Returns true if the term is ground.
	 */
	boolean isGround();
	
	/**
	 * Returns variables from the term.
	 */
	Set<Variable> getVariables();
	
	/**
	 * Returns non-anonymous (names) variables.
	 */
	Set<Variable> getNamedVariables();
	
	/**
	 * Replaces anonymous variables with names like <code>_n</code>.
	 */
	Term replaceAnonVariables(IntGenerator intGenerator);
	
}

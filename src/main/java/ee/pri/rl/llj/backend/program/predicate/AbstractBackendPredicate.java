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
package ee.pri.rl.llj.backend.program.predicate;

import java.io.Serializable;

import ee.pri.rl.llj.backend.program.BackendDefinition;
import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.program.index.PredicateIndex;
import ee.pri.rl.llj.common.term.Functor;

/**
 * Base class for backend predicates.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 */
public abstract class AbstractBackendPredicate implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Flag indicating whether this predicate is module-
	 * transparent or not. Module-transparent predicate
	 * does not change the module context within the predicate
	 * call is made from. This corresponds lightly to
	 * Swi-Prolog's module_transparent.
	 */
	public final boolean moduleTransparent;
	
	/**
	 * The module that defines this predicate.
	 */
	public final BackendModule context;
	
	/**
	 * Functor of this predicate.
	 */
	public final Functor functor;
	
	/**
	 * An index associated with this predicate. This is
	 * initially null.
	 */
	public PredicateIndex index = null;
	
	public AbstractBackendPredicate(
			boolean moduleTransparent,
			BackendModule context,
			Functor functor) {
		
		this.moduleTransparent = moduleTransparent;
		this.context = context;
		this.functor = functor;
	}
	
	/**
	 * Returns how many definitions the current predicate has.
	 */
	public abstract int getNumberOfDefinitions();
	
	/**
	 * Returns the i-th definition of this predicate.
	 */
	public abstract BackendDefinition getDefinition(int i);
	
	/**
	 * Returns the largest required environment size.
	 */
	public abstract int getLargestEnvironment();
	
}

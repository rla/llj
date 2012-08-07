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
package ee.pri.rl.llj.backend.program;

import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.term.Functor;
import ee.pri.rl.llj.common.term.Struct;

/**
 * Helper class for handling 'module_transparent' directive.
 * 
 * @author Raivo Laanemets
 */
public final class ModuleTransparent {
	private final Functor functor;

	public ModuleTransparent(Struct call) throws LLJException {
		if (Functor.MODULE_TRANSPARENT.equals(call.functor())) {
			functor = Functor.fromTerm(call.first());
		} else {
			throw new LLJException("Invalid module_transparent declaration");
		}
	}

	/**
	 * Returns the functor representing predicate for which this 'module_transparent'
	 * declaration applies.
	 */
	public Functor getFunctor() {
		return functor;
	}
	
}

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
package ee.pri.rl.llj.backend.term;

import ee.pri.rl.llj.backend.VariableMap;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;

/**
 * Represents single backend variable. The backend variable
 * contains integer identifier to enable lookup in arrays.
 * 
 * @author Raivo Laanemets
 */
public final class BackendVariable extends BackendTerm {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The identifier of the variable.
	 */
	public final int id;
	
	/**
	 * Creates instance of new {@link BackendVariable}.
	 * 
	 * @param id The identifier of the variable.
	 */
	public BackendVariable(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the instance of {@link RuntimeVariable} from the
	 * given environment.
	 */
	@Override
	public Object getRuntimeObject(Object[] env) {
		return env[id];
	}

	@Override
	public Object getGround() {
		return null;
	}

	@Override
	public String toString() {
		return "V" + id;
	}

	@Override
	public BackendTerm expandCalls(VariableMap map) {
		return this;
	}

}

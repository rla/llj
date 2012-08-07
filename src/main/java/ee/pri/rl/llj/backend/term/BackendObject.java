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


public final class BackendObject extends BackendTerm {
	private static final long serialVersionUID = 1L;
	
	public final Object value;

	public BackendObject(Object value) {
		this.value = value;
	}

	@Override
	public Object getRuntimeObject(Object[] env) {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Object getGround() {
		return value;
	}

	@Override
	public BackendTerm expandCalls(VariableMap map) {
		return this;
	}
}

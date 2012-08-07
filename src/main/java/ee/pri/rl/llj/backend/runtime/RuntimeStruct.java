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
package ee.pri.rl.llj.backend.runtime;

import java.io.Serializable;
import java.util.Arrays;

import ee.pri.rl.llj.common.term.Functor;

/**
 * Runtime representation of compound structure.
 */
public final class RuntimeStruct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final Object[] data;
	
	public RuntimeStruct(Object... data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(data[0]).append('(');
		for (int i = 1; i < data.length; i++) {
			builder.append(data[i]);
			if (i < data.length - 1) {
				builder.append(',');
			}
		}
		builder.append(')');
		
		return builder.toString();
	}
	
	/**
	 * Returns functor of this runtime struct.
	 */
	public Functor getFunctor() {
		return new Functor((String) data[0], data.length - 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuntimeStruct other = (RuntimeStruct) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		return true;
	}
	
}

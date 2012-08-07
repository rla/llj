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
import java.util.IdentityHashMap;


/**
 * A class that represents runtime LLJ variable.
 */
public final class RuntimeVariable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constant for empty environment that contains no
	 * variables.
	 */
	public static final RuntimeVariable[] EMPTY_ENVIRONMENT = {};

	public Object reference;
	
	/**
	 * Creates new runtime variable. Sets it to point to itself.
	 */
	public RuntimeVariable() {
		this.reference = this;
	}
	
	@Override
	public String toString() {
		if (reference == this) {
			return "?" + super.hashCode();
		} else {
			return reference == null ? "null" : reference.toString();
		}
	}
	
	/**
	 * Static method for dereferencing given term (might be Java object).
	 */
	public static Object deref(Object o) {
		while (o.getClass() == RuntimeVariable.class) {
			RuntimeVariable v = ((RuntimeVariable) o);
			o = v.reference;
			if (o == v) {
				return v;
			}
		}
		
		return o;
	}
	
	public static Object copyTerm(Object o) {
		return renameVariables(derefRecursive(o));
	}
	
	/**
	 * Static method for dereferencing given term recursively (might be Java object).
	 */
	private static Object derefRecursive(Object o) {
		o = deref(o);
		if (o.getClass() == RuntimeVariable.class) {
			return o;
		} else if (o instanceof RuntimeListStruct) {
			RuntimeListStruct list = (RuntimeListStruct) o;
			return new RuntimeListStruct(derefRecursive(list.head), derefRecursive(list.tail));
		} else if (o instanceof RuntimeStruct) {
			RuntimeStruct struct = (RuntimeStruct) o;
			Object[] data = new Object[struct.data.length];
			data[0] = struct.data[0];
			for (int i = 1; i < data.length; i++) {
				data[i] = derefRecursive(struct.data[i]);
			}
			
			return new RuntimeStruct(data);
		} else {
			return o;
		}
	}
	
	/**
	 * Renames the variables inside the runtime term.
	 */
	private static Object renameVariables(Object o) {
		IdentityHashMap<RuntimeVariable, RuntimeVariable> identityMap = new IdentityHashMap<RuntimeVariable, RuntimeVariable>();
		return renameVariables(o, identityMap);
	}
	
	private static Object renameVariables(Object o, IdentityHashMap<RuntimeVariable, RuntimeVariable> newVariables) {
		if (o instanceof RuntimeVariable) {
			RuntimeVariable variable = (RuntimeVariable) o;
			if (newVariables.containsKey(variable)) {
				return newVariables.get(variable);
			} else {
				RuntimeVariable newVariable = new RuntimeVariable();
				newVariables.put(variable, newVariable);
				return newVariable;
			}
		} else if (o instanceof RuntimeListStruct) {
			RuntimeListStruct list = (RuntimeListStruct) o;
			return new RuntimeListStruct(renameVariables(list.head, newVariables), renameVariables(list.tail, newVariables));
		} else if (o instanceof RuntimeStruct) {
			RuntimeStruct struct = (RuntimeStruct) o;
			Object[] data = new Object[struct.data.length];
			data[0] = struct.data[0];
			for (int i = 1; i < data.length; i++) {
				data[i] = renameVariables(struct.data[i], newVariables);
			}
			
			return new RuntimeStruct(data);
		} else {
			return o;
		}
	}
}

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
import ee.pri.rl.llj.backend.runtime.RuntimeListStruct;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;
import ee.pri.rl.llj.common.term.ListStruct;

public final class BackendListStruct extends BackendTerm {
	private static final long serialVersionUID = 1L;
	
	private final BackendTerm head;
	private final BackendTerm tail;
	private final Object ground;
	
	public BackendListStruct(BackendTerm head, BackendTerm tail) {
		this.head = head;
		this.tail = tail;
		ground = null;
	}
	
	public BackendListStruct(ListStruct list, VariableMap variableMap) {
		head = createTerm(list.head, variableMap);
		tail = createTerm(list.tail, variableMap);
		
		if (list.isGround()) {
			ground = getRuntimeObject(new RuntimeVariable[0]);
		} else {
			ground = null;
		}
	}

	@Override
	public Object getRuntimeObject(Object[] env) {
		if (ground == null) {
			return new RuntimeListStruct(head.getRuntimeObject(env), tail.getRuntimeObject(env));
		} else {
			return ground;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		Object o = this;
		
		builder.append('[');
		boolean first = true;
		while (o instanceof BackendListStruct) {
			BackendListStruct cur = (BackendListStruct) o;
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}
			builder.append(cur.head);
			o = cur.tail;
		}
		
		if (o instanceof BackendVariable) {
			builder.append('|').append(o);
		} else if (o instanceof BackendObject) {
			builder.append(',').append(o);
		}
		
		builder.append(']');
		
		return builder.toString();
	}

	@Override
	public Object getGround() {
		return ground;
	}

	@Override
	public BackendTerm expandCalls(VariableMap map) {
		return this;
	}

}

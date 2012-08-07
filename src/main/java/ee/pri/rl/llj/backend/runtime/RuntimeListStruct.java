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
import java.util.ArrayList;
import java.util.List;


public final class RuntimeListStruct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final Object EMPTY_LIST = "[]";

	public final Object head;
	public final Object tail;
	
	public RuntimeListStruct(Object head, Object tail) {
		this.head = head;
		this.tail = tail;
	}
	
	public List<Object> toList() {
		List<Object> list = new ArrayList<Object>();
		Object cur = this;
		while (cur.getClass() == RuntimeListStruct.class) {
			RuntimeListStruct listStruct = (RuntimeListStruct) cur;
			list.add(RuntimeVariable.deref(listStruct.head));
			cur = RuntimeVariable.deref(listStruct.tail);
		}
		
		return list;
	}
	
	public Object[] toArray() {
		List<Object> list = toList();
		
		return list.toArray();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		Object o = this;
		
		builder.append('[');
		boolean first = true;
		while (o instanceof RuntimeListStruct) {
			RuntimeListStruct cur = (RuntimeListStruct) o;
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}
			builder.append(cur.head);
			o = RuntimeVariable.deref(cur.tail);
		}
		
		if (o instanceof RuntimeVariable) {
			builder.append('|').append(o);
		} else if (!EMPTY_LIST.equals(o)) {
			builder.append(',').append(o);
		}
		
		builder.append(']');
		
		return builder.toString();
	}
	
	public static Object fromArray(Object[] array) {
		Object tail = EMPTY_LIST;
		for (int i = array.length - 1; i >= 0; i--) {
			tail = new RuntimeListStruct(array[i], tail);
		}
		
		return tail;
	}
	
	// FIXME slow for some lists
	public static Object fromList(List<?> list) {
		Object tail = EMPTY_LIST;
		for (int i = list.size() - 1; i >= 0; i--) {
			tail = new RuntimeListStruct(list.get(i), tail);
		}
		
		return tail;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + ((tail == null) ? 0 : tail.hashCode());
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
		RuntimeListStruct other = (RuntimeListStruct) obj;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		if (tail == null) {
			if (other.tail != null)
				return false;
		} else if (!tail.equals(other.tail))
			return false;
		return true;
	}

}

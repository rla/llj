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
package ee.pri.rl.llj.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StringMap implements Map<String, Integer> {
	private Map<Integer, String> int2string = new HashMap<Integer, String>();
	private Map<String, Integer> string2int = new HashMap<String, Integer>();
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return string2int.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Integer>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer get(Object key) {
		return string2int.get(key);
	}

	@Override
	public boolean isEmpty() {
		return string2int.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer put(String key, Integer value) {
		int2string.put(value, key);
		return string2int.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Integer> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return string2int.size();
	}

	@Override
	public Collection<Integer> values() {
		throw new UnsupportedOperationException();
	}
	
	public String getStrin(Integer value) {
		return int2string.get(value);
	}
	
}
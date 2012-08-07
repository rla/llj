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
package ee.pri.rl.llj.backend;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import ee.pri.rl.llj.common.term.Variable;

/**
 * Helper class to associate each variable with an integer id.
 * 
 * @author Raivo Laanemets
 */
public final class VariableMap implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Map<Variable, Integer> map;
	private final Map<Variable, Integer> anonymousMap;
	private int id = 0;
	
	public VariableMap() {
		map = new HashMap<Variable, Integer>();
		anonymousMap = new IdentityHashMap<Variable, Integer>();
	}
	
	/**
	 * Creates new variable map using collection of input variables.
	 * Identificators are associated in the order as the variables
	 * appear in the collection.
	 */
	public VariableMap(Collection<Variable> variables) {
		this();
		for (Variable variable : variables) {
			if (!map.containsKey(variable) && !variable.isAnonymous()) {
				map.put(variable, id++);
			}
		}
	}
	
	/**
	 * Returns an integer id for the given variable.
	 */
	public int getId(Variable variable) {
		if (variable.isAnonymous()) {
			if (anonymousMap.containsKey(variable)) {
				return anonymousMap.get(variable);
			} else {
				anonymousMap.put(variable, id++);
				return anonymousMap.get(variable);
			}
		}
		if (!map.containsKey(variable)) {
			map.put(variable, id++);
		}
		
		return map.get(variable);
	}
	
	/**
	 * Generates fresh id for the variable.
	 */
	public int generateId() {
		return id++;
	}
	
	/**
	 * Returns number of distinct variables in the map.
	 */
	public int getNumberOfVariables() {
		return id;
	}
	
	public Map<String, Integer> getNonAnonMap() {
		Map<String, Integer> ret = new HashMap<String, Integer>();
		for (Entry<Variable, Integer> entry : map.entrySet()) {
			ret.put(entry.getKey().name, entry.getValue());
		}
		
		return ret;
	}
}

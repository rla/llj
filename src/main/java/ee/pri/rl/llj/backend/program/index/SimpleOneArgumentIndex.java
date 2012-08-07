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
package ee.pri.rl.llj.backend.program.index;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import ee.pri.rl.llj.backend.program.BackendDefinition;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;
import ee.pri.rl.llj.backend.runtime.goal.PredicateGoal;

/**
 * Predicate index that indexes by single argument.
 * Supports non-determinism.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 */
public class SimpleOneArgumentIndex implements PredicateIndex {
	private Map<Object, List<BackendDefinition>> indexData;
	private int argumentNumber;

	public SimpleOneArgumentIndex(
			Map<Object, List<BackendDefinition>> indexData,
			int argumentNumber) {
		
		this.indexData = indexData;
		this.argumentNumber = argumentNumber;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BackendDefinition> applyIndex(PredicateGoal goal) {
		
		Object key = goal.getArgument(argumentNumber);
		
		if (key.getClass() == RuntimeVariable.class) {
			return null;
		} else {
			List<BackendDefinition> definitions = indexData.get(key);
			if (definitions == null) {
				return Collections.EMPTY_LIST;
			} else {
				return definitions;
			}
		}
	}

}

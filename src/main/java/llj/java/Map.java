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
package llj.java;

import java.util.HashMap;

import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.JavaCallGoal;
import ee.pri.rl.llj.common.java.JavaModule;
import ee.pri.rl.llj.common.java.Predicate;

public class Map implements JavaModule {
	
	@Predicate(name="map_new", arity = 1)
	public AbstractGoal newMap(JavaCallGoal goal) throws Exception {
		goal.setVar(0, new HashMap<Object, Object>());
		
		return goal.G;
	}
	
	@SuppressWarnings("unchecked")
	@Predicate(name="map_set", arity = 3)
	public AbstractGoal putMap(JavaCallGoal goal) throws Exception {
		((java.util.Map<Object, Object>) goal.getArg(0)).put(goal.getArg(1), goal.getArg(2));
		
		return goal.G;
	}
	
	@SuppressWarnings("unchecked")
	@Predicate(name="map_get", arity = 3)
	public AbstractGoal getMap(JavaCallGoal goal) throws Exception {
		Object a3 = goal.getArg(2);
		Object o = ((java.util.Map<Object, Object>) goal.getArg(0)).get(goal.getArg(1));
		
		if (goal.Q.unify(a3, o)) {
			return goal.G;
		} else {
			return goal.Q.backtrack();
		}
		
	}
}

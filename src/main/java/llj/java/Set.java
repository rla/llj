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

import java.util.ArrayList;
import java.util.HashSet;

import ee.pri.rl.llj.backend.runtime.RuntimeListStruct;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.JavaCallGoal;
import ee.pri.rl.llj.common.java.JavaModule;
import ee.pri.rl.llj.common.java.Predicate;

public class Set implements JavaModule {
	
	@Predicate(name="set_new", arity = 1)
	public AbstractGoal newSet1(JavaCallGoal goal) throws Exception {
		goal.setVar(0, new HashSet<Object>());
		
		return goal.G;
	}
	
	@SuppressWarnings("unchecked")
	@Predicate(name="set_add", arity = 2)
	public AbstractGoal putMap(JavaCallGoal goal) throws Exception {
		((java.util.Set<Object>) goal.getArg(0)).add(goal.getArg(1));
		
		return goal.G;
	}
	
	@SuppressWarnings("unchecked")
	@Predicate(name="set_to_prolog", arity = 2)
	public AbstractGoal listToProlog(JavaCallGoal goal) throws Exception {
		java.util.Set<Object> set = ((java.util.Set<Object>) goal.getArg(0));
		
		Object tail = RuntimeListStruct.fromList(new ArrayList<Object>(set));
		
		if (!goal.Q.unify(goal.getArg(1), tail)) {
			return goal.Q.backtrack();
		}
		
		return goal.G;
	}
	
	@Predicate(name="prolog_to_set", arity = 2)
	public AbstractGoal prologListToSet2(JavaCallGoal goal) throws Exception {
		java.util.Set<Object> set = new HashSet<Object>();
		set.addAll(goal.getListArg(0));
		goal.setVar(1, set);
		
		return goal.G;
	}
}

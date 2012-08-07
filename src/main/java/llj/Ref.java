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
package llj;

import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.JavaCallGoal;
import ee.pri.rl.llj.common.java.JavaModule;
import ee.pri.rl.llj.common.java.Predicate;

public class Ref implements JavaModule {
	
	@Predicate(name="ref", arity = 2)
	public AbstractGoal mutable(JavaCallGoal goal) throws Exception {
		goal.setVar(0, new MutableVariable(goal.getArg(1)));
		
		return goal.G;
	}
	
	@Predicate(name="ref_get", arity = 2)
	public AbstractGoal refGet(JavaCallGoal goal) throws Exception {
		goal.setVar(1, ((MutableVariable) goal.getArg(0)).reference);
		
		return goal.G;
	}
	
	@Predicate(name="ref_set", arity = 2)
	public AbstractGoal refSet(JavaCallGoal goal) throws Exception {
		((MutableVariable) goal.getArg(0)).reference = goal.getArg(1);
		
		return goal.G;
	}
	
	private static class MutableVariable {
		public MutableVariable(Object reference) {
			this.reference = reference;
		}

		public Object reference;
	}
}

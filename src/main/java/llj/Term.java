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

import java.lang.String;

import ee.pri.rl.llj.backend.runtime.RuntimeStruct;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.JavaCallGoal;
import ee.pri.rl.llj.common.java.JavaModule;
import ee.pri.rl.llj.common.java.Predicate;

/**
 * LLJ term manipulation predicates.
 * 
 * @author Raivo Laanemets
 */
public class Term implements JavaModule {
	
	@Predicate(name="arg", arity = 3)
	public AbstractGoal arg(JavaCallGoal goal) throws Exception {
		// FIXME check overflow???
		int arg = (int) goal.getLongArg(0);
		RuntimeStruct term = goal.getStructArg(1);
		
		return goal.unify(2, term.data[arg]);
	}
	
	@Predicate(name="copy_term", arity = 2)
	public AbstractGoal copyTerm(JavaCallGoal goal) throws Exception {
		Object left = RuntimeVariable.copyTerm(goal.getArg(0));
		if (!goal.Q.unify(left, goal.getArg(1))) {
			return goal.Q.backtrack();
		}
		
		return goal.G;
	}
	
	@Predicate(name="functor", arity = 3)
	public AbstractGoal functor(JavaCallGoal goal) throws Exception {
		if (goal.getArg(0).getClass() == RuntimeVariable.class) {
			return functor_vii(goal);
		} else {
			return functor_ioo(goal);
		}
	}

	@Predicate(name="functor_ioo", arity = 3)
	public AbstractGoal functor_ioo(JavaCallGoal goal) throws Exception {
		RuntimeStruct struct = goal.getStructArg(0);
		Integer arity = struct.data.length - 1;
		String functor = (String) struct.data[0];
		
		if (goal.Q.unify(goal.getArg(1), functor)
				&& goal.Q.unify(goal.getArg(2), arity)) {
			return goal.G;
		} else {
			return goal.Q.backtrack();
		}
	}
	
	/**
	 * Assumes that the first argument is certanly variable.
	 */
	@Predicate(name="functor_vii", arity = 3)
	public AbstractGoal functor_vii(JavaCallGoal goal) throws Exception {
		// FIXME check overflow?
		Integer arity = (int) goal.getLongArg(2);
		String functor = (String) goal.getStringArg(1);
		
		Object[] data = new Object[arity + 1];
		for (int i = 0; i < arity; i++) {
			data[i + 1] = new RuntimeVariable();
		}
		data[0] = functor;
		
		goal.setVar(0, new RuntimeStruct(data));
		
		return goal.G;
	}
	
	@Predicate(name="var", arity = 1)
	public AbstractGoal var(JavaCallGoal goal) throws Exception {
		Object a1 = goal.getArg(0);
		if (a1.getClass() == RuntimeVariable.class) {
			return goal.G;
		} else {
			return goal.Q.backtrack();
		}
	}
}

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
package ee.pri.rl.llj.backend.runtime.goal;

import java.util.Collections;
import java.util.List;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.program.call.BackendJavaMethodCall;
import ee.pri.rl.llj.backend.runtime.RuntimeListStruct;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.RuntimeStruct;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;
import ee.pri.rl.llj.common.LLJException;

public final class JavaCallGoal extends AbstractGoal {
	private static final long serialVersionUID = 1L;
	
	public final BackendJavaMethodCall C;
	public final BackendModule M;
	public final AbstractGoal G; 
	public final Object[] P;
	public final RuntimeQuery Q;
	
	/**
	 * Aux pointer for goals that need to store
	 * state between backtracking calls. This is not automatically
	 * cleaned on recall.
	 */
	public Object aux = null;
	
	public JavaCallGoal(
			AbstractGoal G,
			Object[] P,
			BackendJavaMethodCall C,
			BackendModule M,
			RuntimeQuery Q) {
		
		this.C = C;
		this.M = M;
		this.G = G;
		this.P = P;
		this.Q = Q;
	}

	@Override
	public final AbstractGoal run(final RuntimeQuery Q) throws Exception {
		return C.run(this);
	}
	
	public Object getArg(int i) {
		return RuntimeVariable.deref(P[C.A[i]]);
	}
	
	/**
	 * Returns the object of the given class.
	 * 
	 * @throws LLJException When the i-th argument is not from the given class.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(int i, Class<T> clazz) throws LLJException {
		Object o = getArg(i);
		if (clazz.isInstance(o)) {
			return (T) o;
		} else {
			throw new LLJException("Predicate " + C.functor + " expects " + clazz.getName() + " as argumen " + i);
		}
	}
	
	public RuntimeStruct getStructArg(int i) throws LLJException {
		Object arg = getArg(i);
		if (arg instanceof RuntimeStruct) {
			return (RuntimeStruct) arg;
		} else {
			throw new LLJException("Predicate " + C.functor + " expects compound as argument " + i);
		}
	}
	
	public List<Object> getListArg(int i) throws LLJException {
		Object arg = getArg(i);
		
		if (arg instanceof RuntimeListStruct) {
			return ((RuntimeListStruct) arg).toList();
		}
		
		if (RuntimeListStruct.EMPTY_LIST.equals(arg)) {
			return Collections.emptyList();
		}
		
		throw new LLJException("Predicate " + C.functor + " expects list as argument " + i);
	}
	
	public int getIntArg(int i) throws LLJException {
		Object arg = getArg(i);
		if (arg instanceof Integer) {
			return (Integer) arg;
		} else {
			throw new LLJException("Predicate " + C.functor + " expects integer as argument " + i);
		}
	}
	
	public long getLongArg(int i) throws LLJException {
		Object arg = getArg(i);
		if (arg instanceof Long) {
			return (Long) arg;
		} else {
			throw new LLJException("Predicate " + C.functor + " expects long as argument " + i);
		}
	}
	
	public String getStringArg(int i) throws LLJException {
		Object arg = getArg(i);
		if (arg instanceof String) {
			return (String) arg;
		} else {
			throw new LLJException("Predicate " + C.functor + " expects string as argument " + i);
		}
	}
	
	public RuntimeVariable getVar(int i) {
		Object arg = getArg(i);
		if (arg.getClass() != RuntimeVariable.class) {
			throw new RuntimeException("Arg " + i + " is not variable");
		}
		
		return (RuntimeVariable) arg;
	}
	
	public void setVar(int i, Object value) {
		RuntimeVariable var = getVar(i);
		var.reference = value;
		
		Q.trail(var);
	}
	
	/**
	 * Tries to unify the given value with the i-th argument of the goal.
	 * 
	 * @return The next goal to be executed.
	 */
	public AbstractGoal unify(int i, Object value) throws Exception {
		if (Q.unify(getArg(i), value)) {
			return G;
		} else {
			return Q.backtrack();
		}
	}

	@Override
	public AbstractGoal reentry(final RuntimeQuery Q) throws Exception {
		return C.run(this);
	}
	
}

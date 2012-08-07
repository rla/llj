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
package ee.pri.rl.llj.backend.program.call;

import java.lang.reflect.Method;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.NoChoiceException;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.JavaCallGoal;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.java.JavaModule;
import ee.pri.rl.llj.common.term.Functor;

public final class BackendJavaMethodCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	private final Method method;
	private final JavaModule javaModule;
	public final Functor functor;
	
	public BackendJavaMethodCall(
			Functor functor,
			BackendModule fromModule,
			int[] A,
			Method method,
			JavaModule javaModule) {
		
		super(fromModule, A);
		this.method = method;
		this.javaModule = javaModule;
		this.functor = functor;
	}

	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M) {
		return new JavaCallGoal(G, P, this, M, Q);
	}
	
	public AbstractGoal run(JavaCallGoal goal) throws LLJException {
		try {
			return (AbstractGoal) method.invoke(javaModule, goal);
		} catch (Exception e) {
			if (e.getCause().getClass() == NoChoiceException.class) {
				throw (NoChoiceException) e.getCause();
			}
			throw new LLJException("Cannot execute Java method", e);
		}
	}
}

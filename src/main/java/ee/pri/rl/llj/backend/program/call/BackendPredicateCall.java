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

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.program.predicate.StaticBackendPredicate;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.PredicateGoal;

public final class BackendPredicateCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	public final StaticBackendPredicate predicate;

	public BackendPredicateCall(StaticBackendPredicate predicate, int[] A, BackendModule M) {
		super(M, A);
		this.predicate = predicate;
	}

	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M) {
		return new PredicateGoal(G, this, P, predicate.moduleTransparent ? M : predicate.context);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(predicate.functor.name);
		if (predicate.functor.arity > 0) {
			builder.append('(');
			for (int i = 0; i < predicate.functor.arity; i++) {
				if (i > 0) {
					builder.append(", ");
				}
				builder.append(A[i]);
			}
			builder.append(')');
		}
		
		return builder.toString();
	}

}

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

import ee.pri.rl.llj.backend.program.BackendDefinition;
import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.program.call.BackendPredicateCall;
import ee.pri.rl.llj.backend.runtime.NoChoiceException;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;

public final class PredicateGoal extends AbstractGoal {
	private static final long serialVersionUID = 1L;
	
	private final Object[] P;
	private final AbstractGoal G;
	private final BackendModule M;
	private final BackendPredicateCall C;
	
	/**
	 * Local environment for this goal. First n elements of the
	 * environment are the arguments.
	 */
	private Object[] L;
	
	public PredicateGoal(AbstractGoal G, BackendPredicateCall C, Object[] P, BackendModule M) {
		this.P = P;
		this.G = G;
		this.M = M;
		this.C = C;
	}
	
	/**
	 * Returns n-th argument (used for indexing).
	 */
	public Object getArgument(int n) {
		return RuntimeVariable.deref(L[n]);
	}

	@Override
	public AbstractGoal run(final RuntimeQuery Q) throws Exception {
		
		// If necessary, creates new local environment.
		
		if (L == null) {
			L = new Object[C.predicate.largestEnvironment];
			int[] A = C.A;
			for (int i = 0; i < A.length; i++) {
				L[i] = P[A[i]];
			}
			for (int i = A.length; i < C.predicate.largestEnvironment; i++) {
				L[i] = new RuntimeVariable();
			}
		}
		
		// FIXME create indexedpredicate
		/*
		PredicateIndex index = predicate.index;
		
		if (index != null) {
			List<BackendDefinition> definitions = index.applyIndex(this);
			if (definitions != null) {
				
				if (log.isDebugEnabled()) {
					log.debug("Using index for " + predicate.functor);
				}
				
				if (definitions.isEmpty()) {
					return query.backtrack();
				}
				
				if (definitions.size() == 1) {
					return runDefinition(definitions.get(0));
				}
				
				AbstractBackendPredicate subsetFromIndex = new DynamicBackendPredicate(
					predicate.moduleTransparent,
					fromModule,
					predicate.functor,
					definitions,
					predicate.getLargestEnvironment()
				);
				
				return new BackendPredicateCall(
					subsetFromIndex,
					call.argumentTable,
					fromModule
				).makeGoal(argumentEnvironment, query, next, fromModule);
			}
		}*/
		
		int numberOfDefinitions = C.predicate.definitions.length;
		
		// No choice point will be inserted if we are
		// executing the last definition.
		
		if (n < numberOfDefinitions - 1) {
			Q.markChoice(this);
		}
		
		// Try to unify definition head and then expand it.
		// Otherwise backtrack.
		
		return unifyTryExpand(C.predicate.definitions[n], Q);
	}

	@Override
	public AbstractGoal reentry(final RuntimeQuery Q) throws Exception {
		
		int numberOfDefinitions = C.predicate.definitions.length;
		
		// No choice point will be inserted if we are
		// executing the last definition.
		
		if (n < numberOfDefinitions - 1) {
			Q.markChoice(this);
		}
		
		// Try to unify definition head and then expand it.
		// Otherwise backtrack.
		
		return unifyTryExpand(C.predicate.definitions[n], Q);
	}
	
	private final AbstractGoal unifyTryExpand(BackendDefinition definition, final RuntimeQuery Q) throws NoChoiceException {
		
		// Runs head unifications associated with the definition.
		// If these unifications fail, then backtrack.
		
		if (!definition.headUnify(L, Q)) {
			return Q.backtrack();
		}
		
		return definition.call == null ? G : definition.call.makeGoal(L, Q, G, M);
	}
}

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

import ee.pri.rl.llj.backend.runtime.RuntimeQuery;

/**
 * Goal for running unification(<code>=/2</code>).
 */
public final class UnificationGoal extends AbstractGoal {
	private static final long serialVersionUID = 1L;
	
	private final Object[] P;
	private final int[] A;
	private final AbstractGoal G;

	/**
	 * Constructs new unification goal.
	 * 
	 * @param G the next goal (continuation)
	 * @param A arguments (index to parent)
	 * @param P the parent environment
	 */
	public UnificationGoal(AbstractGoal G, int[] A, Object[] P) {
		this.P = P;
		this.A = A;
		this.G = G;
	}

	@Override
	public AbstractGoal run(final RuntimeQuery Q) throws Exception {
		if (!Q.unify(P[A[0]], P[A[1]])) {
			return Q.backtrack();
		}
		
		return G;
	}
	
	@Override
	public AbstractGoal reentry(final RuntimeQuery Q) throws Exception {
		throw new RuntimeException(getClass() + " does not support re-entry");
	}

}

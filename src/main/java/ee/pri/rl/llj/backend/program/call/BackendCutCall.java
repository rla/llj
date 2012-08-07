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
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.CutGoal;

/**
 * Backend representation of cut(!) call.
 */
public final class BackendCutCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	/**
	 * A static representation of cut call. Cut calls from
	 * different call sites do not differ in any way. Cut also
	 * does not make use of module information (which for this general
	 * instance is <code>null</code>).
	 */
	public static final BackendCutCall CALL = new BackendCutCall(null);

	private BackendCutCall(BackendModule M) {
		super(M);
	}

	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule fromModule) {
		return new CutGoal(G, Q.lastGoalIndex());
	}

}

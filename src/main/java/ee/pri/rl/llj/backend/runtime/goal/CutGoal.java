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
 * Goal for running cut(!) operator.
 * 
 * @author Raivo Laanemets
 */
public final class CutGoal extends AbstractGoal {
	private static final long serialVersionUID = 1L;
	
	private final int s;
	private final AbstractGoal G;

	public CutGoal(AbstractGoal G, int lastGoalIndex) {
		// At the creation of this goal we memorize the stack position
		// of the last goal that was ran before cut and had choice points.
		this.s = lastGoalIndex;
		this.G = G;
	}

	@Override
	public AbstractGoal run(final RuntimeQuery Q) throws Exception {
		if (s != -1) {
			Q.removeChoice(s);
		}
		
		return G;
	}

	@Override
	public AbstractGoal reentry(final RuntimeQuery Q) throws Exception {
		throw new RuntimeException(getClass() + " does not support re-entry");
	}

}

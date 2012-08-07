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

import java.io.Serializable;

import ee.pri.rl.llj.backend.runtime.RuntimeQuery;

/**
 * Base class for LLJ goals.
 */
public abstract class AbstractGoal implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Flag for marking tha goal has been reached by backtracking.
	 * The flag is automatically cleared and should not be used for
	 * detecting if current call is backtrack call or fresh one.
	 */
	public boolean backtracked = false;
	
	/**
	 * Trail position when this goal was marked for choice.
	 */
	public int trailPosition = 0;
	
	/**
	 * Marks n-th time of backtracked call. n = 0 - never backtraced,
	 * n = 1 - backtracked once.
	 */
	public int n = 0;
	
	/**
	 * Runs the goal. Must return next goal that is ran.
	 * 
	 * @param Q the current runtime query
	 */
	public abstract AbstractGoal run(final RuntimeQuery Q) throws Exception;
	
	/**
	 * The goal is run again by backtracking.
	 * 
	 * @param Q the current runtime query
	 */
	public abstract AbstractGoal reentry(final RuntimeQuery Q) throws Exception;
}

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

import java.io.Serializable;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;

/**
 * Base class for backend calls.
 * 
 * @author Raivo Laanemets
 */
public abstract class BackendAbstractCall implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Module from which the call is made from.
	 */
	public final BackendModule M;
	
	/**
	 * Call arguments. This is mapping into the parent
	 * environment.
	 */
	public final int[] A;
	
	/**
	 * Constructor for calls without local environment.
	 * 
	 * @param M module of the call site
	 * @param A arguments (mapping into parent environment)
	 */
	public BackendAbstractCall(BackendModule M, int[] A) {
		this.M = M;
		this.A = A;
	}
	
	/**
	 * Constructor for calls without local environment and arguments.
	 * This is used by pseudocalls or calls which themselves have no effects.
	 * 
	 * @param M module of the call site
	 */
	public BackendAbstractCall(BackendModule M) {
		this.M = M;
		this.A = null;
	}

	/**
	 * Method for creating a new goal for this call. Called at runtime when
	 * the definition is expanded into subcalls.
	 * 
	 * @param P the parent environment (caller's local)
	 * @param Q the query from within the call is made
	 * @param G the next goal to be executed (continuation)
	 * @param M the module from which the call is made
	 */
	public abstract AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M);

}

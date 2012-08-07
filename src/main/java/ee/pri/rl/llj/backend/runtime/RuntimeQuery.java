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
package ee.pri.rl.llj.backend.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ee.pri.rl.llj.backend.VariableMap;
import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.program.call.BackendAbstractCall;
import ee.pri.rl.llj.backend.program.call.BackendCallFactory;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.term.BackendTerm;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.term.Term;

/**
 * Main class for running queries against LLJ database.
 */
public class RuntimeQuery extends RuntimeAbstractQuery {
	private final Map<String, RuntimeVariable> bindings;
	private final AbstractGoal goal;
	private final LLJContext context;
	
	private long callCount = 0;
	
	public RuntimeQuery(
			Term query,
			LLJContext context,
			int trailSize,
			int choiceSize,
			int unifyStackSize) throws LLJException {
		
		super(trailSize, choiceSize, unifyStackSize);
		
		this.context = context;
		this.bindings = new HashMap<String, RuntimeVariable>();
		
		VariableMap map = new VariableMap();
		BackendTerm term = BackendTerm.createTerm(query, map).expandCalls(map);
		
		BackendCallFactory factory = context.getConfiguration().getCallFactory();
		BackendModule mainModule = context.getMainModule();
		BackendAbstractCall call = factory.makeCallFromTerm(term, mainModule, context);
		
		RuntimeVariable[] L = new RuntimeVariable[map.getNumberOfVariables()];
		for (int i = 0; i < L.length; i++) {
			L[i] = new RuntimeVariable();
		}
		
		for (Entry<String, Integer> entry : map.getNonAnonMap().entrySet()) {
			bindings.put(entry.getKey(), L[entry.getValue()]);
		}
		
		this.goal = call.makeGoal(L, this, null, mainModule);
	}
	
	/**
	 * Runs the query first time.
	 */
	public boolean execute() throws LLJException {
		try {
			return run(goal);
		} catch (Exception e) {
			throw new LLJException("Unhandled exception", e);
		}
	}
	
	/**
	 * Checks whether the query has more solutions.
	 */
	public boolean hasMore() throws LLJException {
		try {
			return run(backtrack());
		} catch (NoChoiceException e) {
			return false;
		} catch (Exception e) {
			throw new LLJException("Unhandled exception", e);
		}
	}
	
	/**
	 * The main execution loop.
	 * 
	 * @param goal The initial goal.
	 */
	private boolean run(AbstractGoal goal) throws Exception {
		while (goal != null) {
			try {
				
				if (goal.backtracked) {
					goal.backtracked = false;
					goal.n += 1;
					goal = goal.reentry(this);
				} else {
					goal.n = 0;
					goal = goal.run(this);
				}
				
				callCount++;
			} catch (NoChoiceException e) {
				return false;
			} catch (Exception e) {
				throw e;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the value of the given variable.
	 * @throws LLJException When the binding does not exist.
	 */
	public Object getBinding(String name) throws LLJException {
		if (bindings.containsKey(name)) {
			return RuntimeVariable.deref(bindings.get(name));
		} else {
			throw new LLJException("Binding " + name + " does not exist");
		}
	}
	
	// FIXME document
	public Map<String, RuntimeVariable> getBindingMap() {
		return bindings;
	}
	
	/**
	 * Sets the value of the variable with the given name.
	 */
	public void setBinding(String name, Object value) {
		bindings.get(name).reference = value;
	}

	public LLJContext getContext() {
		return context;
	}

	/**
	 * Returns the number of predicate definition calls executed this far.
	 */
	public long getCallCount() {
		return callCount;
	}
	
}

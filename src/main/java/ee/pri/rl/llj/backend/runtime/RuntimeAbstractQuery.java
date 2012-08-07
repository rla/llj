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

import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;

/**
 * Base class for LLJ queries. Contains primitive functionality
 * needed for running queries.
 * 
 * @author Raivo Laanemets
 */
public abstract class RuntimeAbstractQuery {

	/**
	 * Used in unification.
	 */
	private final Object[] left;
	private final Object[] right;
	
	/**
	 * Stack management.
	 */
	private final RuntimeVariable[] trail;
	private final AbstractGoal[] choices;
	
	private int trailPosition = -1;
	private int choicePosition = -1;
	
	/**
	 * Flag that is set only for the execution of goal
	 * that was reached by backtracking.
	 */
	public boolean currentByBacktracking = false;
	
	/**
	 * Flag that is set when the current goal
	 * becktracks.
	 */
	public boolean currentBacktracked = false;
	
	/**
	 * Constructor for query.
	 * 
	 * @param trailSize Size of the trail stack.
	 * @param choiceSize Size of the choice point stack.
	 * @param unifyStackSize Size of the unification stack.
	 */
	public RuntimeAbstractQuery(int trailSize, int choiceSize, int unifyStackSize) {
		//this.stack = new Stack(stackSize);
		this.left = new Object[unifyStackSize];
		this.right = new Object[unifyStackSize];
		
		this.trail = new RuntimeVariable[trailSize];
		this.choices = new AbstractGoal[choiceSize];
	}
	
	/**
	 * Adds new choice point (of the given goal) into the the stack.
	 * This is done when the goal is executed and it has one or
	 * more choices left.
	 */
	public final void markChoice(AbstractGoal goal) {
		choices[++choicePosition] = goal;
		goal.trailPosition = trailPosition;
	}
	
	/**
	 * Add new variable for unbinding into the stack.
	 */
	public final void trail(RuntimeVariable variable) {
		trail[++trailPosition] = variable;
	}
	
	/**
	 * Backtracks to the last choicepoint. Unbinds all
	 * variables that were bound since the choicepoint.
	 * 
	 * @return The last goal that has choice point.
	 * @throws NoChoiceException If there is no choice point available.
	 */
	public AbstractGoal backtrack() throws NoChoiceException {
		if (choicePosition < 0) {
			throw new NoChoiceException();
		}
		
		AbstractGoal goal = choices[choicePosition--];
		
		while (trailPosition > goal.trailPosition) {
			trail[trailPosition].reference = trail[trailPosition];
			trailPosition--;
		}
		
		goal.backtracked = true;
		
		return goal;
	}
	
	/**
	 * Removes choice point at the given stack position (
	 * and choice points in between).
	 * Does not unbind variable binding. Used for implementing the cut
	 * operator.
	 */
	public void removeChoice(int choicePoint) {
		choicePosition = choicePoint - 1;
	}
	
	public int lastGoalIndex() {
		return choicePosition;
	}
	
	public void undoLastGoalBindings() {
		int pos = choicePosition >= 0 ? choices[choicePosition].trailPosition + 1 : 0;
		
		while (pos <= trailPosition) {			
			trail[trailPosition].reference = trail[trailPosition];
			trailPosition--;
		}
	}
	
	/**
	 * Prepares stack for running another query.
	 */
	public void reset() {
		trailPosition = -1;
		choicePosition = -1;
	}
	
	/**
	 * Method for unifying two LLJ terms. Does not allow for null values.
	 * 
	 * @param o1 First term.
	 * @param o2 Second term.
	 */
	public final boolean unify(Object o1, Object o2) {
		int pos = 0;
		left[pos] = o1;
		right[pos] = o2;
		RuntimeVariable v;
		
		while (pos > -1) {
			o1 = left[pos];
			o2 = right[pos];
			
			while (o1.getClass() == RuntimeVariable.class) {
				v = ((RuntimeVariable) o1);
				o1 = v.reference;
				if (o1 == v) {
					break;
				}
			}
			
			while (o2.getClass() == RuntimeVariable.class) {
				v = ((RuntimeVariable) o2);
				o2 = v.reference;
				if (o2 == v) {
					break;
				}
			}
			
			if (o1 == o2) {
				pos--;
				continue;
			}
			
			if (o1.getClass() == RuntimeVariable.class) {
				v = (RuntimeVariable) o1;
				v.reference = o2;
				trail[++trailPosition] = v;
				pos--;
				continue;
			}
			
			if (o2.getClass() == RuntimeVariable.class) {
				v = (RuntimeVariable) o2;
				v.reference = o1;
				trail[++trailPosition] = v;
				pos--;
				continue;
			}
			
			if (o1.getClass() == RuntimeListStruct.class) {
				if (o2.getClass() == RuntimeListStruct.class) {
					RuntimeListStruct l1 = (RuntimeListStruct) o1;
					RuntimeListStruct l2 = (RuntimeListStruct) o2;
					left[pos] = l1.tail;
					right[pos] = l2.tail;
					pos++;
					left[pos] = l1.head;
					right[pos] = l2.head;
					continue;
				} else {
					return false;
				}
			}
			
			if (o1.getClass() == RuntimeStruct.class) {
				if (o2.getClass() == RuntimeStruct.class) {
					Object[] d1 = ((RuntimeStruct) o1).data;
					Object[] d2 = ((RuntimeStruct) o2).data;
					if (d1.length != d2.length || !d1[0].equals(d2[0])) {
						return false;
					} else {
						final int n = d1.length;
						for (int i = 1; i < n; i++) {
							left[pos] = d1[i];
							right[pos] = d2[i];
							pos++;
						}
						pos--;
						continue;
					}
				} else {
					return false;
				}
			}
			
			if (o1.getClass() == o2.getClass() && o1.equals(o2)) {
				pos--;
				continue;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Method for testing if two LLJ terms unify. Does not allow for null values.
	 * 
	 * @param o1 First term.
	 * @param o2 Second term.
	 */
	public final boolean unifyTest(Object o1, Object o2) {
		int pos = 0;
		left[pos] = o1;
		right[pos] = o2;
		RuntimeVariable v;
		
		while (pos > -1) {
			o1 = left[pos];
			o2 = right[pos];
			
			while (o1.getClass() == RuntimeVariable.class) {
				v = ((RuntimeVariable) o1);
				o1 = v.reference;
				if (o1 == v) {
					break;
				}
			}
			
			while (o2.getClass() == RuntimeVariable.class) {
				v = ((RuntimeVariable) o2);
				o2 = v.reference;
				if (o2 == v) {
					break;
				}
			}
			
			if (o1 == o2) {
				pos--;
				continue;
			}
			
			if (o1.getClass() == RuntimeVariable.class) {
				v = (RuntimeVariable) o1;
				v.reference = o2;
				pos--;
				continue;
			}
			
			if (o2.getClass() == RuntimeVariable.class) {
				v = (RuntimeVariable) o2;
				v.reference = o1;
				pos--;
				continue;
			}
			
			if (o1.getClass() == RuntimeListStruct.class) {
				if (o2.getClass() == RuntimeListStruct.class) {
					RuntimeListStruct l1 = (RuntimeListStruct) o1;
					RuntimeListStruct l2 = (RuntimeListStruct) o2;
					left[pos] = l1.tail;
					right[pos] = l2.tail;
					pos++;
					left[pos] = l1.head;
					right[pos] = l2.head;
					continue;
				} else {
					return false;
				}
			}
			
			if (o1.getClass() == RuntimeStruct.class) {
				if (o2.getClass() == RuntimeStruct.class) {
					Object[] d1 = ((RuntimeStruct) o1).data;
					Object[] d2 = ((RuntimeStruct) o2).data;
					if (d1.length != d2.length || !d1[0].equals(d2[0])) {
						return false;
					} else {
						final int n = d1.length;
						for (int i = 1; i < n; i++) {
							left[pos] = d1[i];
							right[pos] = d2[i];
							pos++;
						}
						pos--;
						continue;
					}
				} else {
					return false;
				}
			}
			
			if (o1.getClass() == o2.getClass() && o1.equals(o2)) {
				pos--;
				continue;
			} else {
				return false;
			}
		}
		
		return true;
	}
}

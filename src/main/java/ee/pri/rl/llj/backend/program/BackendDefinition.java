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
package ee.pri.rl.llj.backend.program;

import java.io.Serializable;

import ee.pri.rl.llj.backend.VariableMap;
import ee.pri.rl.llj.backend.program.call.BackendAbstractCall;
import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.term.BackendTerm;
import ee.pri.rl.llj.backend.term.BackendVariable;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.operators.Operator;
import ee.pri.rl.llj.common.term.Functor;
import ee.pri.rl.llj.common.term.StringAtom;
import ee.pri.rl.llj.common.term.Struct;
import ee.pri.rl.llj.common.term.Term;

public final class BackendDefinition implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public BackendAbstractCall call;
	public final int numberOfVariables;
	public final BackendVariable[] leftUnifications;
	public final BackendTerm[] rightUnifications;
	public final Functor functor;
	
	private final VariableMap variableMap;
	private final BackendTerm body;
	private final BackendModule module;
	
	public BackendDefinition(Term term, BackendModule module) {
		Struct head = null;
		Struct body = null;
		
		if (term instanceof Struct) {
			Struct struct = (Struct) term;
			
			if (Operator.COLONMINUS.getFunctor().equals(struct.getFunctor())) {
				head = Struct.toStruct(struct.first());
				body = Struct.toStruct(struct.second());
			} else {
				head = struct;
			}
			
		} else if (term instanceof StringAtom) {
			head = Struct.toStruct(term);
		} else {
			throw new RuntimeException("Cannot use term " + term + " as a definition");
		}
		
		this.functor = head.getFunctor();
		this.rightUnifications = new BackendTerm[functor.arity];
		this.leftUnifications = new BackendVariable[functor.arity];
		this.variableMap = new VariableMap();
		this.module = module;
		
		// First create and generate identificators
		// for argument variables (arguments must be
		// in the right order at the start of the environment).
		
		for (int a = 0; a < functor.arity; a++) {
			leftUnifications[a] = (BackendVariable) new BackendVariable(variableMap.generateId());
		}
		
		for (int a = 0; a < functor.arity; a++) {
			rightUnifications[a] = BackendTerm.createTerm(head.arg(a), variableMap);
		}
		
		this.body = body == null ? null : BackendTerm.createTerm(body, variableMap).expandCalls(variableMap);
		this.numberOfVariables = variableMap.getNumberOfVariables();
	}
	
	/**
	 * Resolves reference to the predicate for each call.
	 * 
	 * @param lljContext LLJ context.
	 * @throws LLJException When resolving fails.
	 */
	public void resolve(LLJContext lljContext) throws LLJException {
		if (body != null) {
			call = lljContext.getConfiguration().getCallFactory().makeCallFromTerm(body, module, lljContext);
		}
	}
	
	/**
	 * Returns i-th argument. Used for indexing purposes.
	 */
	public BackendTerm getArgument(int i) {
		return rightUnifications[i];
	}
	
	/**
	 * Tries to unify definition head with the given arguments.
	 * Returns false if that fails (but does not remove variable bindings).
	 * 
	 * @param L the local environment 
	 * @param Q current runtime
	 * @return true if unifications succeed, otherwise false
	 */
	public boolean headUnify(Object[] L, RuntimeQuery Q) {
		int c = leftUnifications.length;
		for (int i = 0; i < c; i++) {
			if (!Q.unify(L[leftUnifications[i].id], rightUnifications[i].getRuntimeObject(L))) {
				return false;
			}
		}
		
		return true;
	}

	public String toString(Functor predicate) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(predicate.name);
		
		if (predicate.arity > 0) {
			builder.append('(');
			for (int i = 0; i < predicate.arity; i++) {
				if (i > 0) {
					builder.append(", ");
				}
				builder.append("V" + i);
			}
			
			builder.append(')');
		}
		
		builder.append(" :-");
		
		for (int i = 0; i < leftUnifications.length; i++) {
			builder.append('\n');
			builder.append("  ").append("V" + leftUnifications[i].id)
				.append(" = ").append(rightUnifications[i]);
			if (i < leftUnifications.length - 1) {
				builder.append(',');
			}
		}
		
		/*
		for (int i = 0; i < calls.length; i++) {
			builder.append(",\n");
			builder.append("  ").append(calls[i]);
		}*/
		
		builder.append(".\n");
		
		return builder.toString();
	}

}

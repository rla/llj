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
package ee.pri.rl.llj.backend.term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ee.pri.rl.llj.backend.VariableMap;
import ee.pri.rl.llj.backend.runtime.RuntimeStruct;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;
import ee.pri.rl.llj.common.operators.Operator;
import ee.pri.rl.llj.common.term.Functor;
import ee.pri.rl.llj.common.term.Struct;

public final class BackendStruct extends BackendTerm {
	private static final long serialVersionUID = 1L;
	
	private final String name;
	private final BackendTerm[] args;
	private final Object ground;
	
	public BackendStruct(String name, BackendTerm[] args) {
		this.name = name;
		this.args = args;
		this.ground = null;
	}
	
	public BackendStruct(Struct struct, VariableMap variableMap) {
		name = struct.name;
		
		args = new BackendTerm[struct.getArity()];
		for (int i = 0; i < args.length; i++) {
			args[i] = createTerm(struct.arg(i), variableMap);
		}
		
		if (struct.isGround()) {
			ground = getRuntimeObject(new RuntimeVariable[0]);
		} else {
			ground = null;
		}
	}
	
	public BackendTerm[] getArguments() {
		return args;
	}

	@Override
	public Object getRuntimeObject(Object[] env) {
		if (ground != null) {
			return ground;
		}
		Object[] data = new Object[args.length + 1];
		data[0] = name;
		for (int i = data.length - 1; i > 0; i--) {
			data[i] = args[i - 1].getRuntimeObject(env);
		}
		
		return new RuntimeStruct(data);
	}
	
	/**
	 * Returns the functor of this structure.
	 */
	public Functor getFunctor() {
		return new Functor(name, args.length);
	}

	@Override
	public Object getGround() {
		return ground;
	}

	@Override
	public BackendTerm expandCalls(VariableMap map) {
		Functor functor = getFunctor();
		
		// Binary flow-control operators.
		
		if (Operator.COMMA.getFunctor().equals(functor)
				|| Operator.SEMICOLON.getFunctor().equals(functor)
				|| Operator.IF_THEN.getFunctor().equals(functor)
				|| Operator.COLON.getFunctor().equals(functor)) {
			
			return new BackendStruct(name, new BackendTerm[] {args[0].expandCalls(map), args[1].expandCalls(map)});
		}
		
		// Unary flow-control operator.
		
		if (Functor.NOT.equals(functor)) {
			return new BackendStruct(name, new BackendTerm[] {args[0].expandCalls(map)});
		}
		
		// We have a call, expand arguments that are not variables.
		
		List<BackendStruct> unifications = new ArrayList<BackendStruct>();
		for (int a = 0; a < args.length; a++) {
			if (!(args[a] instanceof BackendVariable)) {
				BackendVariable v = new BackendVariable(map.generateId());
				BackendTerm t = args[a];
				args[a] = v;
				unifications.add(new BackendStruct("=", new BackendTerm[] {v, t}));
			}
		}
		
		// Write unifications into chain of calls.
		
		Collections.reverse(unifications);
		
		BackendStruct last = this;
		for (BackendStruct u : unifications) {
			last = new BackendStruct(",", new BackendTerm[] {u, last});
		}
		
		return last;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(name).append('(');
		
		for (int a = 0; a < args.length; a++) {
			if (a > 0) {
				builder.append(", ");
			}
			builder.append(args[a]);
		}
		
		builder.append(')');
		
		return builder.toString();
	}

}

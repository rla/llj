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
import java.util.ArrayList;
import java.util.List;

import ee.pri.rl.llj.backend.VariableMap;
import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.backend.term.BackendObject;
import ee.pri.rl.llj.backend.term.BackendStruct;
import ee.pri.rl.llj.backend.term.BackendTerm;
import ee.pri.rl.llj.backend.term.BackendVariable;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.ModulePath;
import ee.pri.rl.llj.common.operators.Operator;
import ee.pri.rl.llj.common.term.Functor;

/**
 * Factory class for transforming normalized calls to backend calls.
 * 
 * @author Raivo Laanemets
 */
public class BackendCallFactory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Helper method for creating backend call from a term.
	 * This function cannot be called runtime since it does
	 * not accept runtime terms. Used for flow-control calls
	 * (see {@link Functor#isFlowControl()}).
	 */
	private BackendAbstractCall makeFlowCall(
			Functor functor,
			ModulePath path,
			BackendModule module,
			LLJContext context,
			BackendTerm[] arguments) throws LLJException {
		
		if (Functor.NOT.equals(functor)) {
			return new BackendNotCall(makeCallFromTerm(arguments[0], module, context));
		}
		
		if (Operator.COMMA.getFunctor().equals(functor)) {			
			BackendAbstractCall left = makeCallFromTerm(arguments[0], module, context);
			BackendAbstractCall right = makeCallFromTerm(arguments[1], module, context);
			
			return new BackendAndCall(module, left, right);
		}
		
		if (Operator.COLON.getFunctor().equals(functor)) {
			return makeCallFromTerm(new BackendStruct(":", arguments), module, context);
		}
		
		if (Operator.SEMICOLON.getFunctor().equals(functor)) {
			BackendAbstractCall left = makeCallFromTerm(arguments[0], module, context);
			BackendAbstractCall right = makeCallFromTerm(arguments[1], module, context);
			
			return new BackendOrCall(module, left, right);
		}
		
		if (Operator.IF_THEN.getFunctor().equals(functor)) {
			BackendAbstractCall left = makeCallFromTerm(arguments[0], module, context);
			BackendAbstractCall right = makeCallFromTerm(arguments[1], module, context);
			
			return new BackendIfThenCall(module, left, right);
		}
		
		throw new RuntimeException("Unknown call " + path + ":" + functor);
	}
	
	/**
	 * Helper method for creating backend call from a term.
	 * This can be called both runtime and before.
	 */
	private BackendAbstractCall makeCall(
			Functor functor,
			ModulePath path,
			BackendModule module,
			LLJContext context,
			int[] arguments) throws LLJException {
		
		// Cut and unification are handled separatedly.
		
		if (Functor.FAIL.equals(functor)) {
			return BackendFailCall.CALL;
		}
		
		if (Functor.CUT.equals(functor)) {
			return BackendCutCall.CALL;
		}
		
		// Restricted unification, arguments are variables.
		
		if (Operator.UNIFIABLE.getFunctor().equals(functor)) {
			return new BackendUnificationCall(arguments, module);
		}
		
		// General predicates/built-ins.
		
		if (context.getConfiguration().isGlobalNamespace()) {
			return context.getMainModule().resolveCall(functor, arguments);
		}
		
		if (path == null || path.path.isEmpty()) {
			return module.resolveCall(functor, arguments);
		}
		
		if (path != null && !path.path.isEmpty()) {
			BackendModule calledModule = context.getModule(path);
			if (calledModule == null) {
				throw new RuntimeException("There is no module " + path);
			} else {
				return calledModule.resolveLocalCall(functor, arguments, module);
			}
		}
		
		throw new RuntimeException("Unknown call " + path + ":" + functor);
	}
	
	/**
	 * Creates call from the given term.
	 * 
	 * @param term term representing call
	 * @param module the module which the call is made from 
	 * @param context LLJ context
	 */
	public BackendAbstractCall makeCallFromTerm(
			BackendTerm term,
			BackendModule module,
			LLJContext context) throws LLJException {
		
		if (term instanceof BackendStruct) {
			BackendStruct struct = (BackendStruct) term;
		
			// Extracts module path and call.
			// FIXME needs better error message when terms are not suitable for call.
			
			List<String> pathTokens = new ArrayList<String>();
			while (Operator.COLON.getFunctor().equals(struct.getFunctor())) {
				BackendTerm[] arguments = struct.getArguments();
				pathTokens.add(((BackendObject) arguments[0]).value.toString());
				struct = (BackendStruct) arguments[1];
			}
			
			ModulePath path = new ModulePath(pathTokens);
			Functor functor = struct.getFunctor();
			boolean hasNonVariables = hasNonVariable(struct.getArguments());
			
			// This is term constructor call.
			
			if (functor.equals(Operator.UNIFIABLE.getFunctor())
					&& hasNonVariables) {
				
				return new BackendConstructorCall(module, ((BackendVariable) struct.getArguments()[0]).id, struct.getArguments()[1]);
			}
			
			if (functor.isFlowControl()) {
				return makeFlowCall(functor, path, module, context, struct.getArguments());
			}
			
			// General predicate/built-in call.
			// Arguments are indexes of parent environment.
			
			if (hasNonVariables) {
				throw new RuntimeException("Call " + term + " has non-variables as arguments");
			}
			
			int[] arguments = argumentsToParent(struct.getArguments());
			
			return makeCall(functor, path, module, context, arguments);
		}
		
		if (term instanceof BackendObject) {
			Functor functor = new Functor(((BackendObject) term).value.toString());
			return makeCall(functor, null, module, context, new int[0]);
		}
		
		throw new RuntimeException("Cannot convert " + term + " into usable call");
	}
	
	/**
	 * Converts call arguments into an index vector that indexes
	 * into the parent environment. The arguments must be variables.
	 * This is achieved by calling {@link BackendStruct#expandCalls(VariableMap)}.
	 */
	private static int[] argumentsToParent(BackendTerm[] args) {
		int[] ret = new int[args.length];
		
		for (int a = 0; a < args.length; a++) {
			ret[a] = ((BackendVariable) args[a]).id;
		}
		
		return ret;
	}
	
	/**
	 * Returns true if the arguments contain non-variable term.
	 */
	private static boolean hasNonVariable(BackendTerm[] args) {
		for (BackendTerm t : args) {
			if (!(t instanceof BackendVariable)) {
				return true;
			}
		}
		
		return false;
	}
}

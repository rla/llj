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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ee.pri.rl.llj.backend.VariableMap;
import ee.pri.rl.llj.common.term.FloatAtom;
import ee.pri.rl.llj.common.term.ListStruct;
import ee.pri.rl.llj.common.term.LongAtom;
import ee.pri.rl.llj.common.term.StringAtom;
import ee.pri.rl.llj.common.term.Struct;
import ee.pri.rl.llj.common.term.Term;
import ee.pri.rl.llj.common.term.Variable;

/**
 * Base class for terms used in the backend.
 * 
 * @author Raivo Laanemets
 */
public abstract class BackendTerm implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Returns the runtime object representation of this term.
	 */
	public abstract Object getRuntimeObject(Object[] env);
	
	/**
	 * Returns ground instance of this object (if there is any).
	 */
	public abstract Object getGround();
	
	/**
	 * Expands calls p(t(1), t(2)) into calls A1 = t(1), A2 = t(2), p(A1, A2)
	 * thus allowing p arguments to refer as positions into the local environment.
	 * 
	 * @param map used for generating aux variables.
	 */
	public abstract BackendTerm expandCalls(VariableMap map);
	
	/**
	 * Creates backend term from normal term.
	 * 
	 * @param term Normal term.
	 * @param variableMap Variable map that is used to associate variables with integer id's.
	 * @return Backend term representation of the normal term.
	 */
	public static BackendTerm createTerm(Term term, VariableMap variableMap) {
		if (term instanceof StringAtom) {
			return new BackendObject(((StringAtom) term).value);
		} else if (term instanceof LongAtom) {
			return new BackendObject(((LongAtom) term).value);
		} else if (term instanceof FloatAtom) {
			return new BackendObject(((FloatAtom) term).value);
		} else if (term instanceof Struct) {
			return new BackendStruct((Struct) term, variableMap);
		} else if (term instanceof Variable) {
			return new BackendVariable(variableMap.getId((Variable) term));
		} else if (term instanceof ListStruct) {
			return new BackendListStruct((ListStruct) term, variableMap);
		} else {
			throw new RuntimeException("Cannot create backend term from " + term.getClass());
		}
	}
	
	public static BackendTerm[] createTerms(List<? extends Term> terms, VariableMap variableMap) {
		List<BackendTerm> ret = new ArrayList<BackendTerm>();
		
		for (Term t : terms) {
			ret.add(createTerm(t, variableMap));
		}
		
		return ret.toArray(new BackendTerm[ret.size()]);
	}
	
}

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
package ee.pri.rl.llj.backend.program.predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ee.pri.rl.llj.backend.program.BackendDefinition;
import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.common.term.Functor;

/**
 * Backend class for predicates that have fixed
 * number of definition.
 *  
 * @author Raivo Laanemets
 * @since 1.1
 */
public final class StaticBackendPredicate extends AbstractBackendPredicate {
	private static final long serialVersionUID = 1L;
	
	public final BackendDefinition[] definitions;
	public final int largestEnvironment;
	
	public StaticBackendPredicate(
			List<BackendDefinition> definitions,
			boolean moduleTransparent,
			BackendModule context,
			Functor functor) {
		
		super(moduleTransparent, context, functor);
		this.definitions = definitions.toArray(new BackendDefinition[definitions.size()]);
		
		int largest = 0;
		for (BackendDefinition definition : definitions) {
			if (definition.numberOfVariables > largest) {
				largest = definition.numberOfVariables;
			}
		}
		
		largestEnvironment = largest;
		
		// First argument indexing code.
		if (functor.arity > 0) {
			boolean hasFirstGround = true;
			boolean firstArgUnique = true;
			List<Object> values = new ArrayList<Object>();
			
			for (BackendDefinition definition : definitions) {
				Object ground = definition.getArgument(0).getGround();
				hasFirstGround &= ground != null;
				firstArgUnique &= !values.contains(ground);
				values.add(ground);
			}
			
			if (hasFirstGround && firstArgUnique && values.size() >= 2) {
				Map<Object, BackendDefinition> index = new HashMap<Object, BackendDefinition>();
				for (int i = 0; i < values.size(); i++) {
					index.put(values.get(i), definitions.get(i));
				}
				// FIXME this kind of indexing is slow
				//this.index = new UniqueFirstArgumentIndex(index);
			}
		}
	}

	public int getLargestEnvironment() {
		return largestEnvironment;
	}

	@Override
	public int getNumberOfDefinitions() {
		return definitions.length;
	}

	@Override
	public BackendDefinition getDefinition(int i) {
		return definitions[i];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (BackendDefinition d : definitions) {
			builder.append(d.toString(functor)).append('\n');
		}
		
		return builder.toString();
	}

}

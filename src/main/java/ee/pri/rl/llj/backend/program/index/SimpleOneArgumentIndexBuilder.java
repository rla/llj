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
package ee.pri.rl.llj.backend.program.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ee.pri.rl.llj.backend.program.BackendDefinition;
import ee.pri.rl.llj.backend.program.predicate.AbstractBackendPredicate;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;
import ee.pri.rl.llj.backend.term.BackendObject;
import ee.pri.rl.llj.backend.term.BackendTerm;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.log.LLJLogger;

/**
 * Index builder for {@link SimpleOneArgumentIndex}.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 */
public class SimpleOneArgumentIndexBuilder implements IndexBuilder {
	private static final LLJLogger log = LLJLogger.getLogger(SimpleOneArgumentIndexBuilder.class);
	
	private int argumentNumber;

	public SimpleOneArgumentIndexBuilder(int argumentNumber) {
		this.argumentNumber = argumentNumber;
	}

	@Override
	public void buildIndexFor(AbstractBackendPredicate predicate) throws LLJException {
		log.debug("building index for " + predicate.functor);
		
		Map<Object, List<BackendDefinition>> map = new HashMap<Object, List<BackendDefinition>>();
		
		int n = predicate.getNumberOfDefinitions();
		
		for (int i = 0; i < n; i++) {
			BackendDefinition definition = predicate.getDefinition(i);
			Object argumentValue = getArgumentValue(definition, argumentNumber);
			List<BackendDefinition> definitions = map.get(argumentValue);
			if (definitions == null) {
				definitions = new ArrayList<BackendDefinition>();
				map.put(argumentValue, definitions);
			}
			
			definitions.add(definition);
		}
		
		predicate.index = new SimpleOneArgumentIndex(map, argumentNumber);
		
		log.debug("index building finished");
	}
	
	private static Object getArgumentValue(
			BackendDefinition definition,
			int argumentNumber) throws LLJException {
		
		BackendTerm first = definition.getArgument(argumentNumber);
		
		if (first instanceof BackendObject) {
			return ((BackendObject) first).getRuntimeObject(RuntimeVariable.EMPTY_ENVIRONMENT);
		} else {
			throw new LLJException("Can only index on atoms");
		}
	}

}

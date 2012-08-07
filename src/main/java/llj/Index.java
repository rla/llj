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
package llj;

import ee.pri.rl.llj.backend.program.index.SimpleOneArgumentIndexBuilder;
import ee.pri.rl.llj.backend.program.predicate.AbstractBackendPredicate;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.JavaCallGoal;
import ee.pri.rl.llj.common.java.JavaModule;
import ee.pri.rl.llj.common.java.Predicate;
import ee.pri.rl.llj.common.log.LLJLogger;
import ee.pri.rl.llj.common.term.Functor;

import java.lang.String;

/**
 * Module to help to index the predicates.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 */
public class Index implements JavaModule {
	private static final LLJLogger log = LLJLogger.getLogger(Index.class);

	@Predicate(name="index_one_argument", arity = 3)
	public AbstractGoal index_one_argument3(JavaCallGoal goal) throws Exception {
		
		String name = goal.getStringArg(0);
		int arity = (int) goal.getLongArg(1);
		int argumentNumber = (int) goal.getLongArg(2);
		
		Functor functor = new Functor(name, arity);
		
		if (log.isDebugEnabled()) {
			log.debug("trying to create an index for " + functor + " for argument " + argumentNumber);
		}
		
		AbstractBackendPredicate predicate = goal.M.getPredicate(functor);
		new SimpleOneArgumentIndexBuilder(argumentNumber).buildIndexFor(predicate);
		
		return goal.G;
	}
}

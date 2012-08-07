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

import java.io.BufferedReader;
import java.io.StringReader;

import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.term.Term;
import ee.pri.rl.llj.frontend.pass1.Tokenizer;
import ee.pri.rl.llj.frontend.pass2.Parser;

/**
 * Factory class for creating queries.
 * 
 * @author Raivo Laanemets
 */
public class RuntimeQueryFactory {
	private static final int DEFAULT_UNIFY_STACK_SIZE = 20000;
	private static final int DEFAULT_TRAIL_SIZE = 20000;
	private static final int DEFAULT_CHOICE_SIZE = 1024;
	
	private int maxUnifyStackSize = DEFAULT_UNIFY_STACK_SIZE;
	private int trailSize = DEFAULT_TRAIL_SIZE;
	private int choiceSize = DEFAULT_CHOICE_SIZE;

	/**
	 * Creates new query based on the query string.
	 * 
	 * @param queryString Query as string.
	 * @param context LLJContext.
	 */
	public RuntimeQuery createQuery(String queryString, LLJContext context) throws LLJException {
		Tokenizer tokenizer = new Tokenizer(new BufferedReader(new StringReader(queryString)));
		Parser parser = new Parser(tokenizer);
		
		Term query = parser.parseSingleTerm();

		return new RuntimeQuery(query, context, trailSize, choiceSize, maxUnifyStackSize);
	}

	public int getMaxUnifyStackSize() {
		return maxUnifyStackSize;
	}

	public void setMaxUnifyStackSize(int maxUnifyStackSize) {
		this.maxUnifyStackSize = maxUnifyStackSize;
	}

}

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
package ee.pri.rl.llj.test;

import junit.framework.TestCase;
import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.common.LLJException;

// FIXME make this base class for all tests.
public abstract class AbstractLLJTest extends TestCase {
	protected LLJContext context;
	
	public AbstractLLJTest() {
		try {
			context = new LLJContext(getPath());
		} catch (LLJException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract String getPath();
	
	protected RuntimeQuery query(String queryString) throws LLJException {
		return context.createQuery(queryString);
	}
}

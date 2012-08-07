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
package ee.pri.rl.llj.test.lpn;

import junit.framework.TestCase;
import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.common.LLJException;

// Excercise 2.3
public class Word5Test extends TestCase {
	private LLJContext context;

	public Word5Test() throws LLJException {
		context = new LLJContext("ee:pri:rl:llj:test:lpn:word5");
	}
	
	public void testEnumerate() throws LLJException {
		RuntimeQuery query = context.createQuery("sentence(Word1, Word2, Word3, Word4, Word5)");
		assertTrue(query.execute());
		int i = 1;
		while (query.hasMore()) {
			i++;
		}
		
		assertEquals(32, i);
	}
}

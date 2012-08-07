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

import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.common.LLJException;

public class CutTest extends AbstractLLJTest {
	
	public void testMax1() throws LLJException {
		RuntimeQuery query = query("max(2, 1, M)");
		assertTrue(query.execute());
		
		assertEquals(2L, query.getBinding("M"));
		assertFalse(query.hasMore());
	}
	
	public void testMax2() throws LLJException {
		RuntimeQuery query = query("max(1, 2, M)");
		assertTrue(query.execute());
		
		assertEquals(2L, query.getBinding("M"));
		assertFalse(query.hasMore());
	}

	@Override
	protected String getPath() {
		return "ee:pri:rl:llj:test:cut";
	}

}

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

public class KnowledgeBase2Test extends TestCase {
	private LLJContext context;

	public KnowledgeBase2Test() throws LLJException {
		context = new LLJContext("ee:pri:rl:llj:test:lpn:kb2");
	}

	public void testAirGuitarMia() throws LLJException {
		RuntimeQuery query = context.createQuery("playsAirGuitar(mia)");
		assertTrue(query.execute());
		assertFalse(query.hasMore());
	}
	
	public void testAirGuitarYolanda() throws LLJException {
		RuntimeQuery query = context.createQuery("playsAirGuitar(yolanda)");
		assertTrue(query.execute());
		assertFalse(query.hasMore());
	}
	
}

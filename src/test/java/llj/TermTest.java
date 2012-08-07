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

import java.lang.String;

import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.RuntimeStruct;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.test.AbstractLLJTest;

public class TermTest extends AbstractLLJTest {
	
	public void testCopyTerm() throws LLJException {
		RuntimeQuery query = query("copy_term(t(M), t(N))");
		assertTrue(query.execute());
		assertFalse(query.getBinding("M") == query.getBinding("N"));
	}
	
	public void testInFunctor() throws LLJException {
		RuntimeQuery query = query("functor(t(a,b), F, A)");
		assertTrue(query.execute());
		assertEquals("t", query.getBinding("F"));
		assertEquals(2, query.getBinding("A"));
		assertFalse(query.hasMore());
	}
	
	public void testOutFunctor() throws LLJException {
		RuntimeQuery query = query("functor(T, t, 2)");
		assertTrue(query.execute());
		assertTrue(query.getBinding("T") instanceof RuntimeStruct);
		assertFalse(query.hasMore());
	}

	@Override
	protected String getPath() {
		return "llj:term";
	}
}

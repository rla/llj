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
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.test.AbstractLLJTest;

public class ListTest extends AbstractLLJTest {

	public void test() throws LLJException {
		RuntimeQuery query = query("invert([1,2,3], L)");
		assertTrue(query.execute());
		
		assertEquals("[3,2,1]", query.getBinding("L").toString());
	}
	
	public void testIsMember() throws LLJException {		
		RuntimeQuery query = query("member(E, [2,2,3])");
		assertTrue(query.execute());
		
		assertEquals("2", query.getBinding("E").toString());
	}
	
	public void testAppend() throws LLJException {
		RuntimeQuery query = query("append([a,b,c],[1,2,3],[a,b,c,1,2,3])");
		assertTrue(query.execute());
		assertFalse(query.hasMore());
	}
	
	public void testCountAppend() throws LLJException {
		RuntimeQuery query = query("append(X,Y,[a,b,c,d])");
		assertTrue(query.execute());
		int i = 1;
		
		while (query.hasMore()) {
			i++;
		}
		
		assertEquals(5, i);
	}
	
	public void testSuffix() throws LLJException {
		RuntimeQuery query = query("suffix(S,[a,b,c,d])");
		assertTrue(query.execute());
		
		//System.out.println(query.getBinding("S"));
		
		int i = 1;
		
		while (query.hasMore()) {
			//System.out.println(query.getBinding("S"));
			i++;
		}
		
		assertEquals(5, i);
	}
	
	public void testPrefix() throws LLJException {
		RuntimeQuery query = query("prefix(S,[a,b,c,d])");
		assertTrue(query.execute());
		
		//System.out.println(query.getBinding("S"));
		
		int i = 1;
		
		while (query.hasMore()) {
			//System.out.println(query.getBinding("S"));
			i++;
		}
		
		assertEquals(5, i);
	}
	
	public void testCountSublist() throws LLJException {
		RuntimeQuery query = query("sublist(S,[a,b,c,d])");
		assertTrue(query.execute());
		//System.out.println(query.getBinding("S"));
		
		int i = 1;
		
		while (query.hasMore()) {
			//System.out.println(query.getBinding("S"));
			i++;
		}
		
		assertEquals(15, i);
	}
	
	public void testSort() throws LLJException {
		RuntimeQuery query = query("msort([a,c,b], [a,b,c])");
		assertTrue(query.execute());
		assertFalse(query.hasMore());
	}

	@Override
	protected String getPath() {
		return "llj:list";
	}
}

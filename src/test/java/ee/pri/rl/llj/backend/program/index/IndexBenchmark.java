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

import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.test.AbstractLLJTest;

public class IndexBenchmark extends AbstractLLJTest {
	
	public void test() throws LLJException {
		
		// Comment this out to run without indexing.
		assertTrue(query("create_index").execute());
		
		long start = System.currentTimeMillis();
		RuntimeQuery query = query("bench");
		assertTrue(query.execute());
		
		System.out.println("Time taken: " + (System.currentTimeMillis() - start) + "ms");
		
		System.out.println(query.getCallCount());
	}

	@Override
	protected String getPath() {
		return "ee:pri:rl:llj:backend:program:index:test";
	}

}

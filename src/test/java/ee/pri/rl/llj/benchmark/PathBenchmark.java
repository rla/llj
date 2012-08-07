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
package ee.pri.rl.llj.benchmark;

import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.common.LLJException;

public class PathBenchmark {

	public static void main(String[] args) throws LLJException {
		LLJContext context = new LLJContext("ee:pri:rl:llj:benchmark:path");
		
		RuntimeQuery query = context.createQuery("solve(p(2,2), L)");
		long start = System.currentTimeMillis();
		query.execute();
		System.out.println(query.getBinding("L"));
		System.out.println(System.currentTimeMillis() - start);
	}

}

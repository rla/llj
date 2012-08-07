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
package ee.pri.rl.llj.example;

import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.common.LLJException;

/**
 * Example about using LLJ.
 */
public class MemberExample {
	
	public static void main(String[] args) throws LLJException {
		// Creates new LLJ context
		LLJContext context = new LLJContext("llj:list");
		// Creates new LLJ query
		RuntimeQuery query = context.createQuery("member(X, [1,2,3])");
		
		// Executes the query for first answer
		if (query.execute()) {
			System.out.println("First answer: " + query.getBinding("X"));
		}
		
		// Tries to find more answers
		while (query.hasMore()) {
			System.out.println("Also: " + query.getBinding("X"));
		}
	}
}

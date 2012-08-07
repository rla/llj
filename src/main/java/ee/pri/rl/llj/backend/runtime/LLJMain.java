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

import ee.pri.rl.llj.common.LLJException;

/**
 * Helper class for running LLJ stand-alone programs.
 * 
 * @author Raivo Laanemets
 */
public class LLJMain {
	private LLJContext context;
	private String[] args;
	
	/**
	 * @param path The path of the main module. The main module must export predicate <code>main/1</code>.
	 * @param args The array of program arguments.
	 * @throws LLJException When the exception occurs during the load of the context.
	 */
	public LLJMain(String path, String[] args) throws LLJException {
		this.context = new LLJContext(path);
		this.args = args;
	}
	
	public void execute() throws LLJException {
		RuntimeQuery query = context.createQuery("main(Args)");
		query.setBinding("Args", args);
		if (!query.execute()) {
			throw new LLJException("The main query failed");
		}
	}

	/**
	 * Returns the LLJ context that is used for executing the program.
	 * @return
	 */
	public LLJContext getContext() {
		return context;
	}

}

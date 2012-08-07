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
package ee.pri.rl.llj.frontend.loader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.ModulePath;

/**
 * The module loader for loading LLJ code from
 * Java classpath.
 * 
 * @author Raivo Laanemets
 * @since 1.0
 */
public class ClassPathModuleLoader implements ModuleLoader {
	
	/**
	 * The default read buffer size (1000000 bytes).
	 */
	public static final int DEFAULT_BUF_SIZE = 1000000;
	
	private int bufferSize;
	
	/**
	 * Constructs a new reader with the given read buffer size.
	 * @param bufferSize The buffer size.
	 */
	public ClassPathModuleLoader(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	/**
	 * Constructs a new reader with the default buffer size.
	 */
	public ClassPathModuleLoader() {
		this(DEFAULT_BUF_SIZE);
	}

	@Override
	public BufferedReader load(ModulePath path) throws LLJException, ModuleNotFoundException {
		StringBuilder builder = new StringBuilder();
		
		builder.append("/");
		boolean first = true;
		for (String part : path.path) {
			if (first) {
				first = false;
			} else {
				builder.append("/");
			}
			builder.append(part);
		}
		
		String resourceName = builder.toString() + ".llj";
		InputStream input = null;

		input = this.getClass().getResourceAsStream(resourceName);
		if (input == null) {
			throw new ModuleNotFoundException();
		}
		
		return new BufferedReader(new InputStreamReader(input), bufferSize);
	}

}

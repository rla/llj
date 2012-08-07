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

import java.io.Reader;

import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.ModulePath;

/**
 * Base interface for LLJ module loaders.
 * 
 * @author Raivo Laanemets
 * @since 1.0
 */
public interface ModuleLoader {
	
	/**
	 * Returns the reader for the given module path.
	 * Whatever calls this method must close the reader
	 * afterwards.
	 * 
	 * @param path Path of the module.
	 * @throws LLJException When some IO or other exception occurs.
	 * @throws ModuleNotFoundException When the module is not found.
	 */
	public Reader load(ModulePath path) throws LLJException, ModuleNotFoundException;
}

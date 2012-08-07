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
package ee.pri.rl.llj.common;

import java.io.Serializable;
import java.util.List;

import ee.pri.rl.llj.common.term.Functor;

/**
 * Class for storing module information.
 * 
 * @author Raivo Laanemets
 */
public class ModuleInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final ModulePath path;
	public final List<Functor> exports;
	public final List<ModulePath> imports;
	
	public ModuleInfo(
		ModulePath path,
		List<Functor> exports,
		List<ModulePath> imports) {
		
		this.path = path;
		this.exports = exports;
		this.imports = imports;
	}

	@Override
	public String toString() {
		return path + ": " + exports;
	}

}

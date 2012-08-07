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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ee.pri.rl.llj.common.term.StringAtom;
import ee.pri.rl.llj.common.term.Struct;
import ee.pri.rl.llj.common.term.Term;
import ee.pri.rl.llj.common.term.visitor.ModulePathFlattenerVisitor;

/**
 * Represents name or path for single LLJ module.
 * 
 * Example path: <code>ee:pri:rl:llj:io</code> - refers to file
 * <code>/ee/pri/rl/llj/io.pl</code> in the classpath.
 */
public final class ModulePath implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of module as a list of package name tokens.
	 */
	public final List<String> path;
	
	public ModulePath(Term term) throws LLJException {
		ModulePathFlattenerVisitor visitor = new ModulePathFlattenerVisitor();
		
		term.visitWith(visitor);
		
		List<String> path = new LinkedList<String>();
		
		for (Term part : visitor.getPath()) {
			if (part instanceof StringAtom) {
				path.add(((StringAtom) part).value);
			} else {
				throw new LLJException("Module path can only contain strings");
			}
		}
		
		this.path = Collections.unmodifiableList(path);
	}

	public ModulePath(String pathString) {
		List<String> path = new LinkedList<String>();
		
		for (String part : pathString.split("\\:")) {
			path.add(part);
		}
		
		this.path = Collections.unmodifiableList(path);
	}
	
	public ModulePath(List<String> path) {
		this.path = Collections.unmodifiableList(path);
	}
	
	public Term toTerm() {
		List<String> path = new ArrayList<String>(this.path);
		Collections.reverse(path);
		
		Term tail = new StringAtom(path.get(0));
		path.remove(0);
		
		for (String string : path) {
			tail = new Struct(":", new StringAtom(string), tail); 
		}
		
		return tail;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ModulePath && ((ModulePath) obj).path.equals(path);
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public String toString() {
		if (path.size() == 1) {
			return path.get(0);
		}
		return path.toString();
	}
	
	public String toIdentifier() {
		StringBuilder builder = new StringBuilder();
		
		for (String token : path) {
			builder.append('_').append(token);
		}
		
		return builder.substring(1);
	}
	
}

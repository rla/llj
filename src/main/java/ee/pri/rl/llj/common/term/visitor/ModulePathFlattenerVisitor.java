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
package ee.pri.rl.llj.common.term.visitor;

import java.util.LinkedList;
import java.util.List;

import ee.pri.rl.llj.common.term.StringAtom;
import ee.pri.rl.llj.common.term.Struct;
import ee.pri.rl.llj.common.term.Term;

public class ModulePathFlattenerVisitor extends BaseRecursiveVisitor {
	private final List<Term> path;
	
	public ModulePathFlattenerVisitor() {
		path = new LinkedList<Term>();
	}

	@Override
	public void visitStruct(Struct input) {
		if (":".equals(input.name) && input.isBinary()) {
			input.first().visitWith(this);
			input.second().visitWith(this);
		} else {
			path.add(input);
		}
	}
	
	@Override
	public void visitStringAtom(StringAtom input) {
		path.add(input);
	}

	public List<Term> getPath() {
		return path;
	}
	
}
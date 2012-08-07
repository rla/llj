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
package ee.pri.rl.llj.backend.program;

import java.util.LinkedList;
import java.util.List;

import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.ModulePath;
import ee.pri.rl.llj.common.operators.Operator;
import ee.pri.rl.llj.common.term.Functor;
import ee.pri.rl.llj.common.term.ListStruct;
import ee.pri.rl.llj.common.term.Struct;
import ee.pri.rl.llj.common.term.Term;

/**
 * Helper class for working with module declaration.
 * 
 * @author Raivo Laanemets
 */
public final class ModuleDeclaration {
	private final ModulePath path;
	private final List<Functor> exports = new LinkedList<Functor>();

	/**
	 * Constructs new module declaration from the given directive.
	 */
	public ModuleDeclaration(Term term) throws LLJException {

		Struct struct = Struct.toStruct(term);
		if (!Operator.UNARY_COLONMINUS.getFunctor().equals(struct.getFunctor())) {
			throw new RuntimeException("Invalid module declaration: " + term);
		}
		
		Struct module = Struct.toStruct(struct.first()); // module(X)
		if (Functor.MODULE.equals(module.functor())) {
			parseExports(module.second());
			try {
				this.path = new ModulePath(module.first());
			} catch (LLJException e) {
				throw error();
			}
		} else {
			throw new RuntimeException("Invalid module declaration: " + term);
		}
	}

	private void parseExports(Term term) throws LLJException {
		if (term instanceof ListStruct) {
			while (term instanceof ListStruct) {
				ListStruct list = (ListStruct) term;				
				exports.add(Functor.fromTerm(list.head));
				term = list.tail;
			}
		} else {
			throw error();
		}
	}

	/**
	 * Helper method for returning exception with the explaining message.
	 */
	private LLJException error() {
		return new LLJException("Invalid module declaration");
	}

	public ModulePath getPath() {
		return path;
	}

	public List<Functor> getExports() {
		return exports;
	}

}

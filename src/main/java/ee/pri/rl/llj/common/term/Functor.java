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
package ee.pri.rl.llj.common.term;

import java.io.Serializable;

import ee.pri.rl.llj.common.operators.Operator;

/**
 * Represents functor (struct's name together with the arity of the struct).
 */
public final class Functor implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final Functor CUT = new Functor("!");
	public static final Functor FAIL = new Functor("fail");
	public static final Functor NOT = new Functor("not", 1);
	public static final Functor IMPORT = new Functor("import", 1);
	public static final Functor MODULE = new Functor("module", 2);
	public static final Functor MODULE_TRANSPARENT = new Functor("module_transparent", 1);
	public static final Functor DYNAMIC = new Functor("dynamic", 1);

	public final String name;
	public final int arity;

	/**
	 * Constructor for 0-ary functors.
	 */
	public Functor(String name) {
		this(name, 0);
	}

	/**
	 * Constructor for functor with given arity (arity >= 0).
	 */
	public Functor(String name, int arity) {
		this.name = name;
		this.arity = arity;
	}

	@Override
	public int hashCode() {
		return arity ^ name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Functor && ((Functor) obj).arity == arity && ((Functor) obj).name.equals(name);
	}

	@Override
	public String toString() {
		return name + "/" + arity;
	}
	
	/**
	 * Converts term (name/arity) into functor.
	 * String atom gets converted into name/0.
	 */
	public static Functor fromTerm(Term term) {
		if (term instanceof Struct) {
			Struct functor = (Struct) term;
			if (functor.name.equals("/") && functor.isBinary()) {
				String predicateName = ((StringAtom) functor.first()).value;
				int predicateArity = ((LongAtom) functor.second()).value.intValue();
				return new Functor(predicateName, predicateArity);
			} else {
				throw new IllegalArgumentException("Cannot convert " + functor + " to functor");
			}
		}
		
		if (term instanceof StringAtom) {
			return new Functor(((StringAtom) term).value);
		}
		
		throw new IllegalArgumentException("Cannot convert " + term + " to functor");
	}
	
	/**
	 * Returns whether this functor is used for flow-control
	 * purpose. Such functors are (,), (;), (->), (:).
	 */
	public boolean isFlowControl() {
		return this.equals(Operator.COMMA.getFunctor())
			|| this.equals(Operator.SEMICOLON.getFunctor())
			|| this.equals(Operator.IF_THEN.getFunctor())
			|| this.equals(Operator.COLON.getFunctor())
			|| this.equals(NOT);
	}

}
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ee.pri.rl.llj.common.IntGenerator;
import ee.pri.rl.llj.common.term.visitor.TermVisitor;

/**
 * Represents compound structure.
 * 
 * @author Raivo Laanemets
 */
public final class Struct implements Term, Callable, Fact {
	private static final long serialVersionUID = 1L;
	
	public final String name;
	public final Term[] args;
	
	public Struct(String name, Term... args) {
		this.name = name;
		this.args = args;
		//this.functor = new Functor(name, args.length);
	}
	
	public Struct(String name, List<? extends Term> args) {
		this(name, args.toArray(new Term[args.size()]));
	}
	
	public Functor functor() {
		return new Functor(name, args.length);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(StringAtom.quoteAndEscapeString(name)).append('(');
		boolean first = true;
		for (Term arg : args) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}
			builder.append(arg);
		}
		builder.append(')');
		
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(args) ^ name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Struct other = (Struct) obj;
		if (!Arrays.equals(args, other.args)) {
			return false;
		}
		if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public void visitWith(TermVisitor visitor) {
		visitor.visitStruct(this);
	}
	
	/**
	 * Returns the first argument of this structure.
	 */
	public Term first() {
		return args[0];
	}
	
	/**
	 * Returns the second argument of this structure.
	 */
	public Term second() {
		return args[1];
	}
	
	/**
	 * Returns the third argument of this structure.
	 */
	public Term third() {
		return args[2];
	}
	
	/**
	 * Returns the fourth argument of this structure.
	 */
	public Term fourth() {
		return args[3];
	}
	
	/**
	 * Returns the i-th argument of this structure. 
	 */
	public Term arg(int i) {
		if (i < 0 || i >= args.length) {
			throw new IllegalArgumentException("Invalid argument index");
		}
		
		return args[i];
	}
	
	/**
	 * Returns the arity of this structure.
	 */
	public int getArity() {
		return args.length;
	}
	
	/**
	 * Returns the arguments of this structure as a list of terms.
	 */
	public List<Term> getArgList() {
		return Arrays.asList(args);
	}
	
	/**
	 * Returns true if the structure has single argument.
	 */
	public boolean isUnary() {
		return args.length == 1;
	}
	
	/**
	 * Returns true if the structure has 2 arguments.
	 */
	public boolean isBinary() {
		return args.length == 2;
	}

	@Override
	public boolean isGround() {
		for (Term arg : args) {
			if (!arg.isGround()) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Set<Variable> getVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		
		for (Term term : args) {
			variables.addAll(term.getVariables());
		}
		
		return variables;
	}
	
	@Override
	public Set<Variable> getNamedVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		
		for (Term term : args) {
			variables.addAll(term.getNamedVariables());
		}
		
		return variables;
	}

	@Override
	public Term replaceAnonVariables(IntGenerator intGenerator) {
		Term[] args = new Term[this.args.length];
		
		for (int i = 0; i < args.length; i++) {
			args[i] = this.args[i].replaceAnonVariables(intGenerator);
		}
		
		return new Struct(name, args);
	}

	@Override
	public Functor getFunctor() {
		return functor();
	}

	@Override
	public Struct toStruct() {
		return this;
	}
	
	/**
	 * Returns the argument in the case of struct. Returns 0-ary
	 * struct in the case for atom. Throws exception for
	 * any other case.
	 */
	public static Struct toStruct(Term term) {
		if (term instanceof Struct) {
			return (Struct) term;
		}
		
		if (term instanceof StringAtom) {
			return new Struct(((StringAtom) term).value.toString(), new Term[0]);
		}
		
		throw new RuntimeException("Cannot convert term " + term + " into structure"); 
	}

}

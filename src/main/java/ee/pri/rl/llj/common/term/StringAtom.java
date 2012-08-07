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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ee.pri.rl.llj.common.IntGenerator;
import ee.pri.rl.llj.common.term.visitor.TermVisitor;

/**
 * Represents non-numerical atom.
 */
public final class StringAtom implements Term, Single, Callable, Fact {
	private static final long serialVersionUID = 1L;
	
	public final String value;
	
	public StringAtom(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return quoteAndEscapeString(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == StringAtom.class && ((StringAtom) obj).value.equals(value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	/**
	 * Quotes and escapes all strings in the given list.
	 */
	public static List<String> quoteAndEscapeStrings(List<String> input) {
		List<String> ret = new LinkedList<String>();
		
		for (String inString : input) {
			ret.add(quoteAndEscapeString(inString));
		}
		
		return ret;
	}
	
	/**
	 * Return string that is properly escaped and quoted (when necessary)
	 */
	public static String quoteAndEscapeString(String input) {
		if ("[]".equals(input)) {
			return "[]";
		}
		
		StringBuilder builder = new StringBuilder();
		boolean requiresQuoting = false;
		
		if (input.length() > 0) {
			char ch = input.charAt(0);
			if (!Character.isLowerCase(ch)) {
				requiresQuoting = true;
			}
		}
		
		for (char ch : input.toCharArray()) {
			if (Character.isLetter(ch) || Character.isDigit(ch)) {
				builder.append(ch);
			} else if (ch == '_' || ch == '@') {
				builder.append(ch);
			} else if (ch == '\\') {
				builder.append("\\\\");
				requiresQuoting = true;
			} else if (ch == '\n') {
				builder.append("\\n");
				requiresQuoting = true;
			} else if (ch == '\r') {
				builder.append("\\r");
				requiresQuoting = true;
			} else if (ch == '\t') {
				builder.append("\\t");
				requiresQuoting = true;
			} else if (ch == '"') {
				builder.append('"');
				requiresQuoting = true;
			} else {
				builder.append(ch);
				requiresQuoting = true;
			}
		}
		
		if (requiresQuoting) {
			builder.insert(0, '"');
			builder.append('"');
		}
		
		return builder.toString();
	}

	@Override
	public void visitWith(TermVisitor visitor) {
		visitor.visitStringAtom(this);
	}

	@Override
	public boolean isGround() {
		return true;
	}

	@Override
	public Set<Variable> getVariables() {
		return new HashSet<Variable>();
	}

	
	@Override
	public Set<Variable> getNamedVariables() {
		return new HashSet<Variable>();
	}

	@Override
	public Term replaceAnonVariables(IntGenerator intGenerator) {
		return this;
	}

	@Override
	public Functor getFunctor() {
		return new Functor(value);
	}
	
	/**
	 * Converts this StringAtom to 0-ary Struct.
	 */
	@Override
	public Struct toStruct() {
		return new Struct(value);
	}

}

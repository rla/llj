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
package ee.pri.rl.llj.frontend.pass2;

import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.term.Term;

/**
 * Term from parsed raw module. Contains also the position of the term.
 * 
 * @author Raivo Laanemets
 */
public final class ParsedTerm {
	public final Term term;
	public final int lineNumber;
	
	public ParsedTerm(Term term) {
		this(term, LLJException.UNKNOWN_LINE);
	}
	
	public ParsedTerm(Term term, int lineNumber) {
		this.term = term;
		this.lineNumber = lineNumber;
	}
	
}

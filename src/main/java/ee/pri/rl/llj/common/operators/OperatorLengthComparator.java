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
package ee.pri.rl.llj.common.operators;

import java.util.Comparator;


/**
 * Comparator for operators for sorting them by their lenght. Used
 * in the tokenizer to recognize correctly operators like ':' and ':-'
 * (tokenizer must check for longer operators first, otherwise ':-'
 * gets recognized as two tokens ':' and '-').
 * 
 * @author Raivo Laanemets
 */
public final class OperatorLengthComparator implements Comparator<Operator> {

	@Override
	public int compare(Operator o1, Operator o2) {
		return o1.getName().length() == o2.getName().length() ?
			0 : (o1.getName().length() > o2.getName().length() ? -1 : 1);
	}

}

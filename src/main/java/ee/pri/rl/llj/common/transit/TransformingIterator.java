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
package ee.pri.rl.llj.common.transit;

import java.util.Iterator;

import ee.pri.rl.llj.common.NonRemovableItemIterator;

/**
 * Class for lazy list element transforming. Lazy lists are
 * represented as iterators.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 *
 * @param <T1> Type of the input list.
 * @param <T2> Type of the output list.
 */
public abstract class TransformingIterator<T1, T2> extends NonRemovableItemIterator<T2> {

	private Iterator<T1> input;

	/**
	 * Creates a new list transform for the input iterator.
	 * 
	 * @param input The input iterator.
	 */
	public TransformingIterator(Iterator<T1> input) {
		this.input = input;
	}

	/**
	 * Returns the input iterator.
	 */
	public Iterator<T1> getInput() {
		return input;
	}
	
}

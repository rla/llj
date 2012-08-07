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

import java.util.Iterator;

/**
 * Primitive extension of {@link Iterator} that does not permit
 * the removal of current element.
 * 
 * @author Raivo Laanemets
 *
 * @param <E> Type of the element.
 * @since 1.1
 */
public abstract class NonRemovableItemIterator<E> implements Iterator<E> {

	/**
	 * Will throw {@link UnsupportedOperationException} with the message
	 * that this iterator does not support removal of the current element.
	 */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException("Cannot remove elements from " + getClass());
	}

}

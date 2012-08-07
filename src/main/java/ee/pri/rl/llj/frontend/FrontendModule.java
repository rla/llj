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
package ee.pri.rl.llj.frontend;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import ee.pri.rl.llj.common.NonRemovableItemIterator;
import ee.pri.rl.llj.frontend.pass1.Token;
import ee.pri.rl.llj.frontend.pass1.Tokenizer;
import ee.pri.rl.llj.frontend.pass2.ParsedTerm;
import ee.pri.rl.llj.frontend.pass2.Parser;

/**
 * Interface for LLJ frontend. It reads and processes
 * the source code lazily.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 */
public class FrontendModule extends NonRemovableItemIterator<ParsedTerm> implements Closeable {
	private Reader reader;
	private Iterator<ParsedTerm> terms;
	
	public FrontendModule(Reader reader, FrontendConfiguration configuration) {
		this.reader = reader;
		Iterator<Token> tokenizer = new Tokenizer(reader);
		terms = new Parser(tokenizer, configuration.getTermFactory());
	}

	/**
	 * Call this to close the underlying Reader.
	 */
	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public boolean hasNext() {
		return terms.hasNext();
	}

	@Override
	public ParsedTerm next() {
		return terms.next();
	}
}

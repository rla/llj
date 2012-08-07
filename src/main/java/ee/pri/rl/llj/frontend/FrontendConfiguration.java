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

import ee.pri.rl.llj.frontend.loader.ClassPathModuleLoader;
import ee.pri.rl.llj.frontend.loader.ModuleLoader;
import ee.pri.rl.llj.frontend.pass1.CachingTokenFactory;
import ee.pri.rl.llj.frontend.pass1.TokenFactory;
import ee.pri.rl.llj.frontend.pass2.SimpleTermFactory;
import ee.pri.rl.llj.frontend.pass2.TermFactory;

public class FrontendConfiguration {
	private TokenFactory tokenFactory;
	private TermFactory termFactory;
	private ModuleLoader sourceLoader;
	
	public FrontendConfiguration(
			TokenFactory tokenFactory,
			TermFactory termFactory,
			ModuleLoader sourceLoader) {
		
		this.termFactory = termFactory;
		this.tokenFactory = tokenFactory;
		this.sourceLoader = sourceLoader;
	}
	
	public FrontendConfiguration() {
		this(
			new CachingTokenFactory(),
			new SimpleTermFactory(),
			new ClassPathModuleLoader()
		);
	}

	public TermFactory getTermFactory() {
		return termFactory;
	}

	public void setTermFactory(TermFactory termFactory) {
		this.termFactory = termFactory;
	}

	public TokenFactory getTokenFactory() {
		return tokenFactory;
	}

	public void setTokenFactory(TokenFactory tokenFactory) {
		this.tokenFactory = tokenFactory;
	}

	public ModuleLoader getSourceLoader() {
		return sourceLoader;
	}

	public void setSourceLoader(ModuleLoader sourceLoader) {
		this.sourceLoader = sourceLoader;
	}
	
}

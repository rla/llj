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
package ee.pri.rl.llj.backend;

import java.util.ArrayList;
import java.util.List;

import ee.pri.rl.llj.backend.program.call.BackendCallFactory;
import ee.pri.rl.llj.backend.runtime.RuntimeQueryFactory;
import ee.pri.rl.llj.backend.runtime.loader.BackendModuleLoader;
import ee.pri.rl.llj.backend.runtime.loader.SingleThreadModuleLoader;
import ee.pri.rl.llj.common.ModulePath;
import ee.pri.rl.llj.frontend.FrontendConfiguration;
import ee.pri.rl.llj.frontend.LLJFrontend;

public class BackendConfiguration {
	private BackendModuleLoader loader;
	private RuntimeQueryFactory queryFactory;
	private BackendCallFactory callFactory;
	private boolean globalNamespace;
	private boolean namingConflict;
	private List<ModulePath> autoImports = new ArrayList<ModulePath>();
	
	public BackendConfiguration(
			BackendModuleLoader loader,
			RuntimeQueryFactory queryFactory,
			BackendCallFactory callFactory,
			boolean globalNamespace,
			boolean namingConflict) {
		
		this.loader = loader;
		this.queryFactory = queryFactory;
		this.callFactory = callFactory;
		this.globalNamespace = globalNamespace;
		this.namingConflict = namingConflict;
	}
	
	public BackendConfiguration(FrontendConfiguration frontendConfiguration) {
		this(
			new SingleThreadModuleLoader(new LLJFrontend(frontendConfiguration)),
			new RuntimeQueryFactory(),
			new BackendCallFactory(),
			false,
			true
		);
	}

	public BackendModuleLoader getLoader() {
		return loader;
	}

	public RuntimeQueryFactory getQueryFactory() {
		return queryFactory;
	}

	public BackendCallFactory getCallFactory() {
		return callFactory;
	}

	public boolean isGlobalNamespace() {
		return globalNamespace;
	}

	public void setGlobalNamespace(boolean globalNamespace) {
		this.globalNamespace = globalNamespace;
	}

	public void setLoader(BackendModuleLoader loader) {
		this.loader = loader;
	}

	public void setQueryFactory(RuntimeQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	public boolean isNamingConflict() {
		return namingConflict;
	}

	public void setNamingConflict(boolean namingConflict) {
		this.namingConflict = namingConflict;
	}

	public List<ModulePath> getAutoImports() {
		return autoImports;
	}

	public void setAutoImports(List<ModulePath> autoImports) {
		this.autoImports = autoImports;
	}
	
}

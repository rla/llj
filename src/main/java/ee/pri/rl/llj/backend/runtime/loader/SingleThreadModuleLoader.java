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
package ee.pri.rl.llj.backend.runtime.loader;

import java.util.HashMap;
import java.util.Map;

import ee.pri.rl.llj.backend.BackendConfiguration;
import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.ModulePath;
import ee.pri.rl.llj.frontend.LLJFrontend;

/**
 * Backend module loader that uses only a single thread.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 */
public class SingleThreadModuleLoader extends BackendModuleLoader {
	private static final long serialVersionUID = 1L;

	public SingleThreadModuleLoader(LLJFrontend frontend) {
		super(frontend);
	}

	@Override
	public Map<ModulePath, BackendModule> load(ModulePath path, BackendConfiguration configuration, LLJContext context) throws LLJException {
		Map<ModulePath, BackendModule> modules = new HashMap<ModulePath, BackendModule>();
		loadModules(path, modules, configuration, context);
		
		return modules;
	}

	private void loadModules(ModulePath path, Map<ModulePath, BackendModule> modules, BackendConfiguration configuration, LLJContext context) throws LLJException {
		if (!modules.containsKey(path)) {
			BackendModule backendModule;
			try {
				backendModule = loadModule(path, configuration, context);
				modules.put(path, backendModule);
				
				for (ModulePath importPath : backendModule.getImports()) {
					loadModules(importPath, modules, configuration, context);
				}
			} catch (Exception e) {
				throw new LLJException("Loading modules failed", e);
			}
		}
	}

}

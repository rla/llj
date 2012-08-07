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
package ee.pri.rl.llj.backend.runtime;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ee.pri.rl.llj.backend.BackendConfiguration;
import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.LLJRuntimeException;
import ee.pri.rl.llj.common.ModulePath;
import ee.pri.rl.llj.frontend.FrontendConfiguration;

/**
 * Context for a single LLJ program. Consists of processed
 * backend modules.
 * 
 * @author Raivo Laanemets
 * @since 1.0
 */
public class LLJContext implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ModulePath main;
	private Map<ModulePath, BackendModule> modules;
	private BackendConfiguration configuration;
	
	/**
	 * Global variables, accessed through llj:global module.
	 */
	private Map<String, Object> globals = new ConcurrentHashMap<String, Object>();
	
	/**
	 * Field to show whether the modules in the context are linked or not.
	 */
	private boolean resolved;
	
	/**
	 * Creates a new LLJ program context.
	 * 
	 * @param pathString Path of the main module.
	 * @param configuration Backend configuration.
	 */
	public LLJContext(String pathString, BackendConfiguration configuration) throws LLJException {
		this.main = new ModulePath(pathString);
		this.configuration = configuration;
		
		Map<ModulePath, BackendModule> modules = configuration.getLoader().load(main, configuration, this);
		
		if (configuration.isGlobalNamespace()) {
			BackendModule mainModule = mergeToGlobal(modules.values(), main);
			this.modules = Collections.singletonMap(main, mainModule);
		} else {
			this.modules = modules;
		}
	}
	
	/**
	 * Creates a new LLJ program context with the
	 * default backend configuration.
	 * 
	 * @param pathString Path of the main module.
	 * @param frontendConfiguration Frontend configuration.
	 */
	public LLJContext(String pathString, FrontendConfiguration frontendConfiguration) throws LLJException {
		this(pathString, new BackendConfiguration(frontendConfiguration));
	}
	
	/**
	 * Creates a new LLJ program context with the
	 * default frontend and backend configuration.
	 * 
	 * @param pathString Path of the main module.
	 */
	public LLJContext(String pathString) throws LLJException {
		this(pathString, new FrontendConfiguration());
	}
	
	/**
	 * Creates new LLJ query. This is actually delegated
	 * to {@link RuntimeQueryFactory} that can be set through
	 * {@link BackendConfiguration} for this LLJ context. If the
	 * predicates calls are not yet resolved it is done automatically.
	 * (This is thread safe).
	 * 
	 * @param queryString Query.
	 */
	public RuntimeQuery createQuery(String queryString) throws LLJException {
		resolve();
		return configuration.getQueryFactory().createQuery(queryString, this);
	}
	
	/**
	 * Automatically resolves predicate calls in modules.
	 * Resolving avoids dynamic lookup of predicates
	 * (as was done in XProlog using HashMap).
	 */
	private synchronized void resolve() throws LLJException {
		if (!resolved) {
			resolveModules();
			resolved = true;
		}
	}

	private void resolveModules() throws LLJException {
		for (BackendModule module : modules.values()) {
			module.resolve(this);
		}
	}
	
	/**
	 * Returns the main module of this context.
	 */
	public BackendModule getMainModule() {
		return modules.get(main);
	}
	
	/**
	 * Returns the module with the given path. If the
	 * module does not exist, this will throw an exception.
	 * 
	 * @param path Path of the module.
	 */
	public BackendModule getModule(ModulePath path) {
		if (modules.containsKey(path)) {
			return modules.get(path);
		} else {
			throw new LLJRuntimeException("Module " + path + " does not exist");
		}
	}
	
	/**
	 * Returns the collection of all modules in this context.
	 */
	public Collection<BackendModule> getModules() {
		return modules.values();
	}

	/**
	 * Returns the backend configuration of this context.
	 */
	public BackendConfiguration getConfiguration() {
		return configuration;
	}
	
	/**
	 * Merges the given collection of modules into one large
	 * global module.
	 * 
	 * @param modules The collection of modules.
	 * @param main The name of the new global module.
	 */
	private BackendModule mergeToGlobal(Collection<BackendModule> modules, ModulePath main) {
		BackendModule global = new BackendModule(main, this);
		for (BackendModule module : modules) {
			global.merge(module, configuration);
		}
		
		return global;
	}

	/**
	 * Returns global term with the given key. The method
	 * is thread-safe.
	 */
	public Object getGlobal(String key) {
		return RuntimeVariable.copyTerm(globals.get(key));
	}
	
	/**
	 * Sets the global term with the given key. Old value
	 * will be overwritten. <code>copy_term</code> is applied to
	 * the given term. The method is thread-safe.
	 */
	public void setGlobal(String key, Object term) {
		globals.put(key, RuntimeVariable.copyTerm(term));
	}

}

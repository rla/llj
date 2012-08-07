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

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ee.pri.rl.llj.backend.BackendConfiguration;
import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.ModulePath;
import ee.pri.rl.llj.common.java.JavaMethodPredicate;
import ee.pri.rl.llj.common.java.JavaModule;
import ee.pri.rl.llj.common.java.Predicate;
import ee.pri.rl.llj.common.term.Functor;
import ee.pri.rl.llj.frontend.FrontendModule;
import ee.pri.rl.llj.frontend.LLJFrontend;
import ee.pri.rl.llj.frontend.loader.ModuleNotFoundException;

/**
 * Class for loading backend modules.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 */
public abstract class BackendModuleLoader implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LLJFrontend frontend;

	/**
	 * @param frontend LLJ frontend that is used for loading frontend part of the modules.
	 */
	public BackendModuleLoader(LLJFrontend frontend) {
		this.frontend = frontend;
	}

	/**
	 * Loads all modules starting from main.
	 * 
	 * @param main The main module.
	 * @param configuration The backend configuration.
	 * @throws LLJException When module loading fails.
	 */
	public abstract Map<ModulePath, BackendModule> load(
			ModulePath main, BackendConfiguration configuration, LLJContext context) throws LLJException;
	
	/**
	 * Loads the LLJ module of the given path.
	 * At first it tries to load normal module. If the normal
	 * module is not found then it tries to load module defined as
	 * a Java class. 
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	protected BackendModule loadModule(ModulePath path, BackendConfiguration configuration, LLJContext context) throws Exception {
		try {
			return loadLLJModule(path, configuration, context);
		} catch (ModuleNotFoundException e) {
			return loadJavaModule(path, context);
		} catch (LLJException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Loads LLJ module of the given path.
	 * 
	 * @param path Path of the module.
	 * 
	 * @throws LLJException When frontend module processing fails.
	 * @throws ModuleNotFoundException When the module is not found.
	 * @throws IOException When the module reading gives an error.
	 */
	protected BackendModule loadLLJModule(ModulePath path, BackendConfiguration configuration, LLJContext context) throws LLJException, ModuleNotFoundException, IOException {
		FrontendModule module = frontend.loadModule(path);
		BackendModule backendModule = new BackendModule(module, context);
		module.close();
		
		for (ModulePath autoImport : configuration.getAutoImports()) {
			backendModule.getImports().add(autoImport);
		}
		
		return backendModule;
	}
	
	/**
	 * Helper method to load module that is defined as a Java class
	 * with methods annotated with the {@link Predicate} annotation.
	 * 
	 * @param path Path of the module.
	 */
	protected BackendModule loadJavaModule(ModulePath path, LLJContext context) throws Exception {
		String className = JavaMethodPredicate.pathToClassName(path);
		
		try {
			List<JavaMethodPredicate> predicates = new LinkedList<JavaMethodPredicate>();
			Class<?> clazz = Class.forName(className);
			JavaModule javaModule = (JavaModule) clazz.getConstructor().newInstance();
			for (Method method : clazz.getMethods()) {
				for (Annotation annotation : method.getAnnotations()) {
					if (annotation instanceof Predicate) {
						Predicate predAnnot = (Predicate) annotation;
						String name = predAnnot.name();
						JavaMethodPredicate predicate = new JavaMethodPredicate(new Functor(name, predAnnot.arity()), method, javaModule);
						predicates.add(predicate);
						break;
					}
				}
			}
			
			return new BackendModule(path, context, predicates);
		} catch (Exception e) {
			throw e;
		}
	}
	
}

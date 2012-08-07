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
package ee.pri.rl.llj.backend.program;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ee.pri.rl.llj.backend.BackendConfiguration;
import ee.pri.rl.llj.backend.program.call.BackendAbstractCall;
import ee.pri.rl.llj.backend.program.call.BackendDynamicCall;
import ee.pri.rl.llj.backend.program.call.BackendJavaMethodCall;
import ee.pri.rl.llj.backend.program.call.BackendPredicateCall;
import ee.pri.rl.llj.backend.program.predicate.AssertStorage;
import ee.pri.rl.llj.backend.program.predicate.StaticBackendPredicate;
import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.ModulePath;
import ee.pri.rl.llj.common.java.JavaMethodPredicate;
import ee.pri.rl.llj.common.operators.Operator;
import ee.pri.rl.llj.common.term.Functor;
import ee.pri.rl.llj.common.term.Struct;
import ee.pri.rl.llj.frontend.pass2.ParsedTerm;

public class BackendModule implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Map<Functor, List<BackendDefinition>> predicates = new HashMap<Functor, List<BackendDefinition>>();
	private final Map<Functor, StaticBackendPredicate> compiledPredicates = new HashMap<Functor, StaticBackendPredicate>();
	private final Map<Functor, JavaMethodPredicate> javaMethodPredicates = new HashMap<Functor, JavaMethodPredicate>();
	private final Set<ModulePath> imports = new HashSet<ModulePath>();
	private final Set<Functor> exports = new HashSet<Functor>();
	private final Set<Functor> moduleTransparents = new HashSet<Functor>();
	private final Map<Functor, AssertStorage> dynamicPredicates = new HashMap<Functor, AssertStorage>();
	private final LLJContext context;
	private ModulePath path;
	
	public BackendModule(Iterator<ParsedTerm> terms, LLJContext context) throws LLJException {
		this.context = context;
		
		ModuleDeclaration declaration = new ModuleDeclaration(terms.next().term);
		path = declaration.getPath();
		exports.addAll(declaration.getExports());
		
		addAll(terms);
	}
	
	public BackendModule(ModulePath path, LLJContext context) {
		this.path = path;
		this.context = context;
	}
	
	/**
	 * Merges another module into this. During the merge
	 * process all predicates from the given module are
	 * merged into this one.
	 * 
	 * @param module The module to merge into this one.
	 * @param configuration Backend configuration.
	 */
	public void merge(BackendModule module, BackendConfiguration configuration) {
		for (Entry<Functor, List<BackendDefinition>> entry : module.predicates.entrySet()) {
			if (configuration.isNamingConflict() && predicates.containsKey(entry.getKey())) {
				throw new IllegalArgumentException("Name conflict: " + this.path + " and " + module.path + " for " + entry.getKey());
			}
			predicates.put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<Functor, JavaMethodPredicate> entry : module.javaMethodPredicates.entrySet()) {
			if (javaMethodPredicates.containsKey(entry.getKey())
				|| predicates.containsKey(entry.getKey())) {
				
				throw new IllegalArgumentException("Name conflict: " + this.path + " and " + module.path + " for " + entry.getKey());
			}
			javaMethodPredicates.put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Adds all the given normalized terms into this module.
	 * 
	 * @param terms The normalized terms to add.
	 * @throws LLJException When some directive or definition contains an error.
	 */
	public void addAll(Iterator<ParsedTerm> terms) throws LLJException {
		while (terms.hasNext()) {
			ParsedTerm term = terms.next();
			Struct struct = Struct.toStruct(term.term);
			if (Operator.UNARY_COLONMINUS.getFunctor().equals(struct.getFunctor())) {
				addDirective(struct);
			} else {
				addDefinition(struct);
			}
		}
	}
	
	/**
	 * Adds new normalized directive to this module.
	 * 
	 * @param directive The directive to add.
	 * @throws LLJException When the directive is unknown or invalid.
	 */
	public void addDirective(Struct term) throws LLJException {
		Struct call = Struct.toStruct(term.first());
		if (Functor.MODULE_TRANSPARENT.equals(call.functor())) {
			moduleTransparents.add(new ModuleTransparent(call).getFunctor());
		} else if (Functor.IMPORT.equals(call.functor())) {
			try {
				imports.add(new ModulePath(call.first()));
			} catch (LLJException e) {
				throw new LLJException("Invalid import path: " + call.first());
			}
		} else if (Functor.DYNAMIC.equals(call.functor())) {
			dynamicPredicates.put(Functor.fromTerm(call.first()), new AssertStorage());
		} else {
			throw new LLJException("Invalid directive: " + term);
		}
	}
	
	/**
	 * Adds new normalized predicate definition to
	 * this module.
	 * 
	 * @param definition The definition to add.
	 */
	public void addDefinition(Struct term) {
		BackendDefinition definition = new BackendDefinition(term, this);
		Functor functor = definition.functor;
		
		List<BackendDefinition> definitions = predicates.get(functor);
		if (definitions == null) {
			definitions = new ArrayList<BackendDefinition>();
			predicates.put(functor, definitions);
		}
		definitions.add(definition);
	}

	/**
	 * Creates new backend module using the module path and list
	 * of java predicates.
	 * 
	 * @param path The path of the module.
	 * @param predicates List of Java predicates.
	 */
	public BackendModule(ModulePath path, LLJContext context, List<JavaMethodPredicate> predicates) {
		this.path = path;
		this.context = context;
		
		for (JavaMethodPredicate predicate : predicates) {
			javaMethodPredicates.put(predicate.functor, predicate);
			exports.add(predicate.functor);
		}
	}

	public boolean isPredicate(Functor functor) {
		return predicates.containsKey(functor);
	}

	public Set<ModulePath> getImports() {
		return imports;
	}

	public boolean isExported(Functor functor) {
		return exports.contains(functor);
	}

	public ModulePath getPath() {
		return path;
	}

	public void setPath(ModulePath path) {
		this.path = path;
	}

	public void resolve(LLJContext context) throws LLJException {
		for (List<BackendDefinition> list : predicates.values()) {
			for (BackendDefinition def : list) {
				def.resolve(context);
			}
		}
	}

	public boolean isImported(ModulePath path) {
		return imports.contains(path);
	}

	public Set<Functor> getExports() {
		return exports;
	}

	public StaticBackendPredicate getPredicate(Functor functor) throws LLJException {
		if (!predicates.containsKey(functor)) {
			throw new LLJException("Predicate " + functor + " does not exist in " + path);
		}
		StaticBackendPredicate predicate = compiledPredicates.get(functor);
		if (predicate == null) {
			predicate = new StaticBackendPredicate(predicates.get(functor), moduleTransparents.contains(functor), this, functor);
			compiledPredicates.put(functor, predicate);
		}
		
		return predicate;
	}

	public boolean isJavaMethodPredicate(Functor functor) {
		return javaMethodPredicates.containsKey(functor);
	}

	public JavaMethodPredicate getJavaMethodPredicate(Functor functor) {
		return javaMethodPredicates.get(functor);
	}
	
	public boolean isDynamicPredicate(Functor functor) {
		return dynamicPredicates.containsKey(functor);
	}
	
	/**
	 * Checks for call ambiguity.
	 * 
	 * @param functor call functor
	 * @return there is only one callable entity with this functor
	 */
	public boolean isAmbiguous(Functor functor) {
		int a = 0;
		a += javaMethodPredicates.containsKey(functor) ? 1 : 0;
		a += predicates.containsKey(functor) ? 1 : 0;
		a += dynamicPredicates.containsKey(functor) ? 1 : 0;
		
		for (ModulePath path : imports) {
			if (context.getModule(path).isExported(functor)) {
				a++;
			}
		}
		
		return a > 1;
	}
	
	/**
	 * Resolves the given call which is made from this module.
	 * 
	 * @param functor call functor (name/arity)
	 */
	public BackendAbstractCall resolveCall(Functor functor, int[] arguments) throws LLJException {
		if (isAmbiguous(functor)) {
			throw new RuntimeException("Call " + functor + " is ambiguous in module " + path);
		}
		
		BackendAbstractCall local = null;
		if ((local = resolveLocalCall(functor, arguments, this)) != null) {
			return local;
		}
		
		for (ModulePath path : imports) {
			if (context.getModule(path).isExported(functor)) {
				return context.getModule(path).resolveLocalCall(functor, arguments, this);
			}
		}
		
		throw new RuntimeException("Cannot resolve call " + functor + " in module " + path);
	}
	
	/**
	 * Returns callable object for the given functor. Returns
	 * <code>null</code> when such callable does not exist.
	 * 
	 * @see {@link StaticBackendPredicate}
	 * @see {@link JavaMethodPredicate}
	 * @see {@link AssertStorage}
	 */
	public Object findCallable(Functor functor) throws LLJException {
		if (isAmbiguous(functor)) {
			throw new RuntimeException("Call " + functor + " is ambiguous in module " + path);
		}
		
		Object local = null;
		if ((local = findLocalCallable(functor)) != null) {
			return local;
		}
		
		for (ModulePath path : imports) {
			if (context.getModule(path).isExported(functor)) {
				return context.getModule(path).findLocalCallable(functor);
			}
		}
		
		throw new RuntimeException("Cannot resolve call " + functor + " in module " + path);
	}

	/**
	 * Resolves the given call which is made to exported from this
	 * module.
	 * 
	 * @param functor call functor
	 * @return resolved call or <code>null</code> the no matching callable can be found
	 */
	public BackendAbstractCall resolveLocalCall(Functor functor, int[] arguments, BackendModule from) throws LLJException {
		if (predicates.containsKey(functor)) {
			return new BackendPredicateCall(getPredicate(functor), arguments, this);
		}
		
		if (javaMethodPredicates.containsKey(functor)) {
			JavaMethodPredicate jp = javaMethodPredicates.get(functor);
			return new BackendJavaMethodCall(jp.functor, this, arguments, jp.method, jp.javaModule);
		}
		
		if (dynamicPredicates.containsKey(functor)) {
			AssertStorage storage = dynamicPredicates.get(functor);
			return new BackendDynamicCall(this, storage, arguments);
		}
		
		return null;
	}
	
	/**
	 * Returns callable object for the given functor. Returns
	 * <code>null</code> when such callable does not exist.
	 * 
	 * @see {@link StaticBackendPredicate}
	 * @see {@link JavaMethodPredicate}
	 * @see {@link AssertStorage}
	 */
	public Object findLocalCallable(Functor functor) {
		if (predicates.containsKey(functor)) {
			return predicates.get(functor);
		}
		
		if (javaMethodPredicates.containsKey(functor)) {
			return javaMethodPredicates.get(functor);
		}
		
		if (dynamicPredicates.containsKey(functor)) {
			return dynamicPredicates.get(functor);
		}
		
		return null;
	}
	
	/**
	 * Returns dynamic storage for given term functor.
	 * User for assert/retract. Returns <code>null</code>
	 * if there is no such dynamic predicate. 
	 * 
	 * @param functor the term functor
	 */
	public AssertStorage getDynamicStorage(Functor functor) {
		return dynamicPredicates.get(functor);
	}

	/**
	 * Includes normal predicates only.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (StaticBackendPredicate p : compiledPredicates.values()) {
			builder.append(p).append('\n');
		}
		
		return builder.toString();
	}
	
}

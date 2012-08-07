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
package ee.pri.rl.llj.common.java;

import java.lang.reflect.Method;

import ee.pri.rl.llj.common.ModulePath;
import ee.pri.rl.llj.common.term.Functor;

// FIXME find better package
public class JavaMethodPredicate {
	public final Functor functor;
	public final Method method;
	public final JavaModule javaModule;
	
	public JavaMethodPredicate(Functor functor, Method method, JavaModule javaModule) {
		this.functor = functor;
		this.method = method;
		this.javaModule = javaModule;
	}
	
	/**
	 * Converts short classname into module name.
	 * For example, TermUtil will be converted to term_util.
	 */
	public static String classNameToModuleName(String className) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < className.length(); i++) {
			char ch = className.charAt(i);
			if (Character.isUpperCase(ch)) {
				if (i > 0) {
					builder.append('_').append(Character.toLowerCase(ch));
				} else {
					builder.append(Character.toLowerCase(ch));
				}
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Converts module name into short classname.
	 * For example, term_util will be converted to TermUtil.
	 */
	public static String moduleNameToClassName(String moduleName) {
		StringBuilder builder = new StringBuilder();
		
		int i = 0;
		while (i < moduleName.length()) {
			char ch = moduleName.charAt(i);
			if (ch == '_') {
				i++;
				builder.append(Character.toUpperCase(moduleName.charAt(i)));
			} else {
				if (i == 0) {
					builder.append(Character.toUpperCase(ch));
				} else {
					builder.append(ch);
				}
			}
			i++;
		}
		
		return builder.toString();
	}
	
	public static String pathToClassName(ModulePath path) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		int size = path.path.size();
		for (int i = 0; i < size; i++) {
			String pathToken = path.path.get(i);
			if (first) {
				first = false;
			} else {
				builder.append('.');
			}
			if (i == size - 1) {
				pathToken = moduleNameToClassName(path.path.get(i));
			}
			builder.append(pathToken);
		}
		
		return builder.toString();
	}
	
}

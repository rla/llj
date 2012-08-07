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
package ee.pri.rl.llj.common.log;

/**
 * Very basic logging facade without any dependencies
 * on logging frameworks.
 * 
 * XXX This should actually use log4j or something like that internally.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 */
public class LLJLogger {
	private static boolean debugEnabledGlobal = false;
	
	private String name;
	
	public LLJLogger(String name) {
		this.name = name;
	}

	public boolean isDebugEnabled() {
		return debugEnabledGlobal;
	}
	
	public void debug(Object data) {
		if (debugEnabledGlobal) {
			System.out.println(name + ":" + data.toString());
		}
	}
	
	public static void enableDebug() {
		debugEnabledGlobal = true;
	}

	public static LLJLogger getLogger(Class<?> clazz) {
		return new LLJLogger(clazz.getSimpleName());
	}
}

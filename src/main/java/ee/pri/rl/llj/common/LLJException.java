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

/**
 * Base class for LLJ exceptions.
 */
public class LLJException extends Exception {
	private static final long serialVersionUID = 1L;
	public static final int UNKNOWN_LINE = -1;
	
	/**
	 * Line number (if available) for an error.
	 */
	private int lineNumber = UNKNOWN_LINE;
	private ModulePath module = null;

	public LLJException() {
		super();
	}

	public LLJException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LLJException(String message, Throwable cause, int lineNumber) {
		super(message, cause);
		this.lineNumber = lineNumber;
	}
	
	public LLJException(String message, int lineNumber) {
		super(message);
		this.lineNumber = lineNumber;
	}
	
	public LLJException(String message, int lineNumber, ModulePath module) {
		super(message);
		this.lineNumber = lineNumber;
		this.module = module;
	}

	public LLJException(String message) {
		super(message);
	}

	public LLJException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		String message = super.getMessage();
		
		if (UNKNOWN_LINE != lineNumber) {
			message = "Error on line " + lineNumber + ": " + message;
		}
		
		if (module != null) {
			message = message + " in module " + module;
		}
		
		return message;
	}

}

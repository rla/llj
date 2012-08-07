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
package llj;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.String;
import java.text.MessageFormat;

import ee.pri.rl.llj.backend.runtime.RuntimeListStruct;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.JavaCallGoal;
import ee.pri.rl.llj.common.FileUtils;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.java.JavaModule;
import ee.pri.rl.llj.common.java.Predicate;

public class Io implements JavaModule {
	
	@Predicate(name="is_link", arity = 1)
	public AbstractGoal isLink1(JavaCallGoal goal) throws Exception {
		File file = goal.getObject(0, File.class);
		try {
			if (!file.getAbsolutePath().equals(file.getCanonicalPath())) {
				return goal.G;
			} else {
				return goal.Q.backtrack();
			}
		} catch (IOException e) {
			throw new LLJException("IO exception", e);
		}
	}
	
	@Predicate(name="is_dir", arity = 1)
	public AbstractGoal isDir1(JavaCallGoal goal) throws Exception {
		File file = goal.getObject(0, File.class);
		if (file.isDirectory()) {
			return goal.G;
		} else {
			return goal.Q.backtrack();
		}
	}
	
	@Predicate(name="list_files", arity = 2)
	public AbstractGoal listFiles2(JavaCallGoal goal) throws Exception {
		File file = goal.getObject(0, File.class);
		goal.setVar(1, RuntimeListStruct.fromArray(file.listFiles()));
		
		return goal.G;
	}
	
	@Predicate(name="file", arity = 2)
	public AbstractGoal file2(JavaCallGoal goal) throws Exception {
		goal.setVar(1, new File(goal.getStringArg(0)));
		
		return goal.G;
	}
	
	@Predicate(name="read_string", arity = 2)
	public AbstractGoal read(JavaCallGoal goal) throws Exception {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(goal.getStringArg(0))));
			goal.setVar(1, FileUtils.readString(reader));
		} catch (Exception e) {
			throw new LLJException("Cannot read file", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new LLJException("Cannot close file", e);
				}
			}
		}
		
		return goal.G;
	}
	
	@Predicate(name="ln", arity = 2)
	public AbstractGoal ln2(JavaCallGoal goal) throws Exception {
		OutputStream out = (OutputStream) goal.getArg(0);
		try {
			out.write("\n".getBytes());
		} catch (IOException e) {
			throw new LLJException("Cannot write to stream", e);
		}
		
		return goal.G;
	}
	
	@Predicate(name="ln", arity = 1)
	public AbstractGoal ln1(JavaCallGoal goal) throws Exception {
		System.out.println();
		
		return goal.G;
	}
	
	@Predicate(name="writeln", arity = 2)
	public AbstractGoal writeln2(JavaCallGoal goal) throws Exception {
		OutputStream out = (OutputStream) goal.getArg(0);
		try {
			out.write(goal.getArg(1).toString().concat("\n").getBytes());
		} catch (IOException e) {
			throw new LLJException("Cannot write to stream", e);
		}
		
		return goal.G;
	}
	
	@Predicate(name="writeln", arity = 1)
	public AbstractGoal writeln1(JavaCallGoal goal) throws Exception {
		System.out.println(goal.getArg(0));
		
		return goal.G;
	}
	
	@Predicate(name="write", arity = 2)
	public AbstractGoal write2(JavaCallGoal goal) throws Exception {
		OutputStream out = (OutputStream) goal.getArg(0);
		try {
			out.write(goal.getArg(1).toString().getBytes());
		} catch (IOException e) {
			throw new LLJException("Cannot write to stream", e);
		}
		
		return goal.G;
	}
	
	@Predicate(name="format", arity = 2)
	public AbstractGoal format(JavaCallGoal goal) throws Exception {
		Object[] args = goal.getListArg(1).toArray();
		System.out.print(MessageFormat.format(goal.getStringArg(0), args));
		
		return goal.G;
	}
	
	@Predicate(name="writef", arity = 2)
	public AbstractGoal writef(JavaCallGoal goal) throws Exception {
		System.out.printf(goal.getStringArg(0), goal.getListArg(1).toArray());
		
		return goal.G;
	}
	
	@Predicate(name="write", arity = 1)
	public AbstractGoal write1(JavaCallGoal goal) throws Exception {
		System.out.print(goal.getArg(0));
		
		return goal.G;
	}
	
	@Predicate(name="close", arity = 1)
	public AbstractGoal close(JavaCallGoal goal) throws Exception {
		Closeable closeable = (Closeable) goal.getArg(0);
		try {
			closeable.close();
		} catch (IOException e) {
			throw new LLJException("Cannot close " + closeable, e);
		}
		
		return goal.G;
	}
	
	@Predicate(name="open", arity = 3)
	public AbstractGoal open(JavaCallGoal goal) throws Exception {
		String name = (String) goal.getArg(0);
		String mode = (String) goal.getArg(1);

		try {
			if ("write".equals(mode)) {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(name)));
				goal.setVar(2, stream);
			} else if ("append".equals(mode)) {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(name), true));
				goal.setVar(2, stream);
			} else if ("read".equals(mode)) {
				BufferedInputStream stream = new BufferedInputStream(new FileInputStream(new File(name)));
				goal.setVar(2, stream);
			} else {
				throw new LLJException("Invalid file mode while opening " + name);
			}
		} catch (IOException e) {
			throw new LLJException("Cannot open file", e);
		}

		return goal.G;
	}
	
	
}

/*
Copyright 2008 Raivo Laanemets

This file is part of rl-prolog.

rl-prolog is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

rl-prolog is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with rl-prolog.  If not, see <http://www.gnu.org/licenses/>.
*/

package ee.pri.rl.llj.cli;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import jline.ConsoleReader;
import jline.History;
import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;
import ee.pri.rl.llj.common.LLJException;

/**
 * Interactive top-level shell for developing and running
 * LLJ programs.
 */
public class Main {
	private LLJContext context;

	public static void main(String[] args) throws IOException {
		new Main().startShell();
	}
	
	private void startShell() throws IOException {
		ConsoleReader reader = new ConsoleReader();
		reader.setHistory(new History(new File(".jline_history")));
		
		String line = null;
        while ((line = reader.readLine("?- ")) != null) {
        	if (doLine(reader, line)) {
        		break;
        	}
        }
	}
	
	public boolean doLine(ConsoleReader reader, String line) throws IOException {
    	line = line.trim();
    	
    	if (line.startsWith("[") && line.endsWith("].")) {
    		try {
    			String path = line.substring(1, line.length() - 2);
    			context = new LLJContext(path);
    		} catch (LLJException e) {
    			printError(e);
    		}
    		return false;
    	}
    	
        if (line.equalsIgnoreCase("halt.")) {
            return true;
        }
        
        if (!line.endsWith(".")) {
        	System.out.println("Please end command with '.'");
        	return false;
        }
        
        try {
        	line = line.substring(0, line.length() - 1);
			RuntimeQuery query = context.createQuery(line);
			if (query.execute()) {
				System.out.println("Yes.");
				for (Entry<String, RuntimeVariable> var : query.getBindingMap().entrySet()) {
					System.out.println(var.getKey() + "=" + RuntimeVariable.deref(var.getValue()));
				}
				while (true) {
					System.out.print("More? (y/n): ");
					int ch = reader.readCharacter(new char[] {'y', 'n', ';'});
					System.out.println((char) ch);
					if (ch == 'y' || ch == ';') {
	        			if (query.hasMore()) {
	    					for (Entry<String, RuntimeVariable> var : query.getBindingMap().entrySet()) {
	    						System.out.println(var.getKey() + "=" + RuntimeVariable.deref(var.getValue()));
	    					}
	        			} else {
	        				System.out.println("No.");
	        				break;
	        			}
					} else {
						break;
					}
				}
			}
		} catch (LLJException e) {
			printError(e);
		}
		
		return false;
	}
	
	public static void printError(Throwable e) {
		while (e != null) {
			System.err.println(e.getMessage());
			e = e.getCause();
		}
	}

}

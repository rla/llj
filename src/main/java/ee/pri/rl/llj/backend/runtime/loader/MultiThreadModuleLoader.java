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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import ee.pri.rl.llj.backend.BackendConfiguration;
import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.LLJContext;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.ModulePath;
import ee.pri.rl.llj.frontend.LLJFrontend;

/**
 * Backend module loader that is able to process modules in parallel.
 * 
 * @author Raivo Laanemets
 * @since 1.1
 */
public class MultiThreadModuleLoader extends BackendModuleLoader {
	private static final long serialVersionUID = 1L;
	
	private ReentrantLock testProcessingLock = new ReentrantLock(true);
	private Map<ModulePath, BackendModule> modules;
	private Queue<ModulePath> queue;
	private int processingCount;
	private int numOfThreads = 2;

	public MultiThreadModuleLoader(LLJFrontend frontend, int numOfThreads) {
		super(frontend);
		this.numOfThreads = numOfThreads;
	}

	@Override
	public Map<ModulePath, BackendModule> load(
			ModulePath main,
			BackendConfiguration configuration,
			LLJContext context) throws LLJException {
		
		modules = new HashMap<ModulePath, BackendModule>();
		
		queue = new LinkedList<ModulePath>();
		queue.add(main);
		
		processingCount = 0;
		
		Thread[] threads = new Thread[numOfThreads];
		for (int i = 0; i < numOfThreads; i++) {
			threads[i] = new Thread(new Loader(configuration, context));
			threads[i].start();
		}
		
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return modules;
	}
	
	private class Loader implements Runnable {
		private BackendConfiguration configuration;
		private LLJContext context;

		public Loader(BackendConfiguration configuration, LLJContext context) {
			this.configuration = configuration;
			this.context = context;
		}

		@Override
		public void run() {
			while (true) {
				testProcessingLock.lock();
				if (queue.isEmpty()) {
					if (processingCount == 0) {
						testProcessingLock.unlock();
						break;
					} else {
						testProcessingLock.unlock();
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							break;
						}
						continue;
					}
				} else {
					ModulePath path = queue.poll();
					if (!modules.containsKey(path)) {
						modules.put(path, null);
						processingCount++;
						testProcessingLock.unlock();
						BackendModule module = null;
						try {
							module = loadModule(path, configuration, context);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							testProcessingLock.lock();
							if (module != null) {
								modules.put(path, module);
								queue.addAll(module.getImports());
							}
							processingCount--;
							testProcessingLock.unlock();
						}
					} else {
						testProcessingLock.unlock();
					}
				}
			}
		}
		
	}

}

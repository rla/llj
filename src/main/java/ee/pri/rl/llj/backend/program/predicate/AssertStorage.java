package ee.pri.rl.llj.backend.program.predicate;

import java.util.ArrayList;
import java.util.List;

import ee.pri.rl.llj.backend.runtime.RuntimeStruct;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;

/**
 * Assert storage for predicates that can be asserted/retracted.
 * Restriction: only ground terms can be used.
 * FIXME rather incomplete
 * 
 * @author Raivo Laanemets
 */
public class AssertStorage {
	private final List<Object> terms = new ArrayList<Object>();

	/**
	 * Asserts the given term as the first clause. The method
	 * is thread-safe.
	 */
	public synchronized void assertFirst(RuntimeStruct term) {
		terms.add(0, RuntimeVariable.copyTerm(term));
	}
	
	/**
	 * Asserts the given term as the last clause. The method
	 * is thread-safe.
	 */
	public synchronized void assertLast(RuntimeStruct term) {
		terms.add(RuntimeVariable.copyTerm(term));
	}
	
	/**
	 * Returns the count of asserted terms.
	 */
	public synchronized int size() {
		return terms.size();
	}
	
	/**
	 * Returns n-th term.
	 */
	public synchronized RuntimeStruct getTerm(int n) {
		return (RuntimeStruct) RuntimeVariable.copyTerm(terms.get(n));
	}
	
}

package llj;

import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.test.AbstractLLJTest;
import java.lang.String;

public class AssertTest extends AbstractLLJTest {
	
	public void test() throws LLJException {
		RuntimeQuery query = query("a(X, Y)");
		assertFalse(query.execute());
		assertFalse(query.hasMore());
		
		RuntimeQuery assertQuery = query("test1");
		assertTrue(assertQuery.execute());
		assertFalse(assertQuery.hasMore());
		
		query = query("a(X, Y)");
		assertTrue(query.execute());
		assertEquals(3L, query.getBinding("X"));
		assertEquals(4L, query.getBinding("Y"));
		assertFalse(query.hasMore());
		
		assertQuery = query("test2");
		assertTrue(assertQuery.execute());
		assertFalse(assertQuery.hasMore());
		
		query = query("a(X, Y)");
		assertTrue(query.execute());
		assertEquals(3L, query.getBinding("X"));
		assertEquals(4L, query.getBinding("Y"));
		assertTrue(query.hasMore());
		assertEquals(5L, query.getBinding("X"));
		assertEquals(6L, query.getBinding("Y"));
		assertFalse(query.hasMore());
	}

	@Override
	protected String getPath() {
		return "llj:assert";
	}

}

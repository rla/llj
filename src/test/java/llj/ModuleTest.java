package llj;

import java.lang.String;

import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.test.AbstractLLJTest;

public class ModuleTest extends AbstractLLJTest {

	public void test() throws LLJException {
		RuntimeQuery query = query("test(X)");
		assertTrue(query.execute());
		
		assertEquals(1L, query.getBinding("X"));
	}
	
	@Override
	protected String getPath() {
		return "llj:module";
	}

}

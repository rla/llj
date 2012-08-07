package ee.pri.rl.llj.backend.runtime.goal;

import ee.pri.rl.llj.backend.runtime.RuntimeQuery;

/**
 * Implementation of fail/0 goal.
 * 
 * @author Raivo Laanemets
 */
public final class FailGoal extends AbstractGoal {
	private static final long serialVersionUID = 1L;

	@Override
	public AbstractGoal run(final RuntimeQuery Q) throws Exception {
		return Q.backtrack();
	}
	
	@Override
	public AbstractGoal reentry(final RuntimeQuery Q) throws Exception {
		throw new RuntimeException(getClass() + " does not support re-entry");
	}

}

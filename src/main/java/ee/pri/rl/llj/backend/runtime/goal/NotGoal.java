package ee.pri.rl.llj.backend.runtime.goal;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.program.call.BackendAbstractCall;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;

/**
 * Goal for not(predicate).
 * 
 * @author Raivo Laanemets
 */
public final class NotGoal extends AbstractGoal {
	private static final long serialVersionUID = 1L;
	
	private final BackendModule M;
	private final AbstractGoal G;
	private final BackendAbstractCall call;
	private final Object[] P;

	public NotGoal(AbstractGoal G, BackendAbstractCall call, Object[] P, BackendModule M) {
		this.M = M;
		this.G = G;
		this.call = call;
		this.P = P;
	}

	@Override
	public AbstractGoal run(final RuntimeQuery Q) throws Exception {
		Q.markChoice(this);
		return call.makeGoal(P, Q, new CutGoal(new FailGoal(), Q.lastGoalIndex()), M);
	}

	@Override
	public AbstractGoal reentry(final RuntimeQuery Q) throws Exception {
		// Cut constructed in the first-entry was not
		// reached, meaning there was failing predicate call.
		return G;
	}

}

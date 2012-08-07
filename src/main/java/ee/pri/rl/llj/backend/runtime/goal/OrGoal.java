package ee.pri.rl.llj.backend.runtime.goal;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.program.call.BackendOrCall;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;

public final class OrGoal extends AbstractGoal {
	private static final long serialVersionUID = 1L;
	
	private final BackendModule M;
	private final AbstractGoal G;
	private final Object[] P;
	private final BackendOrCall C;

	public OrGoal(AbstractGoal G, BackendOrCall C, Object[] P, BackendModule M) {
		this.M = M;
		this.G = G;
		this.P = P;
		this.C = C;
	}

	@Override
	public AbstractGoal run(final RuntimeQuery Q) throws Exception {
		Q.markChoice(this);
		return C.left.makeGoal(P, Q, G, M);
	}

	@Override
	public AbstractGoal reentry(final RuntimeQuery Q) throws Exception {
		return C.right.makeGoal(P, Q, G, M);
	}

}

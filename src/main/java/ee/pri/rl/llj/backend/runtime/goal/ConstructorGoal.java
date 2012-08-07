package ee.pri.rl.llj.backend.runtime.goal;

import ee.pri.rl.llj.backend.program.call.BackendConstructorCall;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;

/**
 * Goal for term constructors.
 * 
 * @author Raivo Laanemets
 */
public final class ConstructorGoal extends AbstractGoal {
	private static final long serialVersionUID = 1L;
	
	private final AbstractGoal G;
	private final Object[] P;
	private final BackendConstructorCall C;

	public ConstructorGoal(AbstractGoal G, BackendConstructorCall C, Object[] P) {
		this.G = G;
		this.C = C;
		this.P = P;
	}

	@Override
	public AbstractGoal run(final RuntimeQuery Q) throws Exception {		
		if (Q.unify(P[C.left], C.right.getRuntimeObject(P))) {
			return G;
		} else {
			return Q.backtrack();
		}
	}

	@Override
	public AbstractGoal reentry(final RuntimeQuery Q) throws Exception {
		throw new RuntimeException(getClass() + " does not support re-entry.");
	}

}

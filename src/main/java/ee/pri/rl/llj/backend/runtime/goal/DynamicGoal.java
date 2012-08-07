package ee.pri.rl.llj.backend.runtime.goal;

import ee.pri.rl.llj.backend.program.call.BackendDynamicCall;
import ee.pri.rl.llj.backend.runtime.NoChoiceException;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.RuntimeStruct;

/**
 * Goal for calls of dynamic predicates (assert/retract). 
 * 
 * @author Raivo Laanemets
 */
public final class DynamicGoal extends AbstractGoal {
	private static final long serialVersionUID = 1L;
	
	private final AbstractGoal G;
	private final Object[] P;
	private final BackendDynamicCall C;
	
	public DynamicGoal(AbstractGoal G, BackendDynamicCall C, Object[] P) {
		this.G = G;
		this.C = C;
		this.P = P;
	}

	@Override
	public AbstractGoal run(final RuntimeQuery Q) throws Exception {
		return tryCurrentTerm(Q);
	}

	@Override
	public AbstractGoal reentry(final RuntimeQuery Q) throws Exception {
		return tryCurrentTerm(Q);
	}
	
	private AbstractGoal tryCurrentTerm(final RuntimeQuery Q) throws NoChoiceException {
		if (n >= C.storage.size()) {
			return Q.backtrack();
		}
		
		if (n < C.storage.size() - 1) {
			Q.markChoice(this);
		}
		
		RuntimeStruct term = C.storage.getTerm(n);
		
		for (int i = 1; i < term.data.length; i++) {
			if (!Q.unify(P[C.A[i - 1]], term.data[i])) {
				return Q.backtrack();
			}
		}
		
		return G;
	}

}

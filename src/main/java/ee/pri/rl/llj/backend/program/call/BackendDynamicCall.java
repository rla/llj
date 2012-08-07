package ee.pri.rl.llj.backend.program.call;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.program.predicate.AssertStorage;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.DynamicGoal;

/**
 * Backend call for dynamic predicates (assert/retract).
 * 
 * @author Raivo Laanemets
 */
public final class BackendDynamicCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	public final AssertStorage storage;

	public BackendDynamicCall(BackendModule M, AssertStorage storage, int[] A) {
		super(M, A); 
		this.storage = storage;
	}

	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M) {
		return new DynamicGoal(G, this, P);
	}

}

package ee.pri.rl.llj.backend.program.call;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.CutGoal;

/**
 * If-then call (->).
 * 
 * @author Raivo Laanemets
 */
public final class BackendIfThenCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	private final BackendAbstractCall left;
	private final BackendAbstractCall right;

	public BackendIfThenCall(BackendModule M, BackendAbstractCall left, BackendAbstractCall right) {
		super(M);
		this.left = left;
		this.right = right;
	}

	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M) {
		return left.makeGoal(P, Q, new CutGoal(right.makeGoal(P, Q, G, M), Q.lastGoalIndex()), M);
	}

}

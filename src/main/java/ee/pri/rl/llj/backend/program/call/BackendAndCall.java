package ee.pri.rl.llj.backend.program.call;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;

/**
 * General call for conjuctions (,).
 * 
 * @author Raivo Laanemets
 */
public final class BackendAndCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	private final BackendAbstractCall left;
	private final BackendAbstractCall right;

	public BackendAndCall(BackendModule M, BackendAbstractCall left, BackendAbstractCall right) {
		super(M);
		this.left = left;
		this.right = right;
	}

	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M) {
		return left.makeGoal(P, Q, right.makeGoal(P, Q, G, M), M);
	}

}

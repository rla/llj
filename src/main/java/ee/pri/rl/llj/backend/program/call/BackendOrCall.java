package ee.pri.rl.llj.backend.program.call;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.OrGoal;

/**
 * General disjunctive call (;).
 * 
 * @author Raivo Laanemets
 */
public final class BackendOrCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	public final BackendAbstractCall left;
	public final BackendAbstractCall right;

	public BackendOrCall(BackendModule M, BackendAbstractCall left, BackendAbstractCall right) {
		super(M);
		this.left = left;
		this.right = right;
	}

	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M) {
		return new OrGoal(G, this, P, M);
	}

}

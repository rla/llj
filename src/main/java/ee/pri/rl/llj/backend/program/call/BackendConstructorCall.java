package ee.pri.rl.llj.backend.program.call;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.ConstructorGoal;
import ee.pri.rl.llj.backend.term.BackendTerm;

/**
 * Term constructor call. A1 = A2 where A1 or A2 is
 * not a variable. 
 * 
 * @author Raivo Laanemets
 */
public final class BackendConstructorCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	public final int left;
	public final BackendTerm right;
	
	public BackendConstructorCall(BackendModule M, int left, BackendTerm right) {
		super(M);
		this.left = left;
		this.right = right;
	}
	
	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M) {
		return new ConstructorGoal(G, this, P);
	}

}

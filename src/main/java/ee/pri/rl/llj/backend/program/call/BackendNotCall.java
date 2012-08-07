package ee.pri.rl.llj.backend.program.call;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.NotGoal;

/**
 * Represents call not(predicate(A1, A2, ...)).
 * 
 * @author Raivo Laanemets
 */
public final class BackendNotCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The negated predicate call.
	 */
	public final BackendAbstractCall predicateCall;

	/**
	 * Constructs the new call.
	 * 
	 * @param predicateCall the original predicate call
	 */
	public BackendNotCall(BackendAbstractCall predicateCall) {
		super(predicateCall.M);
		this.predicateCall = predicateCall;
	}

	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M) {
		return new NotGoal(G, predicateCall, P, M);
	}

	@Override
	public String toString() {
		return "not(" + predicateCall + ")";
	}

}

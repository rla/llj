package ee.pri.rl.llj.backend.program.call;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.runtime.RuntimeQuery;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.FailGoal;

/**
 * Implementation of fail/0.
 * 
 * @author Raivo Laanemets
 */
public final class BackendFailCall extends BackendAbstractCall {
	private static final long serialVersionUID = 1L;
	
	/**
	 * A static representation of fail call. Fail calls from
	 * different call sites do not differ in any way. Fail also
	 * does not make use of module information (which for this general
	 * instance is <code>null</code>).
	 */
	public static final BackendFailCall CALL = new BackendFailCall(null);

	private BackendFailCall(BackendModule M) {
		super(M);
	}

	@Override
	public AbstractGoal makeGoal(Object[] P, RuntimeQuery Q, AbstractGoal G, BackendModule M) {
		return new FailGoal();
	}

}

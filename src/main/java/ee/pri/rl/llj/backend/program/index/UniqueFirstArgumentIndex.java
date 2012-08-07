package ee.pri.rl.llj.backend.program.index;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import ee.pri.rl.llj.backend.program.BackendDefinition;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;
import ee.pri.rl.llj.backend.runtime.goal.PredicateGoal;

/**
 * Predicate index for predicates where first argument could be possibly used
 * for indexing.
 * 
 * @author Raivo Laanemets
 */
public class UniqueFirstArgumentIndex implements PredicateIndex {
	private Map<Object, BackendDefinition> indexData;

	public UniqueFirstArgumentIndex(Map<Object, BackendDefinition> indexData) {
		this.indexData = indexData;
	}

	@Override
	public List<BackendDefinition> applyIndex(PredicateGoal predicateGoal) {
		Object key = predicateGoal.getArgument(0);

		if (key.getClass() == RuntimeVariable.class) {
			return null;
		} else {
			BackendDefinition definition = indexData.get(key);
			if (definition == null) {
				return null;
			} else {
				return Collections.singletonList(definition);
			}
		}
	}

}

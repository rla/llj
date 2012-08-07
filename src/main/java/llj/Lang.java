/*
Copyright (C) 2008-2010 Raivo Laanemets

This file is part of Logic Language for Java (LLJ).

LLJ is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

LLJ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with LLJ.  If not, see <http://www.gnu.org/licenses/>.
*/
package llj;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.lang.String;

import ee.pri.rl.llj.backend.program.BackendModule;
import ee.pri.rl.llj.backend.program.call.BackendAbstractCall;
import ee.pri.rl.llj.backend.program.predicate.AssertStorage;
import ee.pri.rl.llj.backend.runtime.RuntimeListStruct;
import ee.pri.rl.llj.backend.runtime.RuntimeStruct;
import ee.pri.rl.llj.backend.runtime.RuntimeVariable;
import ee.pri.rl.llj.backend.runtime.goal.AbstractGoal;
import ee.pri.rl.llj.backend.runtime.goal.JavaCallGoal;
import ee.pri.rl.llj.common.LLJException;
import ee.pri.rl.llj.common.java.JavaModule;
import ee.pri.rl.llj.common.java.Predicate;
import ee.pri.rl.llj.common.operators.Operator;
import ee.pri.rl.llj.common.term.Functor;

public class Lang implements JavaModule {
	
	/*
	@Predicate(name="memberb", arity = 2)
	public AbstractGoal memberb2(JavaCallGoal goal) throws Exception {
		Object a1 = goal.getObject(0, Object.class);
		RuntimeListStruct list = goal.getListStructArg(1);
		
		Object current = list;
		while (current.getClass() == RuntimeListStruct.class) {
			list = (RuntimeListStruct) current;
			if (goal.Q.unify(a1, list.head)) {
				return goal.G;
			} else {
				goal.Q.undoLastGoalBindings();
			}
			current = RuntimeVariable.deref(list.tail);
		}
		
		return goal.Q.backtrack();
	}*/
	
	@Predicate(name="true", arity = 0)
	public AbstractGoal true0(JavaCallGoal goal) throws Exception {
		return goal.G;
	}
	
	@Predicate(name="\\=", arity = 2)
	public AbstractGoal notUnifiable(JavaCallGoal goal) throws Exception {
		if (goal.Q.unify(goal.getArg(0), goal.getArg(1))) {
			return goal.Q.backtrack();
		}
	
		goal.Q.undoLastGoalBindings();
		
		return goal.G;
	}
	
	@Predicate(name="is", arity = 2)
	public AbstractGoal is(JavaCallGoal goal) throws Exception {
		Object lhs = goal.getArg(0);
		Object rhs = goal.getArg(1);
		
		if (!goal.Q.unify(lhs, calculate(rhs))) {
			return goal.Q.backtrack();
		}
		
		return goal.G;
	}
	
	@Predicate(name=">", arity = 2)
	public AbstractGoal arithmeticsGreater(JavaCallGoal goal) throws Exception {
		return arithmeticsCompareInternal(goal);
	}
	
	@Predicate(name="<", arity = 2)
	public AbstractGoal arithmeticsLess(JavaCallGoal goal) throws Exception {
		return arithmeticsCompareInternal(goal);
	}
	
	@Predicate(name=">=", arity = 2)
	public AbstractGoal arithmeticsGreaterEqual(JavaCallGoal goal) throws Exception {
		return arithmeticsCompareInternal(goal);
	}
	
	@Predicate(name="=<", arity = 2)
	public AbstractGoal arithmeticsLessEqual(JavaCallGoal goal) throws Exception {
		return arithmeticsCompareInternal(goal);
	}
	
	@Predicate(name="=:=", arity = 2)
	public AbstractGoal arithmeticsEqual(JavaCallGoal goal) throws Exception {
		return arithmeticsCompareInternal(goal);
	}
	
	@Predicate(name="=\\=", arity = 2)
	public AbstractGoal arithmeticsNotEqual(JavaCallGoal goal) throws Exception {
		return arithmeticsCompareInternal(goal);
	}
	
	private AbstractGoal arithmeticsCompareInternal(JavaCallGoal goal) throws Exception {
		Number lhs = calculate(goal.getArg(0));
		Number rhs = calculate(goal.getArg(1));
		
		// FIXME bigint/bigdecimal precision?
		
		String op = goal.C.functor.name;
		if (Operator.GREATER.getName().equals(op)) {
			if (lhs.doubleValue() <= rhs.doubleValue()) {
				return goal.Q.backtrack();
			}
		} else if (Operator.LESS.getName().equals(op)) {
			if (lhs.doubleValue() >= rhs.doubleValue()) {
				return goal.Q.backtrack();
			}
		} else if (Operator.GREATER_EQUAL.getName().equals(op)) {
			if (lhs.doubleValue() < rhs.doubleValue()) {
				return goal.Q.backtrack();
			}
		} else if (Operator.LESS_EQUAL.getName().equals(op)) {
			if (lhs.doubleValue() > rhs.doubleValue()) {
				return goal.Q.backtrack();
			}
		} else if (Operator.EQUAL_ARITH.getName().equals(op)) {
			if (lhs.doubleValue() != rhs.doubleValue()) {
				return goal.Q.backtrack();
			}
		} else if (Operator.NOT_EQUAL.getName().equals(op)) {
			if (lhs.doubleValue() == rhs.doubleValue()) {
				return goal.Q.backtrack();
			}
		} else {
			throw new RuntimeException("Unknown operator: " + op);
		}
		
		return goal.G;

	}
	
	protected final Number calculate(Object term) throws LLJException {
		if (term instanceof RuntimeStruct) {
			return calculateStruct((RuntimeStruct) term);
		} else if (term.getClass() == Integer.class) {
			return (Number) term;
		} else if (term.getClass() ==  Long.class) {
			return (Number) term;
		} else if (term.getClass() ==  Float.class) {
			return (Number) term;
		} else if (term.getClass() == Double.class) {
			return (Number) term;
		} else if (term.getClass() == BigDecimal.class) {
			return (Number) term;
		} else if (term.getClass() == BigInteger.class) {
			return (Number) term;
		} else if (term.getClass() == Short.class) {
			return (Number) term;
		} else if (term.getClass() == Byte.class) {
			return (Number) term;
		} else {
			throw new LLJException("Cannot use in arithmetics: " + term);
		}
	}
	
	private Number calculateStruct(RuntimeStruct struct) throws LLJException {
		String name = (String) struct.data[0];
		if ("-".equals(name) && struct.data.length == 2) {
			return calculateUnaryMinus(RuntimeVariable.deref(struct.data[1]));
		} 
		
		Object a1 = calculate(RuntimeVariable.deref(struct.data[1]));
		Object a2 = calculate(RuntimeVariable.deref(struct.data[2]));
		
		// FIXME simplify!
		if (a1.getClass() == BigDecimal.class) {
			return calculateBigDecimal(name, (BigDecimal) a1, (Number) a2, false);
		} else if (a2.getClass() == BigDecimal.class) {
			return calculateBigDecimal(name, (BigDecimal) a2, (Number) a1, true);
		} else if (a1.getClass() == BigInteger.class) {
			return calculateBigInteger(name, (BigInteger) a1, (Number) a2, false);
		} else if (a2.getClass() == BigInteger.class) {
			return calculateBigInteger(name, (BigInteger) a2, (Number) a1, true);
		} else if (a1.getClass() == Double.class) {
			return calculateDouble(name, (Double) a1, (Number) a2, false);
		} else if (a2.getClass() == Double.class) {
			return calculateDouble(name, (Double) a2, (Number) a1, true);
		} else if (a1.getClass() == Float.class) {
			return calculateFloat(name, (Float) a1, (Number) a2, false);
		} else if (a2.getClass() == Float.class) {
			return calculateFloat(name, (Float) a2, (Number) a1, true);
		} else if (a1.getClass() == Long.class) {
			return calculateLong(name, (Long) a1, (Number) a2, false);
		} else if (a2.getClass() == Long.class) {
			return calculateLong(name, (Long) a2, (Number) a1, true);
		} else if (a1.getClass() == Integer.class) {
			return calculateInteger(name, (Integer) a1, (Number) a2, false);
		} else if (a2.getClass() == Integer.class) {
			return calculateInteger(name, (Integer) a2, (Number) a1, true);
		} else if (a1.getClass() == Short.class) {
			return calculateShort(name, (Short) a1, (Number) a2, false);
		} else if (a2.getClass() == Short.class) {
			return calculateShort(name, (Short) a2, (Number) a1, true);
		} else if (a1.getClass() == Byte.class) {
			return calculateByte(name, (Byte) a1, (Number) a2, false);
		} else if (a2.getClass() == Byte.class) {
			return calculateByte(name, (Byte) a2, (Number) a1, true);
		} else {
			throw new LLJException("Cannot use in arithmetics: " + struct);
		}
	}
	
	private Number calculateByte(String name, Byte a1, Number a, boolean reverseArgs) throws LLJException {
		if ("+".equals(name)) {
			return Byte.valueOf((byte) (a1.byteValue() + a.byteValue()));
		} else if ("-".equals(name)) {
			return Byte.valueOf((byte) (a1.byteValue() - a.byteValue()));
		} else if ("*".equals(name)) {
			return Byte.valueOf((byte) (a1.byteValue() * a.byteValue()));
		} else if ("/".equals(name)) {
			return (reverseArgs ? (double) a.byteValue() / a1.byteValue() : (double) a1.byteValue() / a.byteValue());
		} else if ("mod".equals(name)) {
			return Byte.valueOf((byte) (a1.byteValue() % a.byteValue()));
		} else if ("div".equals(name)) {
			return Byte.valueOf((byte) (a1.byteValue() / a.byteValue()));
		}
		return null;
	}

	private Number calculateShort(String name, Short a1, Number a, boolean reverseArgs) throws LLJException {
		if ("+".equals(name)) {
			return Short.valueOf((short) (a1.shortValue() + a.shortValue()));
		} else if ("-".equals(name)) {
			return Short.valueOf((short) (a1.shortValue() - a.shortValue()));
		} else if ("*".equals(name)) {
			return Short.valueOf((short) (a1.shortValue() * a.shortValue()));
		} else if ("/".equals(name)) {
			return (reverseArgs ? (double) a.shortValue() / a1.shortValue() : (double) a1.shortValue() / a.shortValue());
		} else if ("mod".equals(name)) {
			return Short.valueOf((short) (a1.shortValue() % a.shortValue()));
		} else if ("div".equals(name)) {
			return Short.valueOf((short) (a1.shortValue() / a.shortValue()));
		}
		return null;
	}

	private Number calculateInteger(String name, Integer a1, Number a, boolean reverseArgs) throws LLJException {
		if ("+".equals(name)) {
			return a1.intValue() + a.intValue();
		} else if ("-".equals(name)) {
			return a1.intValue() - a.intValue();
		} else if ("*".equals(name)) {
			return a1.intValue() * a.intValue();
		} else if ("/".equals(name)) {
			return (reverseArgs ? (double) a.intValue() / a1.intValue() : (double) a1.intValue() / a.intValue());
		} else if ("mod".equals(name)) {
			return a1.intValue() % a.intValue();
		} else if ("div".equals(name)) {
			return a1.intValue() / a.intValue();
		}
		return null;
	}

	private Number calculateLong(String name, Long a1, Number a, boolean reverseArgs) throws LLJException {
		if ("+".equals(name)) {
			return a1.longValue() + a.longValue();
		} else if ("-".equals(name)) {
			return a1.longValue() - a.longValue();
		} else if ("*".equals(name)) {
			return a1.longValue() * a.longValue();
		} else if ("/".equals(name)) {
			return (reverseArgs ? (double) a.longValue() / a1.longValue() : (double) a1.longValue() / a.longValue());
		} else if ("mod".equals(name)) {
			return a1.longValue() % a.longValue();
		} else if ("div".equals(name)) {
			return a1.longValue() / a.longValue();
		}
		return null;
	}

	private Number calculateFloat(String name, Float a1, Number a, boolean reverseArgs) {
		if ("+".equals(name)) {
			return a1.floatValue() + a.floatValue();
		} else if ("-".equals(name)) {
			return a1.floatValue() - a.floatValue();
		} else if ("*".equals(name)) {
			return a1.floatValue() * a.floatValue();
		} else if ("/".equals(name)) {
			return (reverseArgs ? a.floatValue() / a1.floatValue() : a1.floatValue() / a.floatValue());
		} else if ("mod".equals(name)) {
			return a1.floatValue() % a.floatValue();
		} else if ("div".equals(name)) {
			return a1.floatValue() / a.floatValue();
		}
		return null;
	}

	private Number calculateDouble(String name, Double a1, Number a, boolean reverseArgs) {
		if ("+".equals(name)) {
			return a1.doubleValue() + a.doubleValue();
		} else if ("-".equals(name)) {
			return a1.doubleValue() - a.doubleValue();
		} else if ("*".equals(name)) {
			return a1.doubleValue() * a.doubleValue();
		} else if ("/".equals(name)) {
			return (reverseArgs ? a.doubleValue() / a1.doubleValue() : a1.doubleValue() / a.doubleValue());
		} else if ("mod".equals(name)) {
			return a1.doubleValue() % a.doubleValue();
		} else if ("div".equals(name)) {
			return a1.doubleValue() / a.doubleValue();
		}
		return null;
	}

	private Number calculateBigInteger(String name, BigInteger a1, Number a, boolean reverseArgs) {
		BigInteger a2 = null;
		if (a.getClass() == BigInteger.class) {
			a2 = (BigInteger) a;
		} else {
			a2 = BigInteger.valueOf((long) a.doubleValue());
		}
		
		if ("+".equals(name)) {
			return a1.add(a2);
		} else if ("-".equals(name)) {
			return a1.subtract(a2);
		} else if ("*".equals(name)) {
			return a1.multiply(a2);
		} else if ("/".equals(name)) {
			return reverseArgs ? new BigDecimal(a2).divide(new BigDecimal(a1)) : new BigDecimal(a1).divide(new BigDecimal(a2));
		} else if ("mod".equals(name)) {
			return a1.divideAndRemainder(a2)[1];
		} else if ("div".equals(name)) {
			return a1.divide(a2);
		}
		return null;
	}
	
	private Number calculateBigDecimal(String name, BigDecimal a1, Number a, boolean reverseArgs) {
		BigDecimal a2 = null;
		if (a.getClass() == BigDecimal.class) {
			a2 = (BigDecimal) a;
		} else if (a.getClass() == BigInteger.class) {
			a2 = new BigDecimal((BigInteger) a);
		} else {
			a2 = BigDecimal.valueOf(a.doubleValue());
		}
		
		if ("+".equals(name)) {
			return a1.add(a2);
		} else if ("-".equals(name)) {
			return a1.subtract(a2);
		} else if ("*".equals(name)) {
			return a1.multiply(a2);
		} else if ("/".equals(name)) {
			return reverseArgs ? a2.divide(a1) : a1.divide(a2);
		} else if ("mod".equals(name)) {
			return a1.divideAndRemainder(a2)[1];
		} else if ("div".equals(name)) {
			return a1.divideToIntegralValue(a2);
		}
		return null;
	}
	
	private Number calculateUnaryMinus(Object term) throws LLJException {
		if (term.getClass() == Integer.class) {
			return -((Integer) term).intValue();
		} else if (term.getClass() ==  Long.class) {
			return -((Long) term).longValue();
		} else if (term.getClass() ==  Float.class) {
			return -((Float) term).floatValue();
		} else if (term.getClass() == Double.class) {
			return -((Double) term).doubleValue();
		} else if (term.getClass() == BigDecimal.class) {
			return ((BigDecimal) term).negate();
		} else if (term.getClass() == BigInteger.class) {
			return ((BigInteger) term).negate();
		} else if (term.getClass() == Short.class) {
			return -((Short) term).shortValue();
		} else if (term.getClass() == Byte.class) {
			return -((Byte) term).byteValue();
		} else {
			throw new LLJException("Cannot use in arithmetics: " + term);
		}
	}
	
	@Predicate(name="call", arity = 1)
	public AbstractGoal call1(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}
	
	@Predicate(name="call", arity = 2)
	public AbstractGoal call2(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}
	
	
	@Predicate(name="call", arity = 3)
	public AbstractGoal call3(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}
	
	
	@Predicate(name="call", arity = 4)
	public AbstractGoal call4(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}
	
	@Predicate(name="call", arity = 5)
	public AbstractGoal call5(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}
	
	@Predicate(name="call", arity = 6)
	public AbstractGoal call6(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}
	
	@Predicate(name="call", arity = 7)
	public AbstractGoal call7(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}
	
	@Predicate(name = "call", arity = 8)
	public AbstractGoal call8(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}

	@Predicate(name = "call", arity = 9)
	public AbstractGoal call9(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}

	@Predicate(name = "call", arity = 10)
	public AbstractGoal call10(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}

	@Predicate(name = "call", arity = 11)
	public AbstractGoal call11(JavaCallGoal goal) throws Exception {
		return callInternal(goal);
	}

	private AbstractGoal callInternal(JavaCallGoal goal) throws Exception {
		
		// Normalize input term
		
		Object t = goal.getArg(0);
		RuntimeStruct call = null;
		if (t instanceof RuntimeStruct) {
			call = (RuntimeStruct) t;
		} else if (t instanceof String) {
			call = new RuntimeStruct(new Object[] {t});
		} else {
			throw new LLJException("Cannot execute " + t);
		}
		
		Functor functor = new Functor(
				call.getFunctor().name,
				call.getFunctor().arity + goal.C.A.length - 1);
		
		BackendModule module = goal.M;
		
		// Create local environment and suitable argument index.
		
		Object[] L = new Object[functor.arity];
		
		int a = call.data.length - 1;
		for (int i = 0; i < a; i++) {
			L[i] = call.data[i + 1];      // arguments from term
		}
		
		int b = a + goal.C.A.length - 1;
		for (int i = a; i < b; i++) {
			L[i] = goal.P[goal.C.A[i - a + 1]];     // extra arguments of call
		}
		
		// Argument i will be on position i. 
		
		int[] arguments = new int[functor.arity];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = i;
		}
		
		// Find call.
		
		BackendAbstractCall aC = module.resolveCall(functor, arguments);
		
		// Make goal from the call and return it.
		
		return aC.makeGoal(L, goal.Q, goal.G, module);
	}
	
	@Predicate(name="compare", arity = 3)
	public AbstractGoal compare3(JavaCallGoal goal) throws Exception {
		Object a1 = goal.getArg(1);
		Object a2 = goal.getArg(2);
		
		int result = compare(a1, a2);
		String stringResult = result == 0 ? "=" : (result > 0 ? ">" : "<");
		
		if (goal.Q.unify(stringResult, goal.getArg(0))) {
			return goal.G;
		} else {
			return goal.Q.backtrack();
		}
	}
	
	/**
	 * Compares two given terms by standard order. This is strictly typed
	 * and will throw errors when variable are compared to lists and such.
	 * 
	 * @param t1 First term.
	 * @param t2 Second term.
	 * 
	 * @return 0 when t1 = t2, -1 when t1 < t2 and 1 when t1 > t2.
	 * @throws LLJException When the terms cannot be compared.
	 */
	private static int compare(Object t1, Object t2) throws LLJException {
		t1 = RuntimeVariable.deref(t1);
		t2 = RuntimeVariable.deref(t2);
		
		if (t1.getClass() == RuntimeStruct.class && t2.getClass() == RuntimeStruct.class) {
			RuntimeStruct s1 = (RuntimeStruct) t1;
			RuntimeStruct s2 = (RuntimeStruct) t2;
			
			if (s1.data[0].equals(s2.data[0]) && s1.data.length == s2.data.length) {
				int argResult = 0;
				int argIndex = 1; // First argument at data[1].
				while (argResult == 0 && argIndex < s1.data.length) {
					argResult = compare(s1.data[argIndex], s2.data[argIndex]);
					argIndex++;
				}
				
				return argResult;
			} else {
				throw new LLJException("Cannot compare " + s1.getFunctor() + " and " + s2.getFunctor());
			}
		} else if (t1.getClass() == RuntimeListStruct.class && t2.getClass() == RuntimeListStruct.class) {
			RuntimeListStruct l1 = (RuntimeListStruct) t1;
			RuntimeListStruct l2 = (RuntimeListStruct) t2;
			
			int headCompare = compare(l1.head, l2.head);
			if (headCompare != 0) {
				return headCompare;
			} else {
				return compare(l1.tail, l2.tail);
			}
		} else if (t1 instanceof Number && t2 instanceof Number) {
			double d1 = ((Number) t1).doubleValue();
			double d2 = ((Number) t2).doubleValue();
			
			return d1 - d2 > 0 ? 1 : (d1 - d2 < 0 ? -1 : 0);
		} else if (t1 instanceof String && t2 instanceof String) {
			String s1 = (String) t1;
			String s2 = (String) t2;
			
			return s1.compareTo(s2);
		} else {
			throw errorCannotCompare(t1, t2);
		}
	}
	
	private static LLJException errorCannotCompare(Object t1, Object t2) {
		return new LLJException("Cannot compare " + t1.getClass() + " and " + t2.getClass());
	}
	
	@Predicate(name="asserta", arity = 1)
	public AbstractGoal asserta(JavaCallGoal goal) throws Exception {
		RuntimeStruct term = goal.getStructArg(0);
		
		Functor functor = term.getFunctor();
		BackendModule module = goal.M;
		AssertStorage storage = module.getDynamicStorage(functor);
		
		if (storage == null) {
			throw new RuntimeException("There is dynamic predicate with functor " + functor);
		}
		
		storage.assertFirst(term);
		
		return goal.G;
	}
	
	@Predicate(name="assertz", arity = 1)
	public AbstractGoal assertz(JavaCallGoal goal) throws Exception {
		RuntimeStruct term = goal.getStructArg(0);
		
		Functor functor = term.getFunctor();
		BackendModule module = goal.M;
		AssertStorage storage = module.getDynamicStorage(functor);
		
		if (storage == null) {
			throw new RuntimeException("There is dynamic predicate with functor " + functor);
		}
		
		storage.assertLast(term);
		
		return goal.G;
	}
	
	@Predicate(name="assert", arity = 1)
	public AbstractGoal assert1(JavaCallGoal goal) throws Exception {
		return assertz(goal);
	}
}

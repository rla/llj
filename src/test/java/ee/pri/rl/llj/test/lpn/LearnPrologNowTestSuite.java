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
package ee.pri.rl.llj.test.lpn;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	KnowledgeBase1Test.class,
	KnowledgeBase2Test.class,
	KnowledgeBase3Test.class,
	KnowledgeBase4Test.class,
	KnowledgeBase5Test.class,
	MatchingTest.class,
	LineTest.class,
	Word5Test.class,
	EatingTest.class,
	DescendantTest.class,
	SuccessorTest.class,
	RussianDollsTest.class,
	TrainsTest.class,
	ListBasicTest.class,
	ArithmeticsCompareTest.class,
	TestAccMax.class
})
public class LearnPrologNowTestSuite extends TestSuite {}

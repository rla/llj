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
package ee.pri.rl.llj.test;

import junit.framework.TestSuite;
import llj.LLJStdLibTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ee.pri.rl.llj.backend.program.index.IndexTest;
import ee.pri.rl.llj.frontend.FrontendTestSuite;
import ee.pri.rl.llj.test.lpn.LearnPrologNowTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	FrontendTestSuite.class,
	LearnPrologNowTestSuite.class,
	LLJStdLibTestSuite.class,
	CutTest.class,
	IndexTest.class
})
public class LLJTestSuite extends TestSuite {}

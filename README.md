LLJ - Logic Language for JVM
============================

LLJ is Prolog-like logic language for running on JVM. It allows close
integration with Java libraries. LLJ was developed in 2008-2010. The
code is fairly commented and covered with various test cases.

Differences from real Prolog
----------------------------

* Code is loaded through Java classpath
* Module names correspond to Java classpath entries
* Syntax is based on UTF-8
* Terms can be arbitrary Java objects
* No term/goal expansion
* No DCG's
* No custom operators
* Assert/retract can only be used for ground predicates
* Most ISO-Prolog standard predicates are not implemented

Dependencies
------------

For embedded usage there are no dependencies other than the Java Runtime Environment (>= 1.6). The
interactive top-level command-line interface requires [jline](http://jline.sourceforge.net/) to be
in the classpath.

Compiling
---------

Compiling requires JDK (>= 1.6) and Maven. Run Maven goal `mvn package` to build the jar
file into `target/llj.jar`.

Usage
-----

Example module (queens benchmark):

```prolog
:- module(ee:pri:rl:llj:benchmark:queens, [
	queens/2,
	count/2
]).

:- import(llj:list).
:- import(llj:lang).
:- import(llj:findall).

count(N, C):-
	findall(_, queens(N,_), L),
	length(L, C).

queens(N,Qs) :-
	range(1,N,Ns),
	queens(Ns,[],Qs).

queens([],Qs,Qs).
queens(UnplacedQs,SafeQs,Qs) :-
	select(UnplacedQs,UnplacedQs1,Q),
	not_attack(SafeQs,Q),
	queens(UnplacedQs1,[Q|SafeQs],Qs).

not_attack(Xs,X) :-
	not_attack(Xs,X,1).

not_attack([],_,_) :- !.
not_attack([Y|Ys],X,N) :-
	X =\= Y+N, X =\= Y-N,
	N1 is N+1,
	not_attack(Ys,X,N1).

select([X|Xs],Xs,X).
select([Y|Ys],[Y|Zs],X) :- select(Ys,Zs,X).

range(N,N,[N]) :- !.
range(M,N,[M|Ns]) :-
	M < N,
	M1 is M+1,
	range(M1,N,Ns).
```

Example code of using LLJ from Java:

```java
public class MemberExample {
	
	public static void main(String[] args) throws LLJException {
	
		// Creates new LLJ context
		LLJContext context = new LLJContext("llj:list");
		
		// Creates new LLJ query
		RuntimeQuery query = context.createQuery("member(X, [1,2,3])");
		
		// Executes the query for the first answer
		if (query.execute()) {
			System.out.println("First answer: " + query.getBinding("X"));
		}
		
		// Tries to find more answers
		while (query.hasMore()) {
			System.out.println("Also: " + query.getBinding("X"));
		}
	}
}
```

The `LLJContext` class is thread safe while `RuntimeQuery` must be used only by a single
thread at once. Multiple `RuntimeQuery` classes can be executed at once on the same context
at the same time.

Defining predicates through Java code (this is example from `llj:io` module):

```java
public class Io implements JavaModule {

	@Predicate(name="list_files", arity = 2)
	public AbstractGoal listFiles2(JavaCallGoal goal) throws Exception {
		File file = goal.getObject(0, File.class);
		goal.setVar(1, RuntimeListStruct.fromArray(file.listFiles()));
		
		return goal.G;
	}
}
```

Similar
-------

* [WProlog](http://waitaki.otago.ac.nz/~michael/wp/) is a Prolog implementation on JVM.
  While it lacks cut, arithmetics, modules and arithmetics I got some of my implementation ideas from it.
* [XProlog](http://www.iro.umontreal.ca/~vaucher/XProlog/AA_README) enhanced version of WProlog.

Authors
-------

* [Raivo Laanemets](https://github.com/rla)

Some of the test cases were taken from example code
of the excellent book [Learn Prolog Now!](http://www.learnprolognow.org/).

License
-------

LLJ is distributed under LGPL license.
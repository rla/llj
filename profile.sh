#!/bin/sh

java -agentlib:hprof=cpu=times,depth=5 -cp target/classes:target/test-classes ee.pri.rl.llj.benchmark.PathBenchmark
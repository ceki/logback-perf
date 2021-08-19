/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package ch.qos.logback.perf;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.LoggerFactory;


/**
 * Benchmarks Log4j 2, Log4j 1, Logback and JUL using the DEBUG level which is
 * enabled for this test. The configuration for each uses a FileAppender
 */
// HOW TO RUN THIS TEST
// java -jar logback-perf/target/benchmarks.jar ".*AsyncWithFileAppenderBenchmark.*" -f 1
// -wi 10 -i 20
//
// RUNNING THIS TEST WITH 4 THREADS:
// java -jar logback-perf/target/benchmarks.jar ".*AsyncWithFileAppenderBenchmark.*" -f 1
// -wi 10 -i 20 -t 4

//# Run progress: 0.00% complete, ETA 00:01:00
//# Fork: 1 of 1
//# Warmup Iteration   1: 319.006 ops/ms
//# Warmup Iteration   2: 299.692 ops/ms
//Iteration   1: 156.006 ops/ms
//Iteration   2: 246.079 ops/ms
//Iteration   3: 261.095 ops/ms
//Iteration   4: 282.310 ops/ms
//
//
//Result "ch.qos.logback.perf.AsyncWithFileAppenderBenchmark.logbackFile":
//  236.372 ±(99.9%) 359.294 ops/ms [Average]
//  (min, avg, max) = (156.006, 236.372, 282.310), stdev = 55.601
//  CI (99.9%): [≈ 0, 595.667] (assumes normal distribution)

// ================================================================
// ================================= After the drainTo optimization

//Iteration   1: 391.968 ops/ms
//Iteration   2: 732.784 ops/ms
//Iteration   3: 567.104 ops/ms
//Iteration   4: 1486.198 ops/ms
//
//
//Result "ch.qos.logback.perf.AsyncWithFileAppenderBenchmark.logbackFile":
//  794.513 ±(99.9%) 3112.497 ops/ms [Average]
//  (min, avg, max) = (391.968, 794.513, 1486.198), stdev = 481.662
//  CI (99.9%): [≈ 0, 3907.010] (assumes normal distribution)
//
//
//Benchmark                                    Mode  Cnt    Score      Error   Units
//AsyncWithFileAppenderBenchmark.logbackFile  thrpt    4  794.513 ± 3112.497  ops/ms


@State(Scope.Benchmark)
public class AsyncWithFileAppenderBenchmark {
    public static final String MESSAGE = "This is a debug message";

    Logger log4j2Logger;
    Logger log4j2RandomLogger;
    org.slf4j.Logger slf4jLogger;
    org.apache.log4j.Logger log4j1Logger;
    java.util.logging.Logger julLogger;
    String outFolder = "";
    
    
    @Setup
    public void setUp() throws Exception {
        //System.setProperty("log4j.configurationFile", "log4j2-perf.xml");
        System.setProperty("logback.configurationFile", "logback-async-perf.xml");
        //System.setProperty("log4j.configuration", "log4j12-perf.xml");

        outFolder = System.getProperty("outFolder", "");
        
        deleteLogFiles();

        //log4j2Logger = LogManager.getLogger(AsyncWithFileAppenderBenchmark.class);
        //log4j2RandomLogger = LogManager.getLogger("TestRandom");
        slf4jLogger = LoggerFactory.getLogger(AsyncWithFileAppenderBenchmark.class);
        //log4j1Logger = org.apache.log4j.Logger.getLogger(AsyncWithFileAppenderBenchmark.class);
        
    }

    @TearDown
    public void tearDown() {
        System.clearProperty("log4j.configurationFile");
        System.clearProperty("log4j.configuration");
        System.clearProperty("logback.configurationFile");

        //deleteLogFiles();
    }

    private void deleteLogFiles() {
        //final File logbackFile = new File("target/testlogback.log");
        //logbackFile.delete();
        
        //final File log4jFile = new File("target/testlog4j.log");
        //log4jFile.delete();
        
        
        //final File log4jRandomFile = new File("target/testRandomlog4j2.log");
        //log4jRandomFile.delete();
        
        //final File log4j2File = new File("target/testlog4j2.log");
        //log4j2File.delete();
    }
    
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void logbackFile() {
        slf4jLogger.debug(MESSAGE);
    }

}

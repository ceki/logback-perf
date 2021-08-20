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
// java -jar logback-perf/target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 1
// -wi 10 -i 20
//
// RUNNING THIS TEST WITH 4 THREADS:
// java -jar logback-perf/target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 1
// -wi 10 -i 20 -t 4
@State(Scope.Benchmark)
public class FileAppenderBenchmark {
    public static final String MESSAGE = "This is a debug message";
    public static final String LOGBACK_FILE_PATH = "target/test-output/logback-perf.log";
    public static final String LOG4J2_FILE_PATH = "target/test-output/log4j2-perf.log";
    public static final String LOG4J_FILE_PATH = "target/test-output/log4j-perf.log";
    
    Logger log4j2Logger;
    org.slf4j.Logger slf4jLogger;
    org.apache.log4j.Logger log4j1Logger;
    java.util.logging.Logger julLogger;
    String outFolder = "";
    
    
    @Setup
    public void setUp() throws Exception {
        System.setProperty("log4j.configurationFile", "log4j2-perf.xml");
        System.setProperty("logback.configurationFile", "logback-perf.xml");
        System.setProperty("log4j.configuration", "log4j-perf.xml");

        outFolder = System.getProperty("outFolder", "");
        
        deleteLogFiles();

        String loggerName =       "ch.qos.logback.perf.FileAppenderBenchmark";
        
        log4j2Logger = LogManager.getLogger(loggerName);
        slf4jLogger = LoggerFactory.getLogger(loggerName);
        log4j1Logger = org.apache.log4j.Logger.getLogger(loggerName);
        
    }

    @TearDown
    public void tearDown() {
        System.clearProperty("log4j.configurationFile");
        System.clearProperty("log4j.configuration");
        System.clearProperty("logback.configurationFile");

        deleteLogFiles();
    }

    private void deleteLogFiles() {
        final File logbackFile = new File(LOGBACK_FILE_PATH);
        logbackFile.delete();
        
        final File log4jFile = new File(LOG4J_FILE_PATH);
        log4jFile.delete();
        
        final File log4j2File = new File(LOG4J2_FILE_PATH);
        log4j2File.delete();
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void log4j2File() {
        log4j2Logger.debug(MESSAGE);
    }
    
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void logbackFile() {
        slf4jLogger.debug(MESSAGE);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void log4j1File() {
        log4j1Logger.debug(MESSAGE);
    }
}

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
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.slf4j.LoggerFactory;

/**
 * Benchmarks Logback AsyncAppender in conjunction with FileAppender 
 *
 * */

// HOW TO RUN THIS TEST
// 
//java -jar  target/benchmarks.jar ".*AsyncWithFileAppenderBenchmark.*" -f 1  -tu ms -wi 2 -i 10 -t {1,2,4,16,32,64}

@State(Scope.Benchmark)
public class AsyncWithFileAppenderBenchmark {
    public static final String MESSAGE = "This is a debug message";

    public static final String LOGBACK_ASYNC_FILE_PATH = "target/test-output/logback-async-perf.log";
    public static final String LOG4J2_ASYNC_FILE_PATH = "target/test-output/log4j2-async-perf.log";
    public static final String LOG4J1_ASYNC_FILE_PATH = "target/test-output/log4j1-async-perf.log";
    public static final String LOG4J1_NEW_ASYNC_FILE_PATH = "target/test-output/log4j1-new-async-perf.log";

    public static final String LOG4J1_ASYNC_CONFIGURATION_FILE = "log4j1-async-perf.xml";
    public static final String LOG4J1_NEW_ASYNC_CONFIGURATION_FILE = "log4j1-new-async-perf.xml";

    Logger log4j2Logger;
    Logger log4j2RandomLogger;
    org.slf4j.Logger slf4jLogger;
    org.apache.log4j.Logger reload4jLogger;

    @State(Scope.Benchmark)
    public static class Reload4jThreadState {
        //@Param({LOG4J1_ASYNC_CONFIGURATION_FILE, LOG4J1_NEW_ASYNC_CONFIGURATION_FILE})
        @Param({ LOG4J1_NEW_ASYNC_CONFIGURATION_FILE})

        String configFile;

        //@Param({"1", "2", "16"})
        //nt threadCount;

        @Setup
        public void setup() {
            org.apache.log4j.LogManager.getLoggerRepository().resetConfiguration();
            URL url = Loader.getResource(configFile);
            DOMConfigurator.configure(url);
            System.out.println("Reload4jThreadState setup with config file: " + configFile);
        }
        @TearDown
        public void tearDown() {
            org.apache.log4j.LogManager.getLoggerRepository().resetConfiguration();
            System.out.println("------------Reload4jThreadState tearDown");
        }

    }

    @Setup
    public void setUp() throws Exception {

        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("log4j.configurationFile", "log4j2-async-perf.xml");
        System.setProperty("logback.configurationFile", "logback-async-perf.xml");
        //System.setProperty("log4j.configuration", "log4j-async-perf.xml");
        //System.setProperty("log4j.configuration", LOG4J1_ASYNC_CONFIGURATION_FILE);
        /////deleteLogFiles();

        log4j2Logger = LogManager.getLogger(this.getClass());
        slf4jLogger = LoggerFactory.getLogger(this.getClass());
        reload4jLogger = org.apache.log4j.Logger.getLogger(this.getClass());
    }

    @TearDown
    public void tearDown() {
        System.clearProperty("log4j2.contextSelector");
        System.clearProperty("log4j.configurationFile");
        System.clearProperty("log4j.configuration");
        System.clearProperty("logback.configurationFile");

        // better to delete files in setUp not in tearrDown!
        //deleteLogFiles();
    }

    private void deleteLogFiles() {
        System.out.println("Deleting files if existent.");
        chattyDelete(LOGBACK_ASYNC_FILE_PATH);
        chattyDelete(LOG4J2_ASYNC_FILE_PATH);
        chattyDelete(LOG4J1_ASYNC_FILE_PATH);
        chattyDelete(LOG4J1_NEW_ASYNC_FILE_PATH);
    }

    private void chattyDelete(String path) {
        final File file2Delete = new File(path);
        if (!file2Delete.exists()) {
            return;
        }
        System.out.println("About to delete [" + path + "]");
        boolean result = file2Delete.delete();
        if (!result)
            System.out.println("Failed to delete " + path);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@Benchmark
    public void logbackFile() {
        slf4jLogger.debug(MESSAGE);
    }

   // @BenchmarkMode(Mode.Throughput)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
   // @Benchmark
    public void log4j2AsyncFile() {
        //log4j2Logger.debug(MESSAGE);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void reload4jFile(Reload4jThreadState reload4jThreadState) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        reload4jLogger.debug(MESSAGE);
    }

    public static void main(String[] args) throws Exception {
        ChainedOptionsBuilder chainedOptionsBuilder = new OptionsBuilder()
                        .include(AsyncWithFileAppenderBenchmark.class.getName() + ".*")
                        .mode(Mode.Throughput)
                        .timeUnit(TimeUnit.MILLISECONDS)
                        .warmupTime(TimeValue.seconds(1))
                        .warmupIterations(2)
                        .measurementIterations(2)
                        .measurementTime(TimeValue.seconds(10*6))
                        //.timeout(TimeValue.seconds(5))
                        .forks(1)
                        .shouldFailOnError(true)
                        .shouldDoGC(true)
                        .threads(1)
                        .jvm("/java/jdk-21.0.1//bin/java")
                        .jvmArgs("-Xmx8192m")
                        //.addProfiler("gc")
                        //                .addProfiler("perfnorm")
                        //                .addProfiler("perfasm")
                        //.addProfiler("jfr");
                        ;
        Options options = chainedOptionsBuilder.build();

        new Runner(options).run();
    }
}

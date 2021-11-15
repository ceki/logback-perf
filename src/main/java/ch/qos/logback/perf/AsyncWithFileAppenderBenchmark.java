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
    public static final String LOGBACK_LLE_ASYNC_FILE_PATH = "target/test-output/logback-lle-async-perf.log";

    public static final String LOG4J2_ASYNC_FILE_PATH = "target/test-output/log4j2-async-perf.log";
    public static final String LOG4J_ASYNC_FILE_PATH = "target/test-output/log4j-async-perf.log";
                                                        
    Logger log4j2Logger;
    Logger log4j2RandomLogger;
    org.slf4j.Logger slf4jLogger;          // logback
    org.apache.log4j.Logger log4j1Logger;  // log4j1
    org.slf4j.Logger lleLogger;            // logback-logstash-encoder
    
    @Setup
    public void setUp() throws Exception {
    	
    	System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("log4j.configurationFile",   "log4j2-async-perf.xml");
        System.setProperty("logback.configurationFile", "logback-async-perf.xml");
        System.setProperty("log4j.configuration",       "log4j-async-perf.xml");

        
        deleteLogFiles();

        log4j2Logger = LogManager.getLogger(this.getClass());
        slf4jLogger = LoggerFactory.getLogger(this.getClass());
        log4j1Logger = org.apache.log4j.Logger.getLogger(this.getClass());
        lleLogger = LoggerFactory.getLogger("LLE");
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
    	chattyDelete(LOGBACK_LLE_ASYNC_FILE_PATH);
       	chattyDelete(LOG4J2_ASYNC_FILE_PATH);
       	chattyDelete(LOG4J_ASYNC_FILE_PATH);
    }
    
    private void chattyDelete(String path) {
    	final File file2Delete = new File(path);
    	if(!file2Delete.exists()) {
    		return;
    	}
        System.out.println("About to delete [" + path + "]");
    	boolean result = file2Delete.delete();
    	if(!result) 
    		System.out.println("Failed to delete " + path);
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
    public void logbackLleFile() {
        lleLogger.debug(MESSAGE);
    }
	
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void log4j2AsyncFile() {
    	log4j2Logger.debug(MESSAGE);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void log4j1File() {
        log4j1Logger.debug(MESSAGE);
    }
}

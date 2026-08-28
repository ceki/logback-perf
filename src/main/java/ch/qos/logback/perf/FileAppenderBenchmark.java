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
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

//import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


/**
 * Benchmarks Logback (and optionally other frameworks) using a FileAppender
 * writing to disk.
 *
 * IMPORTANT FOR LOW VARIANCE RESULTS:
 * - This benchmark is I/O bound. On a rotating hard disk (HDD), kernel
 *   writeback flusher behavior, seek times and rotational latency cause
 *   significant run-to-run variance even with identical parameters.
 * - For stable, low-variance numbers, point the "perfTarget" system property
 *   to a tmpfs (RAM disk) or a fast SSD. Example:
 *     -DperfTarget=/dev/shm/perf-target/
 */
// HOW TO RUN THIS TEST (recommended parameters for lower variance)
// java -jar target/benchmarks.jar "ch.qos.logback.perf.FileAppenderBenchmark.*" -f 5 -wi 5 -i 10 -r 10 -w 10 -tu ms -t 1
//
// With more threads:
// java -jar target/benchmarks.jar "ch.qos.logback.perf.FileAppenderBenchmark.*" -f 5 -wi 5 -i 10 -r 10 -w 10 -tu ms -t 4

// Historical observations (illustrative only):
// FileAppenderBenchmark.logbackFile  thrpt    4  2735.624 ± 869.311  ops/ms  (HD)   <-- high variance typical on HDD
// FileAppenderBenchmark.logbackFile  thrpt    4  7098.028 ± 1508.44  ops/ms  (SSD)


@State(Scope.Benchmark)
@Fork(value = 3)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class FileAppenderBenchmark {
    public static final String MESSAGE = "This is a debug message";
    public static String LOGBACK_FILE_PATH = "target/test-output/logback-perf.log";
    public static String LOG4J2_FILE_PATH = "target/test-output/log4j2-perf.log";
    public static String LOG4J_FILE_PATH = "target/test-output/log4j-perf.log";

    public static final String PERF_TARGET_KEY = "perfTarget";
    public static final String PERF_TARGET_DEFAULT_VALUE = "target/test-output/";

    public static final String[] REQUEST_IDS = prefixedIds("req-", 32);
    public static final String[] USER_IDS = prefixedIds("user-", 16);
    public static final String[] PATHS = {
            "/api/orders", "/api/users", "/api/items", "/api/health",
            "/api/cart", "/api/search", "/api/payments", "/api/inventory"
    };
    public static final String[] METHODS = {"GET", "POST"};
    public static final int[] STATUSES = {200, 200, 200, 404};
    public static final String[] OUTCOMES = {"ok", "ok", "ok", "miss"};
    public static final Throwable[] EXCEPTIONS = {
            new IllegalStateException("order conflict"),
            new IOException("upstream timeout", new SocketTimeoutException("read timed out")),
            new IllegalArgumentException("bad payload")
    };

    //Logger log4j2Logger;
    org.slf4j.Logger slf4jLogger;
    org.apache.log4j.Logger reload4jLogger;
    java.util.logging.Logger julLogger;
    String outFolder = "";
    protected String perfTarget = PERF_TARGET_DEFAULT_VALUE;

    @State(Scope.Thread)
    public static class ThreadState {
        int seq;

        @TearDown
        public void clearMdc() {
            MDC.clear();
        }
    }

    @Setup
    public void setUp() throws Exception {
        resolvePerfTarget();
        configureLoggingFrameworks();

        LOGBACK_FILE_PATH = perfTarget + logbackOutputFileName();
        LOG4J2_FILE_PATH  = perfTarget + "log4j2-perf.log";
        LOG4J_FILE_PATH   = perfTarget + "log4j-perf.log";

        outFolder = System.getProperty("outFolder", "");

        deleteLogFiles();

        slf4jLogger = LoggerFactory.getLogger(this.getClass().getName());
        //reload4jLogger = org.apache.log4j.Logger.getLogger(loggerName);
    }

    @TearDown
    public void tearDown() {
        clearFrameworkProperties();
        deleteLogFiles();
    }

    protected void resolvePerfTarget() {
        String value = System.getProperty(PERF_TARGET_KEY);
        if (value == null || value.trim().isEmpty()) {
            System.out.printf("System property '%s' is not set or empty. Using default value: '%s'%n", PERF_TARGET_KEY, PERF_TARGET_DEFAULT_VALUE);
            System.setProperty(PERF_TARGET_KEY, PERF_TARGET_DEFAULT_VALUE);
            value = PERF_TARGET_DEFAULT_VALUE;
        }
        this.perfTarget = value;
    }

    protected String logbackConfigurationFile() {
        return "logback-perf.xml";
    }

    protected String logbackOutputFileName() {
        return "logback-perf.log";
    }

    protected void configureLoggingFrameworks() {
        System.setProperty("log4j.configurationFile", "log4j2-perf.xml");
        System.setProperty("logback.configurationFile", logbackConfigurationFile());
        System.setProperty("log4j.configuration", "log4j-perf.xml");
    }

    protected void clearFrameworkProperties() {
        System.clearProperty("log4j.configurationFile");
        System.clearProperty("log4j.configuration");
        System.clearProperty("logback.configurationFile");
    }

    protected void deleteLogFiles() {
        final File logbackFile = new File(LOGBACK_FILE_PATH);

        long logbackFileLen = logbackFile.length();

        if(logbackFile.delete()) {
            System.out.println("Deleted logback file: " + logbackFile.getAbsolutePath() + " of length " + logbackFileLen);
        }

        final File log4jFile = new File(LOG4J_FILE_PATH);
        long log4jFileLen = log4jFile.length();

        if(log4jFile.delete()) {
            System.out.println("Deleted log4j file: " + log4jFile.getAbsolutePath() + " of length " + log4jFileLen);
        }

        final File log4j2File = new File(LOG4J2_FILE_PATH);
        long log4j2FileLen = log4j2File.length();
        if(log4j2File.delete()) {
            System.out.println("Deleted log4j2 file: " + log4j2File.getAbsolutePath()+" of length " + log4j2FileLen);
        }
    }

    //@BenchmarkMode(Mode.Throughput)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@Benchmark
    //public void log4j2File() {
    //    log4j2Logger.debug(MESSAGE);
    //}

    //@BenchmarkMode(Mode.Throughput)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@Benchmark
    public void logbackFile() {
        slf4jLogger.debug(MESSAGE);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void enhancedLogbackToFile(ThreadState ts) {
        logEnhanced(ts);
    }

    /**
     * Mixed structured-log workload: request-scoped MDC, per-event KVPs,
     * and a rare prebuilt exception. Exceptions are allocated once so the
     * benchmark measures encoding, not {@code fillInStackTrace()}.
     */
    protected void logEnhanced(ThreadState ts) {
        int i = ts.seq++;
        int req = i >>> 4;
        if ((i & 15) == 0) {
            MDC.put("requestId", REQUEST_IDS[req & (REQUEST_IDS.length - 1)]);
            MDC.put("userId", USER_IDS[(i >>> 6) & (USER_IDS.length - 1)]);
            MDC.put("httpPath", PATHS[req & (PATHS.length - 1)]);
        }

        String path = PATHS[req & (PATHS.length - 1)];
        String method = METHODS[i & (METHODS.length - 1)];

        if ((i & 31) == 0) {
            slf4jLogger.atError()
                    .addKeyValue("status", 500)
                    .addKeyValue("latencyMs", i % 50)
                    .addKeyValue("outcome", "error")
                    .addArgument(method)
                    .addArgument(path)
                    .setMessage("handled {} {}")
                    .setCause(EXCEPTIONS[(i >>> 5) % EXCEPTIONS.length])
                    .log();
            return;
        }

        slf4jLogger.atDebug()
                .addKeyValue("status", STATUSES[i % STATUSES.length])
                .addKeyValue("latencyMs", i % 50)
                .addKeyValue("outcome", OUTCOMES[i % OUTCOMES.length])
                .addArgument(method)
                .addArgument(path)
                .setMessage("handled {} {}")
                .log();
    }

    //@BenchmarkMode(Mode.Throughput)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@Benchmark
    public void reload4jFile() {
    	reload4jLogger.debug(MESSAGE);
    }

    private static String[] prefixedIds(String prefix, int n) {
        String[] ids = new String[n];
        for (int i = 0; i < n; i++) {
            ids[i] = prefix + i;
        }
        return ids;
    }
}

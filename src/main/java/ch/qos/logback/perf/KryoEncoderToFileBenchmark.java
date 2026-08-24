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

import static ch.qos.steward.core.stream.StewardCoreConstants.KRYO_UNSAFE_PROPERTY_KEY;

/**
 * Benchmarks Logback FileAppender writing Kryo-encoded events to disk via
 * {@code ch.qos.steward.classic.stream.KryoEncoder}.
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
// java -jar target/benchmarks.jar ".*KryoEncoderToFileBenchmark.*" -f 5 -wi 5 -i 10 -r 10 -w 10 -tu ms -t 1
//
// With more threads:
// java -jar target/benchmarks.jar ".*KryoEncoderToFileBenchmark.*" -f 5 -wi 5 -i 10 -r 10 -w 10 -tu ms -t 4

@State(Scope.Benchmark)
@Fork(value = 3)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class KryoEncoderToFileBenchmark {
    public static final String MESSAGE = "This is a debug message";
    public static String KRYO_FILE_PATH = "target/test-output/kryo-encoder-perf.kryo";

    public static final String PERF_TARGET_KEY = "perfTarget";
    public static final String PERF_TARGET_DEFAULT_VALUE = "target/test-output/";
    org.slf4j.Logger slf4jLogger;

    @Setup
    public void setUp() throws Exception {

        String perfTarget = System.getProperty(PERF_TARGET_KEY);
        if (perfTarget == null || perfTarget.trim().isEmpty()) {
            System.out.printf("System property '%s' is not set or empty. Using default value: '%s'%n", PERF_TARGET_KEY, PERF_TARGET_DEFAULT_VALUE);
            System.setProperty(PERF_TARGET_KEY, PERF_TARGET_DEFAULT_VALUE);
            perfTarget = System.getProperty(PERF_TARGET_KEY);
        }

        System.setProperty(KRYO_UNSAFE_PROPERTY_KEY, "false");
        System.setProperty("logback.configurationFile", "logback-kryo-perf.xml");

        KRYO_FILE_PATH = perfTarget + "kryo-encoder-perf.kryo";

        deleteLogFiles();

        String loggerName = "ch.qos.logback.perf.KryoEncoderToFileBenchmark";
        slf4jLogger = LoggerFactory.getLogger(loggerName);
    }

    @TearDown
    public void tearDown() {
        System.clearProperty("logback.configurationFile");
        System.clearProperty(KRYO_UNSAFE_PROPERTY_KEY);

        deleteLogFiles();
    }

    private void deleteLogFiles() {
        final File kryoFile = new File(KRYO_FILE_PATH);

        long kryoFileLen = kryoFile.length();

        if (kryoFile.delete()) {
            System.out.println("Deleted kryo file: " + kryoFile.getAbsolutePath() + " of length " + kryoFileLen);
        }
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void kryoEncoderFile() {
        slf4jLogger.debug(MESSAGE);
    }
}

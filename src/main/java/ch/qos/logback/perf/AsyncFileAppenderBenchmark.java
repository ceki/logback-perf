
/*
 * Inspired by https://github.com/mikeb01/qconsf2014 by Michael Barker
 */

package ch.qos.logback.perf;

import static ch.qos.logback.perf.CommonConstants.LOG4J2_CONFGIGURATION_FILE_KEY;
import static ch.qos.logback.perf.CommonConstants.LOG4J_CONFGIGURATION_FILE_KEY;
import static ch.qos.logback.perf.CommonConstants.LOGBACK_CONFGIGURATION_FILE_KEY;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.LoggerFactory;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmarks Log4j 2, Log4j 1, Logback and JUL using the DEBUG level which is
 * enabled for this test. The configuration for each uses a FileAppender
 */
// HOW TO RUN THIS TEST
// java -jar logback-perf/target/benchmarks.jar ".*AsyncFileAppenderBenchmark.*" -f 1
// -wi 10 -i 20 -bm avgt

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
public class AsyncFileAppenderBenchmark {
    public static final String MESSAGE = "This is a debug message";

    Logger log4j2Logger;
    Logger log4j2RandomLogger;
    org.slf4j.Logger slf4jLogger;
    org.apache.log4j.Logger log4j1Logger;
    java.util.logging.Logger julLogger;
    String outFolder = "";

    RamdomIndexedData randomIndexedData = new RamdomIndexedData();

    @Param({ "8", "10", "12", "14" })
    public int powerOf2;

    @Setup
    public void setUp() throws Exception {
        
        randomIndexedData.setup(powerOf2);

        System.setProperty(LOGBACK_CONFGIGURATION_FILE_KEY, "logback-async-file.xml");
        System.setProperty(LOG4J_CONFGIGURATION_FILE_KEY, "log4j1-async-file.xml");
        System.setProperty(LOG4J2_CONFGIGURATION_FILE_KEY, "log4j2-async-file.xml");

        outFolder = System.getProperty("outFolder", "");

        deleteLogFiles();

        log4j2Logger = LogManager.getLogger(AsyncFileAppenderBenchmark.class);
        log4j2RandomLogger = LogManager.getLogger("TestRandom");
        slf4jLogger = LoggerFactory.getLogger(AsyncFileAppenderBenchmark.class);
        log4j1Logger = org.apache.log4j.Logger.getLogger(AsyncFileAppenderBenchmark.class);

    }

    @TearDown
    public void tearDown() {
        System.clearProperty(LOGBACK_CONFGIGURATION_FILE_KEY);
        System.clearProperty(LOG4J_CONFGIGURATION_FILE_KEY);
        System.clearProperty(LOG4J2_CONFGIGURATION_FILE_KEY);

        // deleteLogFiles();
    }

    private void deleteLogFiles() {
        final File logbackFile = new File("target/testlogback.log");
        logbackFile.delete();

        final File log4jFile = new File("target/testlog4j.log");
        log4jFile.delete();

        final File log4jRandomFile = new File("target/testRandomlog4j2.log");
        log4jRandomFile.delete();

        final File log4j2File = new File("target/testlog4j2.log");
        log4j2File.delete();
    }
    
    @Benchmark
    public void baseline(Blackhole blackHole) {
        consumeRandomIndexedData(blackHole);
    }

    private void consumeRandomIndexedData(Blackhole blackHole) {
        for (final int i : randomIndexedData.indexes) {
            blackHole.consume(randomIndexedData.values[i]);
        }

    }

    @Benchmark
    public void log4j2RAF(Blackhole blackHole) {
        consumeRandomIndexedData(blackHole);
        log4j2RandomLogger.debug(MESSAGE);
    }

    @Benchmark
    public void logbackFile(Blackhole blackHole) {
        consumeRandomIndexedData(blackHole);
        slf4jLogger.debug(MESSAGE);
    }

    @Benchmark
    public void log4j1File(Blackhole blackHole) {
        consumeRandomIndexedData(blackHole);
        log4j1Logger.debug(MESSAGE);
    }
}

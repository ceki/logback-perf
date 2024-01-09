package ch.qos.logback.perf;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

// HOW TO RUN THIS TEST
// java -jar logback-perf/target/benchmarks.jar ".*DisabledLogger.*" -f 1 -wi 3 -i 3 -f 1

// Notable results
// Benchmark                       Mode  Cnt    Score   Error  Units
// DisabledLogger.direct           avgt    2   18.481          ns/op
// DisabledLogger.fluent           avgt    2   15.631          ns/op
// DisabledLogger.fluentLong       avgt    2   15.305          ns/op
//
// DisabledLogger.directMixed      avgt    2   98.102          ns/op
// DisabledLogger.fluentMixed      avgt    2  135.866          ns/op
// DisabledLogger.fluentLongMixed  avgt    2  160.515          ns/op


@State(Scope.Benchmark)
public class DisabledLogger {

    Logger logger;

    static int RUN_LENGTH = 100;

    static final String[] PARAMS = { "val0", "val1" };

    @Setup
    public void setUp() throws Exception {
        System.setProperty("logback.configurationFile", "logback-disabled.xml");
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @TearDown
    public void tearDown() {
        System.clearProperty("logback.configurationFile");
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void direct() {
        for (int i = 0; i < RUN_LENGTH; i++) {
            logger.debug("jello p0={}, p1={}", PARAMS[0], PARAMS[1]);
        }
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void directEnabled() {
        logger.info("jello p0={}, p1={}", PARAMS[0], PARAMS[1]);
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void directMixed() {
        logger.info("jello p0={}, p1={}", PARAMS[0], PARAMS[1]);
        for (int i = 0; i < RUN_LENGTH; i++) {
            logger.debug("jello p0={}, p1={}", PARAMS[0], PARAMS[1]);
        }
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluent() {
        for (int i = 0; i < RUN_LENGTH; i++) {
            logger.atDebug().addArgument(PARAMS[0]).addArgument(PARAMS[1]).setMessage("jello p0={}, p1={}").log();
        }
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluentEnabled() {
        logger.atInfo().addArgument(PARAMS[0]).addArgument(PARAMS[1]).setMessage("jello p0={}, p1={}").log();
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluentMixed() {
        logger.atInfo().addArgument(PARAMS[0]).addArgument(PARAMS[1]).setMessage("jello p0={}, p1={}").log();
        for (int i = 0; i < RUN_LENGTH; i++) {
            logger.atDebug().addArgument(PARAMS[0]).addArgument(PARAMS[1]).setMessage("jello p0={}, p1={}").log();
        }
    }


    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluentLong() {
        for (int i = 0; i < RUN_LENGTH; i++) {
            logger.atDebug().addArgument(PARAMS[0]).addArgument(PARAMS[1]).addKeyValue("a", "a").addKeyValue("b", "b")
                    .setMessage("jello p0={}, p1={}").log();
        }
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluentLongEnabled() {
        logger.atInfo().addArgument(PARAMS[0]).addArgument(PARAMS[1]).addKeyValue("a", "a").addKeyValue("b", "b")
                .setMessage("jello p0={}, p1={}").log();
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluentLongMixed() {
        logger.atInfo().addArgument(PARAMS[0]).addArgument(PARAMS[1]).addKeyValue("a", "a").addKeyValue("b", "b")
                .setMessage("jello p0={}, p1={}").log();
        for (int i = 0; i < RUN_LENGTH; i++) {
            logger.atDebug().addArgument(PARAMS[0]).addArgument(PARAMS[1]).addKeyValue("a", "a").addKeyValue("b", "b")
                    .setMessage("jello p0={}, p1={}").log();
        }
    }
}



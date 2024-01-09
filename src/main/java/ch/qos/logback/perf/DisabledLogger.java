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
// java -jar logback-perf/target/benchmarks.jar ".*DisabledLogger.*" -wi 3 -i 3 -f 3

// Notable results

// Benchmark                          Mode  Cnt   Score   Error  Units
// DisabledLogger.directDisabled      avgt   12   1.172 ± 0.049  ns/op
// DisabledLogger.directEnabled       avgt   12  68.901 ± 1.172  ns/op
// DisabledLogger.directMixed         avgt   12  71.301 ± 2.194  ns/op

// DisabledLogger.fluentDisabled      avgt   12   0.989 ± 0.030  ns/op
// DisabledLogger.fluentEnabled       avgt   12  88.098 ± 2.157  ns/op
// DisabledLogger.fluentMixed         avgt   12  89.583 ± 3.157  ns/op

// DisabledLogger.fluentLongDisabled  avgt   12   0.980 ± 0.012  ns/op
// DisabledLogger.fluentLongEnabled   avgt   12  95.781 ± 1.540  ns/op
// DisabledLogger.fluentLongMixed     avgt   12  99.017 ± 2.514  ns/op

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
    public void directDisabled() {
        logger.debug("jello p0={}, p1={}", PARAMS[0], PARAMS[1]);
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
        logger.debug("jello p0={}, p1={}", PARAMS[0], PARAMS[1]);
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluentDisabled() {
        logger.atDebug().addArgument(PARAMS[0]).addArgument(PARAMS[1]).setMessage("jello p0={}, p1={}").log();
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
        logger.atDebug().addArgument(PARAMS[0]).addArgument(PARAMS[1]).setMessage("jello p0={}, p1={}").log();
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluentLongDisabled() {
        logger.atDebug().addArgument(PARAMS[0]).addArgument(PARAMS[1]).addKeyValue("a", "a").addKeyValue("b", "b").setMessage("jello p0={}, p1={}")
                .log();
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluentLongEnabled() {
        logger.atInfo().addArgument(PARAMS[0]).addArgument(PARAMS[1]).addKeyValue("a", "a").addKeyValue("b", "b").setMessage("jello p0={}, p1={}")
                .log();
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Benchmark
    public void fluentLongMixed() {
        logger.atInfo().addArgument(PARAMS[0]).addArgument(PARAMS[1]).addKeyValue("a", "a").addKeyValue("b", "b").setMessage("jello p0={}, p1={}")
                .log();
        logger.atDebug().addArgument(PARAMS[0]).addArgument(PARAMS[1]).addKeyValue("a", "a").addKeyValue("b", "b").setMessage("jello p0={}, p1={}")
                .log();
    }
}



package ch.qos.logback.perf;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Benchmark LoggerFactory.getLogger() method
 * 
 * @author ceki
 *
 */
//HOW TO RUN THIS TEST
//java -jar logback-perf/target/benchmarks.jar ".*GetLoggerBenchmark.*" -f 1
//-wi 10 -i 20
//
//RUNNING THIS TEST WITH 4 THREADS:
//java -jar logback-perf/target/benchmarks.jar ".*GetLoggerBenchmark.*" -f 1
//-wi 10 -i 20 -t 4
@State(Scope.Thread)
public class GetLoggerBenchmark {

    
    @Setup
    public void setUp() throws Exception {
        System.setProperty("logback.configurationFile", "logback-short.xml");   
    }
    
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void getLogger() {
        Logger logger = LoggerFactory.getLogger(GetLoggerBenchmark.class);
        logger.trace("hello");
    }
}

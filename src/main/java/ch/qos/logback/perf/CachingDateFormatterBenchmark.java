package ch.qos.logback.perf;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.CachingDateFormatter;


/**
 * Benchmark ch.qos.logback.core.util.CachingDateFormatter.format method
 * 
 * @author ceki
 *
 */
//HOW TO RUN THIS TEST
//java -jar logback-perf/target/benchmarks.jar ".*CachingDateFormatterBenchmark.*" -f 1
//-wi 10 -i 20
//
//RUNNING THIS TEST WITH 4 THREADS:
//java -jar logback-perf/target/benchmarks.jar ".*CachingDateFormatterBenchmark.*" -f 1
//-wi 10 -i 20 -t 4
@State(Scope.Benchmark)
public class CachingDateFormatterBenchmark {


	CachingDateFormatter cachingDateFormatter;
	
    @Setup
    public void setUp() throws Exception {
    	cachingDateFormatter = new CachingDateFormatter(CoreConstants.ISO8601_PATTERN);
    }
    
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void format() {
    	cachingDateFormatter.format(System.currentTimeMillis());
    }
}

package ch.qos.logback.perf.caller;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import java.util.logging.LogRecord;
import java.util.logging.Level;

@State(Scope.Benchmark)
public class CallerComputeBenchmark {

	public String result;
	
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@Benchmark
	public void benchmarkgetCallingClassViaSecurityManager() {
		result = CallerCompute.getCallingClassViaSecurityManager(1);
	}

	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@Benchmark
	public void benchmarkgetCallerClassViaThreadAPI() {
		result = CallerCompute.getCallerClassViaThreadAPI(1);
	}

	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@Benchmark
	public void benchmarWalkerAPI() {
		LogRecord lr = new LogRecord(Level.INFO, "a");
		result = CallerCompute9.getCallerClass9(1);
	}

	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@Benchmark
	public void benchmarMethodHandler() {
		LogRecord lr = new LogRecord(Level.INFO, "a");
		result = CallerCompute.getCallingClassViaMethondHander(1);
	}

	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@Benchmark
	public void benchmarJULLogger() {
		LogRecord lr = new LogRecord(Level.INFO, "a");
		result = lr.getSourceClassName();
		
	}
}

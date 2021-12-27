package ch.qos.logback.perf.caller;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class CallerComputeBenchmark {

	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Benchmark
	public void benchmarkgetCallingClassViaSecurityManager() {
		String result = CallerCompute.getCallingClassViaSecurityManager(1);
	}

	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Benchmark
	public void benchmarkgetCallerClassViaThreadAPI() {
		String result = CallerCompute.getCallerClassViaThreadAPI(1);
	}

	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Benchmark
	public void benchmarWalkerAPI() {
		String result = CallerCompute9.getCallerClass9(1);
	}

}

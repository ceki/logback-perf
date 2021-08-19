package ch.qos.logback.perf;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.pattern.LoggerConverter;
import ch.qos.logback.classic.pattern.LoggerNameOnlyLoggingEvent;
import ch.qos.logback.classic.testUtil.Gaussian;
import ch.qos.logback.core.status.OnFileStatusListener;

/**
 * Benchmark LoggerNameBenchmark logger name abbreviation
 * 
 * @author ceki
 */

//HOW TO RUN THIS TEST
//java -jar logback-perf/target/benchmarks.jar ".*LoggerNameBenchmark.*" -f 1
//-wi 10 -i 20
//
//RUNNING THIS TEST WITH 4 THREADS:ja
//java -jar logback-perf/target/benchmarks.jar ".*GetLoggerBenchmark.*" -f 1
//-wi 10 -i 20 -t 4
@State(Scope.Thread)
public class LoggerNameBenchmark {

	static final String NAMES_FILE = "src/main/resources/fqcn.txt";

	@State(Scope.Benchmark)
	public static class MyState {
		List<String> namesList;

		int size;
		double mean;
		double deviation;
		Gaussian g;

		LoggerContext loggerContext = new LoggerContext();
		LoggerConverter loggerConverter = new LoggerConverter();

		LoggerNameOnlyLoggingEvent event = new LoggerNameOnlyLoggingEvent();

		Logger logger = LoggerFactory.getLogger(this.getClass());

		@Setup
		public void setUp() throws Exception {
			loadClassNames();
			setUpVariables();
		}
		
		public void loadClassNames() throws Exception {
			namesList = Files.lines(Paths.get(NAMES_FILE)).collect(Collectors.toList());

			size = namesList.size();
			mean = size / 16;
			deviation = mean / 2;
			g = new Gaussian(mean, deviation);
			System.out.println("names list size=" + size);
		}
		
		public void setUpVariables() {
			OnFileStatusListener ofsl = new OnFileStatusListener();
			ofsl.setContext(loggerContext);
			ofsl.setFilename("status.txt");
			ofsl.start();
			loggerContext.getStatusManager().add(ofsl);
			loggerConverter.setOptionList(List.of("30"));
			loggerConverter.setContext(loggerContext);
			loggerConverter.start();
		}

	}


	
	@BenchmarkMode(Mode.SampleTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	@Benchmark
	public void performAbbreviation(MyState state) {
		String fqn = getFQN(state);
		state.event.setLoggerName(fqn);
		state.loggerConverter.convert(state.event);
	}
	
	private String getFQN(MyState state) {
		while (true) {
			int index = (int) state.g.getGaussian();
			if (index >= 0 && index < state.size) {
				return state.namesList.get(index);
			} else {
				continue;
			}
		}
	}
}

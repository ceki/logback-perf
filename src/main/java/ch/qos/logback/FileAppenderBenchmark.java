package ch.qos.logback;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.LoggerFactory;


/**
 * Benchmarks Log4j 2, Log4j 1, Logback and JUL using the DEBUG level which is
 * enabled for this test. The configuration for each uses a FileAppender
 */
// HOW TO RUN THIS TEST
// java -jar logback-perf/target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 1
// -wi 10 -i 20
//
// RUNNING THIS TEST WITH 4 THREADS:
// java -jar logback-perf/target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 1
// -wi 10 -i 20 -t 4
@State(Scope.Thread)
public class FileAppenderBenchmark {
    public static final String MESSAGE = "This is a debug message";
    Logger log4j2RandomLogger;
    org.slf4j.Logger slf4jLogger;

    @Setup
    public void setUp() throws Exception {
        System.setProperty("log4j.configurationFile", "log4j2-perf.xml");
        System.setProperty("logback.configurationFile", "logback-perf.xml");

        deleteLogFiles();

        log4j2RandomLogger = LogManager.getLogger("TestRandom");
        slf4jLogger = LoggerFactory.getLogger(FileAppenderBenchmark.class);

    }

    @TearDown
    public void tearDown() {
        System.clearProperty("log4j.configurationFile");
        System.clearProperty("log4j.configuration");
        System.clearProperty("logback.configurationFile");

        deleteLogFiles();
    }

    private void deleteLogFiles() {
        final File logbackFile = new File("c:/tmp/testlogback.log");
        logbackFile.delete();
        final File log4jRandomFile = new File("target/testRandomlog4j2.log");
        log4jRandomFile.delete();
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void log4j2RAF() {
        log4j2RandomLogger.debug(MESSAGE);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void logbackFile() {
        slf4jLogger.debug(MESSAGE);
    }

    void warmUp() {
        for (int i = 0; i < 10000; i++) {
            slf4jLogger.debug(MESSAGE);
        }
    }

    void mainLoop() {
        int runLen = 100 * 1000;
        for (int i = 0; i < runLen; i++) {
            slf4jLogger.debug(MESSAGE);
        }

    }

    public static void main(String[] args) throws Exception {
        FileAppenderBenchmark bench = new FileAppenderBenchmark();
        bench.setUp();

        bench.warmUp();
        Thread.yield();
 
        int threadCount = 2;
        Runnable[] runnableArray = bench.buildRunnables(threadCount);
        bench.execute(runnableArray);
        
        bench.tearDown();

        System.out.println("Exiting FileAppenderBenchmark");
    }
  
    
    Runnable[] buildRunnables(int count) {
        Runnable[] ra = new Runnable[count];
        for(int i = 0; i < count; i++) {
            ra[i] = new MainLoopRunnable();
        }
        return ra;
    }
    
    class MainLoopRunnable implements Runnable {

        @Override
        public void run() {
            FileAppenderBenchmark.this.mainLoop();
        }
        
    }
    
    public void execute(Runnable[] runnableArray) throws InterruptedException {
        Thread[] threadArray = new Thread[runnableArray.length];

        for (int i = 0; i < runnableArray.length; i++) {
            threadArray[i] = new Thread(runnableArray[i], "Harness[" + i + "]");
        }
        for (Thread t : threadArray) {
            t.start();
        }

        for (Thread t : threadArray) {
            t.join();
        }
    }
}

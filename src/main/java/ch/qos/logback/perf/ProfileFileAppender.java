package ch.qos.logback.perf;

import java.io.File;

import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.LoggerFactory;

public class ProfileFileAppender {
    public static final String MESSAGE = "This is a debug message";
    org.slf4j.Logger slf4jLogger;

    @Setup
    public void setUp() throws Exception {
        System.setProperty("logback.configurationFile", "logback-perf.xml");
        deleteLogFiles();
        slf4jLogger = LoggerFactory.getLogger(ProfileFileAppender.class);

    }

    @TearDown
    public void tearDown() {
        System.clearProperty("log4j.configurationFile");
        System.clearProperty("log4j.configuration");
        System.clearProperty("logback.configurationFile");

        deleteLogFiles();
    }

    private void deleteLogFiles() {
        final File logbackFile = new File("target/testlogback.log");
        logbackFile.delete();
    }

    void warmUp() {
        for (int i = 0; i < 100000; i++) {
            slf4jLogger.debug(MESSAGE);
        }
    }

    void mainLoop() {
        int runLen = 1000 * 1000;
        for (int i = 0; i < runLen; i++) {
            slf4jLogger.debug(MESSAGE);
        }

    }

    public static void main(String[] args) throws Exception {
        ProfileFileAppender bench = new ProfileFileAppender();
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
            ProfileFileAppender.this.mainLoop();
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

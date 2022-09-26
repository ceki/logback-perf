package ch.qos.logback.perf.mdcAdapter;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.classic.util.LogbackMDCAdapterSimple;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.slf4j.spi.MDCAdapter;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class MDCAdapterBenchmark {


    public static final int NONE = 0;
    public static final int LONG = 256;
    public static final int SHORT = 16;

    LogbackMDCAdapter mdcAdapter = new LogbackMDCAdapter();
    LogbackMDCAdapterSimple mdcAdapterSimple = new LogbackMDCAdapterSimple();

    @Setup
    public void setUp() throws Exception {
    }

    //
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MICROSECONDS)
//    @Benchmark
//    public void benchmarkComplexMDCAdapterNone() {
//       run(mdcAdapter, NONE);
//    }
//
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MICROSECONDS)
//    @Benchmark
//    public void benchmarkComplexMDCAdapterShort() {
//        run(mdcAdapter, SHORT);
//    }
//
//
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MICROSECONDS)
//    @Benchmark
//    public void benchmarkComplexMDCAdapterLong() {
//        run(mdcAdapter, LONG);
//    }


//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MICROSECONDS)
//    @Benchmark
//    public void benchmarkSimpleMDCAdapterNone() {
//        run(mdcAdapterSimple, NONE);
//    }
//
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void benchmarkSimpleMDCAdapterShort() {
        run(mdcAdapterSimple, SHORT);
    }


    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void benchmarkSimpleMDCAdapterLong() {
        run(mdcAdapterSimple, LONG);
    }


    //    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MICROSECONDS)
//    @Benchmark
//    public void benchmarkFranzMDCAdapterNone() {
//        run(mdcAdapterFranz, NONE);
//    }
//


    private void run(MDCAdapter mdcAdapter, final int len) {
        mdcAdapter.put("REQUEST_ID", "0123456789012345678900123456789");
        mdcAdapter.put("REFERENCE", "ASADFASFSDFKLG");
        mdcAdapter.put("HOST", "toto.foo.com");

        if (mdcAdapter instanceof LogbackMDCAdapter) {
            LogbackMDCAdapter logbackMDCAdapter = (LogbackMDCAdapter) mdcAdapter;
            for (int i = 0; i < len; i++) {
                logbackMDCAdapter.getPropertyMap();
            }
            return;
        }

        if (mdcAdapter instanceof LogbackMDCAdapterSimple) {
            LogbackMDCAdapterSimple logbackMDCAdapterSimple = (LogbackMDCAdapterSimple) mdcAdapter;
            for (int i = 0; i < len; i++) {
                logbackMDCAdapterSimple.getPropertyMap();
            }
            return;
        }

    }

}

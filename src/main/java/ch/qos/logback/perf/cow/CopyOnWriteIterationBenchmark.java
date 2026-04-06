package ch.qos.logback.perf.cow;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
//@Fork(value = 3, jvmArgs = {"-Xms128M", "-Xmx128M", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseEpsilonGC"})
@Fork(value = 3)
@State(Scope.Thread)
public class CopyOnWriteIterationBenchmark {

    @Param({"0", "1", "2"})
    private int listSize;

    private CopyOnWriteArrayList<String> list;
    private int randomMultiplier;

    @Setup
    public void setup() {
        list = new CopyOnWriteArrayList<>();
        for (int i = 0; i < listSize; i++) {
            list.add("Element_" + i);
        }
        randomMultiplier = new Random(42).nextInt(1000) + 1;
    }

    @Benchmark
    public int usingToArray() {
        Object[] array = list.toArray();
        final int len = array.length;
        int sum = 0;
        for (int i = 0; i < len; i++) {
            sum += i * randomMultiplier;
        }
        return sum;
    }

    @Benchmark
    public int usingGet0() {
        int sum = 0;
        if(listSize > 0) {
            String element = list.get(0);
            sum += randomMultiplier;
        }
        return sum;
    }

    @Benchmark
    public int usingIterator() {
        int sum = 0;
        int i = 0;
        for (String element : list) {
            sum += i * randomMultiplier;
            i++;
        }
        return sum;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CopyOnWriteIterationBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}

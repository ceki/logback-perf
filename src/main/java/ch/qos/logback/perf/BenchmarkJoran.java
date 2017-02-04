/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.qos.logback.perf;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**

 wi: number of warmup iterations
 i:  umber of benchmarked iterations, use 10 or more to get a good idea
 tu: timeunit
 
 java -jar target/benchmarks.jar '.*BenchmarkJoran' -wi 0 -i 3 -tu us

Benchmark                                      Mode  Cnt      Score       Error  Units
BenchmarkJoran.testPropertySetterLongerConfig    ss   30  43885.034 ± 32561.156  us/op
BenchmarkJoran.testPropertySetterShortConfig     ss   30  33825.291 ± 29224.442  us/op
singleShotPropertySetterLongerConfig time in us:19526


1053831.706
Using java.beans.Introspector 
Benchmark                                      Mode  Cnt      Score       Error  Units
BenchmarkJoran.testPropertySetterLongerConfig    ss   30  49013.013 ± 37679.223  us/op
BenchmarkJoran.testPropertySetterShortConfig     ss   30  35981.958 ± 31635.148  us/op
 10 iterations avg. time: 20605 us
*/

//@State(Scope.Benchmark)
public class BenchmarkJoran {

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void testPropertySetterShortConfig() throws JoranException {
        LoggerContext loggerContext = new LoggerContext();
        JoranConfigurator joran = new JoranConfigurator();
        joran.setContext(loggerContext);
        joran.doConfigure("src/main/resources/logback-short.xml");
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void testPropertySetterLongerConfig() throws JoranException {
        LoggerContext loggerContext = new LoggerContext();
        JoranConfigurator joran = new JoranConfigurator();
        joran.setContext(loggerContext);
        joran.doConfigure("src/main/resources/logback-twoAppenders.xml");
    }

    @Test
    public void singleShotPropertySetterLongerConfig() throws JoranException {
        long start = System.nanoTime();
        int count = 10;
        for (int i = 0; i < count; i++) {
            LoggerContext loggerContext = new LoggerContext();
            JoranConfigurator joran = new JoranConfigurator();
            joran.setContext(loggerContext);
            joran.doConfigure("src/main/resources/logback-twoAppenders.xml");
        }
        long end = System.nanoTime();
        System.out.println("singleShotPropertySetterLongerConfig time in us:" + (end - start) / (count*1000));
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void testPropertySetterMinimalConfig() throws JoranException {
        LoggerContext loggerContext = new LoggerContext();
        JoranConfigurator joran = new JoranConfigurator();
        joran.setContext(loggerContext);
        joran.doConfigure("src/main/resources/logback-minimal.xml");
    }

}

# Logback benchmarking project

This mini project benchmark logback to optimize performance. It is
useful in comparing logback against other logging frameworks such as
log4j and log4j2.

We welcome all suggestions. You are also highly encouraged to run the
benchmarks in your own computing environment.

Once you change your current folder to the foder where you cloned the 
logback-perf project, the the command to build the benchmark is:
    
    mvn install; # this builds the benchmark under target/benchmarks.jar
    
The command to run the benchmark is:
        
    rm target/test-output/*; # make sure to delete any files from previous runs
    java -jar target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 2 -r 4 -tu ms -wi 2 -i 6 -t $TC`
    
where `$TC` stands for thread count, assuming the values in the set 
{1, 2, 4, 8, 16, 32, 64}.

Note that before running the benchmark, be sure to delete the files under 
the `target/test-output/` folder between runs.

Alternatively, you can run the `runFileAppenderBenchmark.sh` script which performs the
above steps for you. 

# Publicly verifiable results

In the this spirit of result verifiability, we encourage *everyone* to
run the `runFileAppenderBenchmak.sh` script and post their results on
the [logback mailing lists](https://logback.qos.ch/mailinglist.html).

Results are viewable as a [**Google doc spreadsheet**.](https://docs.google.com/spreadsheets/d/1cpb5D7qnyye4W0RTlHUnXedYK98catNZytYIu5D91m0/edit?usp=sharing)

We feel that results provided by neutral parties, that is by
developers not directly affiliated with the logback project, lends
more credibility to our benchmarking results.

See also the [logback benchmark page](http://logback.qos.ch/performance.html) for a 
general discussion about the logic behind the benchmark and analysis of the results.




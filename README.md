# Logback benchmarking project

This mini benchmark project is intended to help improve logback's performance. It isa
also useful in comparing logback against other logging frameworks such as
log4j and log4j2.

Please refer to the [logback benchmark page](http://logback.qos.ch/performance.html) for a 
general discussion about the logic behind this benchmark and an analysis of the results.

Results are also viewable as a [**Google doc spreadsheet**.](https://docs.google.com/spreadsheets/d/1cpb5D7qnyye4W0RTlHUnXedYK98catNZytYIu5D91m0/edit?usp=sharing)

# Do run the benchmark on your own

We welcome all suggestions. You are also highly encouraged to run the
benchmarks in your own computing environment and file a pull request.

Once you change your current folder to the foder where you cloned the 
logback-perf project, the the command to build the benchmark is:
    
    mvn install; # this builds the benchmark under target/benchmarks.jar
    
The command to run the benchmark is:
        
    rm target/test-output/*; # make sure to delete any files from previous runs
    java -jar target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 1 -tu ms -wi 2 -i 4 -to 3 -t $TC`
    
where `$TC` stands for thread count, assuming the values in the set 
{1, 2, 4, 8, 16, 32, 64}.

Note that before running the benchmark, be sure to delete the files under 
the `target/test-output/` folder between runs.

Alternatively, you can run the `runFileAppenderBenchmark.sh` script which performs the
clean up, and saves the results in timestamped files.

In the this spirit of result verifiability, we encourage *everyone* to
run the `runFileAppenderBenchmak.sh` script and add their results under 
the `results` folder and a sub-folder named after the CPU/host where the 
test was run. Please send a pull request with your results.

We feel that results provided by neutral parties, that is by
developers not directly affiliated with the logback project, lends
more credibility to our benchmarking results.



# Logback benchmarking project

This mini project benchmark logback to optimize performance. It is
useful in comparing logback against other logging frameworks such as
log4j and log4j2.

We welcome all suggestions. You are also highly encourages to run the
benchmarks on your own computing environment.

The command is:

   java -jar target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 2 -r 4 -tu ms -wi 2 -i 6 -t $TC

where $TC stand for thread count.

# Publicly verifiable results

Results are viewable as a [**Google doc spreadsheet**.](https://docs.google.com/spreadsheets/d/1cpb5D7qnyye4W0RTlHUnXedYK98catNZytYIu5D91m0/edit?usp=sharing)

We feel that results provided by neutral parties, that is by
developers not directly affiliated with the logback project, lends
more credibility to our benchmarking results.

In the this spirit of result verifiability, we encourage *everyone* to
run the `runFileAppenderBenchmak.sh` script and post their results on
the [logback mailing lists](https://logback.qos.ch/mailinglist.html).




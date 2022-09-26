
DATE=$(date  '+%Y-%m-%dT%H%M')

echo "Results will be output into file results-${DATE}-[threadCount].txt"
sleep 1

DATE=$(date  '+%Y-%m-%dT%H%M')

#for TC in 1 2 4 8 16 32 64;
mvn clean
mvn install

# default run duration of 10 seconds, single fork, time unit: microseconds, 2 warm up iterations, 4 iterations,
# $TC is the thread count, timeout 3 seconds, output results to "results-${DATE}-$TC.txt"
DURATION=10
T=1
TIMEOUT=30
java -jar target/benchmarks.jar "ch.qos.logback.perf.mdcAdapter.MDCAdapterBenchmark" -r $DURATION -f 1 -tu ns -w $DURATION -wi 3 -i 4 -t $T -to $TIMEOUT #-o "results-${DATE}-$TC.txt" 



 


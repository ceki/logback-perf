
DATE=$(date  '+%Y-%m-%dT%H%M')

echo "Results will be output into file results-${DATE}-[threadCount].csv"
sleep 1

DATE=$(date  '+%Y-%m-%dT%H%M')

for TC in 1 2 4 8 16 32 64;
do
    echo "Number of threads $TC"
    mvn clean
    mvn install
    java -jar target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 2 -r 4 -tu ms -wi 3 -i 6 -t $TC -rff "results-${DATE}-$TC.csv" -rf csv
done
 


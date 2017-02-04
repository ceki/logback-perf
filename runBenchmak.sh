

#rm /tmp/target/test*.log;

for numThreads in 1 2; # 4 8 16 32 64;
do
    echo Number of threads: $i
    $JAVA_HOME/bin/java -jar target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 1  -tu ms -wi 5 -i 5 -t $numThreads -rff tmp.csv -rf csv
    cat tmp.csv >> results.csv
done


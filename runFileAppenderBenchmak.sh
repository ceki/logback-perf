

# by default test ALL methods in FileAppenderBenchmark
METHOD=log 
BENCHARK_TYPE=regular

if [ ! -z $1 ]
then
  METHOD=$1
   echo "Will run benchmark methods in FileAppenderBenchmark with names starting with [$METHOD]"
else
  echo "Will run all benchmark methods in FileAppenderBenchmark"
fi

if [ ! -z $2 ]
then
   BENCHARK_TYPE=$2
   echo "Setting benchark type to [$BENCHARK_TYPE]"
fi

echo "Results will be output into file $METHOD.csv"
sleep 1


for numThreads in 1 2 4 8 16 32 64;
do
    rm /tmp/target/test*.log;
    echo "Number of threads $numThreads"
    $JAVA_HOME/bin/java -jar target/benchmarks.jar ".*FileAppenderBenchmark.${METHOD}" -f 1  -tu ms -wi 5 -i 10 -t $numThreads -rff tmp.csv -rf csv -p benchmarkType=$BENCHARK_TYPE
    cat tmp.csv >> $METHOD.csv
    rm tmp.csv
done



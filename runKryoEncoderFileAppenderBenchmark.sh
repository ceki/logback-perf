#!/bin/bash
#
# Runs the FileAppenderBenchmark with parameters tuned for lower variance.
#
# CRITICAL NOTE ON VARIANCE:
#   The benchmark writes to disk via a buffered FileAppender (immediateFlush=false).
#   When PERF_TARGET points to a rotating hard disk, the Linux kernel's
#   writeback flusher, combined with HDD seek/rotational latency, produces
#   significant measurement jitter. This is expected physics, not a bug in
#   Logback or the benchmark.
#
#   For the most stable results, run with PERF_TARGET on tmpfs or SSD:
#     PERF_TARGET=/dev/shm/logback-perf-target/   ./runFileAppenderBenchmark.sh
#
# To create a ramdisk on Linux, you can use the following command (requires root privileges):
# sudo mount -t tmpfs -o size=1G tmpfs /mnt/ramdisk

if [ -z "${MVN+set}" ]
then
    echo "Error: The environment variable MVN is not defined."
    echo "Please define it so that it points to the Maven binary and try again. "
    exit 1
fi

DATE=$(date  '+%Y-%m-%dT%H%M')


echo "Results will be output into file results-${DATE}-[threadCount].txt"
sleep 1

DATE=$(date  '+%Y-%m-%dT%H%M')
#PERF_TARGET=/tmp/perf-target/
PERF_TARGET=/mnt/ramdisk/perf-target/

$MVN clean
$MVN install

#for TC in 1 2 4 8 16 32 64;
for TC in 1 8;
do
    echo "Number of threads $TC"
    #
    # IMPORTANT: The parameters below are chosen to reduce variance.
    # - Multiple forks (-f 5) + real warm-up are essential for stable JMH results.
    # - On a rotating hard disk (PERF_TARGET=/tmp/perf-target/), variance will
    #   remain higher than on SSD or tmpfs due to mechanical + kernel writeback effects.
    #   For lowest possible variance, use a RAM disk, e.g.:
    #     PERF_TARGET=/dev/shm/perf-target/
    #
    DURATION=3
    ITERATIONS=3
    FORKS=3
    java -DperfTarget=${PERF_TARGET} -jar target/benchmarks.jar \
      "ch.qos.logback.perf.KryoEncoderToFileBenchmark.*" \
      -f $FORKS -tu ms -wi $ITERATIONS -i $ITERATIONS -r $DURATION -w $DURATION -t $TC -to 5
done


 


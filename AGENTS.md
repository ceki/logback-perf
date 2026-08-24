# AGENTS.md

## Overview
Logback benchmarking project using JMH (Java Microbenchmark Harness).

## Build and Run
- **Build**: `mvn install` (produces `target/benchmarks.jar`)
- **Run Benchmarks (Manual)**:
  1. Clean output: `rm target/test-output/*`
  2. Execute: `java -jar target/benchmarks.jar "<BenchmarkClassNameRegex>" [options]`
  3. Example (for lower variance): `java -jar target/benchmarks.jar ".*FileAppenderBenchmark.*" -f 5 -tu ms -wi 5 -i 10 -r 10 -w 10 -t 1`

   **Note on variance**: FileAppenderBenchmark writes to disk. On rotating hard disks
   (e.g. /tmp/perf-target/), kernel writeback + mechanical latency cause high variance.
   Use tmpfs or SSD for stable results.

- **Run Benchmarks (Scripts)**:
  - Use available `.sh` scripts in the root (e.g., `runFileAppenderBenchmark.sh`, `runMDCAdapter.sh`).
  - **Note**: `runFileAppenderBenchmark.sh` requires the `MVN` environment variable to be set to the Maven path (e.g., `/home/ceki/java/maven-3.9.11/bin/mvn`).

## Project Structure
- **Benchmarks**: `src/main/java/ch/qos/logback/perf/`
- **Resources**: `src/main/resources/` (contains configuration for various logging frameworks)
- **Results**: Output should be saved to the `results/` directory.

## Development Details
- **JDK Version**: 21
- **Build Tool**: Maven
- **Benchmarking Tool**: JMH
- **Tests**: Some tests are located in `src/main/java` instead of `src/test/java`.

Maven can be located via the value of the $MAVEN_HOME environment variable. 
Java can ve located via the value of the $JAVA_HOME environment variable.

Indicate whether these values are accessible or not.

If valid, always use the values of these environment variables to
locate Maven and Java.

The logback-steward project can be found at `/home/ceki/logback-steward/`.
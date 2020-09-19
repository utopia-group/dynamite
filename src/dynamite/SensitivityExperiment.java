package dynamite;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import dynamite.core.IInstance;
import dynamite.core.ISchema;
import dynamite.core.SchemaMapping;
import dynamite.datalog.OptimalityStatsVisitor;
import dynamite.datalog.ProgramStatsVisitor;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.parser.AntlrDatalogParser;
import dynamite.exp.IntegrityConstraint;
import dynamite.exp.IntegrityConstraintParser;
import dynamite.exp.SensitivityAnalysis;
import dynamite.util.FileUtils;

public final class SensitivityExperiment {

    // timeout in seconds for each benchmark
    public static final int SENSITIVITY_TIMEOUT = 600;
    // maximum size of random instances
    public static final int MAXIMUM_SIZE = 8;
    // starting random seed for generating random instances
    public static final int RANDOM_SEED = 666;
    // number of runs for each size with different random seeds
    public static final int RUN_COUNT = 100;

    public static void main(String[] args) {
        runBenchmark("benchmark1", 8);
        runBenchmark("benchmark2", 9);
        runBenchmark("benchmark3", 9);
        runBenchmark("benchmark4", 25);
        runBenchmark("benchmark5", 4);
        runBenchmark("benchmark6", 3);
        runBenchmark("benchmark7", 5);
        runBenchmark("benchmark8", 5);
        runBenchmark("benchmark9", 1);
        runBenchmark("benchmark10", 2);
        runBenchmark("benchmark11", 5);
        runBenchmark("benchmark12", 5);
        runBenchmark("benchmark13", 1);
        runBenchmark("benchmark14", 2);
        runBenchmark("benchmark15", 2);
        runBenchmark("benchmark16", 2);
        runBenchmark("benchmark17", 1);
        runBenchmark("benchmark18", 1);
        runBenchmark("benchmark19", 1);
        runBenchmark("benchmark20", 3);
        runBenchmark("benchmark21", 1);
        runBenchmark("benchmark22", 1);
        runBenchmark("benchmark23", 1);
        runBenchmark("benchmark24", 1);
        runBenchmark("benchmark25", 4);
        runBenchmark("benchmark26", 3);
        runBenchmark("benchmark27", 5);
        runBenchmark("benchmark28", 5);
        System.out.println("Done");
        System.exit(0);
    }

    public static void runBenchmark(String benchmark, int benchmarkCount) {
        for (int size = 1; size <= MAXIMUM_SIZE; ++size) {
            FileUtils.createDirectory(Paths.get(String.format("logs/%s/%d", benchmark, size)));
            runBenchmarkWithSize(benchmark, benchmarkCount, size);
        }
    }

    public static void runBenchmarkWithSize(String benchmark, int benchmarkCount, int size) {
        for (long seed = RANDOM_SEED; seed < RANDOM_SEED + RUN_COUNT; ++seed) {
            String filePath = String.format("logs/%s/%d/%d.log", benchmark, size, seed);
            Logger logger = LoggerWrapper.getFileLogger(filePath);
            runBenchmarkWithSizeAndSeed(logger, benchmark, benchmarkCount, size, seed);
            closeFileHandlers(logger);
        }
    }

    public static void runBenchmarkWithSizeAndSeed(Logger logger, String benchmark, int benchmarkCount, int size, long randomSeed) {
        System.out.println(String.format("Running %s with size %d seed %d", benchmark, size, randomSeed));
        logger.info("=== Benchmark: " + benchmark);
        logger.info("=== Size: " + size);
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Void> task = new Callable<Void>() {
            @Override
            public Void call() {
                for (int i = 1; i <= benchmarkCount; ++i) {
                    String benchmarkName = benchmark + "_" + i;
                    System.out.println(benchmarkName);
                    runSensitivityAnalysis(logger, benchmarkName, size, randomSeed);
                }
                return null;
            }
        };
        Future<Void> future = executor.submit(task);
        try {
            future.get(SENSITIVITY_TIMEOUT, TimeUnit.SECONDS);
            logger.info("=== Status: Success");
        } catch (TimeoutException e) {
            e.printStackTrace();
            logger.info("=== Status: Timeout");
            System.out.println("Timeout");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logger.info("=== Status: Failure");
            System.out.println("Failure");
        } finally {
            future.cancel(true); // cancel the task
        }
        logger.info("=== Elapsed Time: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    public static void runSensitivityAnalysis(Logger logger, String benchmark, int size, long randomSeed) {
        logger.info("=== Sub Benchmark: " + benchmark);
        String majorBenchmarkName = getMajorBenchmarkName(benchmark);
        String benchmarkDir = String.format("benchmarks/%s/%s/", majorBenchmarkName, benchmark);
        File dir = new File(benchmarkDir);
        String srcSchemaPath = ExperimentUtils.getFilePathByPrefix(dir, "SrcSchema");
        String tgtSchemaPath = ExperimentUtils.getFilePathByPrefix(dir, "TgtSchema");
        String goldenSrcExPath = ExperimentUtils.getFilePathByPrefix(dir, "SrcExample");
        String goldenTgtExPath = ExperimentUtils.getFilePathByPrefix(dir, "TgtExample");
        String goldenProgPath = String.format("benchmarks/golden/%s/%s.dl", majorBenchmarkName, benchmark);
        String constraintPath = String.format("benchmarks/constraint/%s/%s.txt", majorBenchmarkName, benchmark);

        ISchema srcSchema = SchemaMapper.parseSchema(srcSchemaPath);
        ISchema tgtSchema = SchemaMapper.parseSchema(tgtSchemaPath);
        IInstance goldenSrcExample = SchemaMapper.parseExample(srcSchema, goldenSrcExPath);
        IInstance goldenTgtExample = SchemaMapper.parseExample(tgtSchema, goldenTgtExPath);
        String programText = FileUtils.readFromFile(goldenProgPath);
        DatalogProgram goldenProgram = AntlrDatalogParser.parse(programText);
        String constraintText = FileUtils.readFromFile(constraintPath);
        IntegrityConstraint constraint = IntegrityConstraintParser.parse(constraintText);

        SchemaMapping schemaMapping = SensitivityAnalysis.mapSchema(logger, benchmark, srcSchema, tgtSchema, constraint, goldenProgram, size, randomSeed);
        if (schemaMapping != null) {
            logStatsInformation(logger, schemaMapping);
            logOptimalityInformation(logger, schemaMapping, goldenProgram);
        }
        // check if the synthesized schema mapping exists
        ExperimentUtils.assertNotNull(schemaMapping);
        // check if the synthesized program is consistent with manually provided examples
        ExperimentUtils.assertConsistency(schemaMapping.programWithoutFacts, goldenSrcExample, goldenTgtExample);
    }

    private static String getMajorBenchmarkName(String benchmark) {
        int index = benchmark.indexOf('_');
        assert index > 0;
        return benchmark.substring(0, index);
    }

    private static void logStatsInformation(Logger logger, SchemaMapping schemaMapping) {
        ProgramStatsVisitor progStatsVisitor = new ProgramStatsVisitor();
        schemaMapping.program.accept(progStatsVisitor);
        logger.info("=== Rule Count: " + progStatsVisitor.getRuleCount());
        logger.info("=== Predicate Count: " + progStatsVisitor.getPredCount());
    }

    private static void logOptimalityInformation(Logger logger, SchemaMapping schemaMapping, DatalogProgram goldenProgram) {
        OptimalityStatsVisitor visitor = new OptimalityStatsVisitor(goldenProgram);
        schemaMapping.program.accept(visitor);
        logger.info("=== Optimal Rule Count: " + visitor.getOptimalRuleCount());
        logger.info("=== Optimality Distance: " + visitor.getOptimalityDifference());
    }

    private static void closeFileHandlers(Logger logger) {
        for (Handler handler : logger.getHandlers()) {
            handler.close();
        }
    }

}

package dynamite;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import dynamite.core.IInstance;
import dynamite.core.ISchema;
import dynamite.core.SchemaMapping;
import dynamite.datalog.DatalogOutput;
import dynamite.datalog.OptimalityStatsVisitor;
import dynamite.datalog.ProgramStatsVisitor;
import dynamite.datalog.SouffleEvaluator;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.parser.AntlrDatalogParser;
import dynamite.util.FileUtils;

public final class SynthesisExperiment {

    private static final Logger LOGGER = LoggerWrapper.getInstance();

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
        System.out.println("Running " + benchmark);
        LOGGER.info("=== Benchmark: " + benchmark);
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Void> task = new Callable<Void>() {
            @Override
            public Void call() {
                for (int i = 1; i <= benchmarkCount; ++i) {
                    String benchmarkName = benchmark + "_" + i;
                    runSchemaMapper(benchmarkName);
                }
                return null;
            }
        };
        Future<Void> future = executor.submit(task);
        try {
            future.get(Config.SYNTH_TIMEOUT, TimeUnit.SECONDS);
            LOGGER.info("=== Status: Success");
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            LOGGER.info("=== Status: Timeout");
            System.out.println("Timeout");
        } finally {
            future.cancel(true); // cancel the task
        }
        LOGGER.info("=== Elapsed Time: " + (System.currentTimeMillis() - startTime));
    }

    public static void runSchemaMapper(String benchmark) {
        LOGGER.info("=== Sub Benchmark: " + benchmark);
        long startTime = System.currentTimeMillis();
        String majorBenchmarkName = getMajorBenchmarkName(benchmark);
        String benchmarkDir = String.format("benchmarks/%s/%s/", majorBenchmarkName, benchmark);
        File dir = new File(benchmarkDir);
        String srcSchemaPath = ExperimentUtils.getFilePathByPrefix(dir, "SrcSchema");
        String tgtSchemaPath = ExperimentUtils.getFilePathByPrefix(dir, "TgtSchema");
        String srcExamplePath = ExperimentUtils.getFilePathByPrefix(dir, "SrcExample");
        String tgtExamplePath = ExperimentUtils.getFilePathByPrefix(dir, "TgtExample");
        ISchema srcSchema = SchemaMapper.parseSchema(srcSchemaPath);
        ISchema tgtSchema = SchemaMapper.parseSchema(tgtSchemaPath);
        IInstance srcExample = SchemaMapper.parseExample(srcSchema, srcExamplePath);
        IInstance tgtExample = SchemaMapper.parseExample(tgtSchema, tgtExamplePath);
        SchemaMapping schemaMapping = SchemaMapper.mapSchema(srcSchema, tgtSchema, srcExample, tgtExample);
        LOGGER.fine("--- Simplified Program:\n" + schemaMapping);
        if (schemaMapping != null) {
            logStatsInformation(schemaMapping);
            logOptimalityInformation(schemaMapping, benchmark);
        }
        LOGGER.info("=== Sub Time: " + (System.currentTimeMillis() - startTime));
        // check if the synthesized schema mapping is correct
        ExperimentUtils.assertNotNull(schemaMapping);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = tgtExample.toDocumentInstance().toDatalogOutputWithoutUniverse();
        ExperimentUtils.assertEquals(expected, actual);
    }

    private static String getMajorBenchmarkName(String benchmark) {
        int index = benchmark.indexOf('_');
        assert index > 0;
        return benchmark.substring(0, index);
    }

    private static void logStatsInformation(SchemaMapping schemaMapping) {
        ProgramStatsVisitor progStatsVisitor = new ProgramStatsVisitor();
        schemaMapping.program.accept(progStatsVisitor);
        LOGGER.info("=== Rule Count: " + progStatsVisitor.getRuleCount());
        LOGGER.info("=== Predicate Count: " + progStatsVisitor.getPredCount());
    }

    private static void logOptimalityInformation(SchemaMapping schemaMapping, String benchmark) {
        String majorBenchmarkName = getMajorBenchmarkName(benchmark);
        String goldenProgPath = String.format("benchmarks/golden/%s/%s.dl", majorBenchmarkName, benchmark);
        String goldenText = FileUtils.readFromFile(goldenProgPath);
        DatalogProgram goldenProgram = AntlrDatalogParser.parse(goldenText);
        OptimalityStatsVisitor visitor = new OptimalityStatsVisitor(goldenProgram);
        schemaMapping.program.accept(visitor);
        LOGGER.info("=== Optimal Rule Count: " + visitor.getOptimalRuleCount());
        LOGGER.info("=== Optimality Distance: " + visitor.getOptimalityDifference());
    }

}

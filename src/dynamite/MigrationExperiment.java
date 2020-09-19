package dynamite;

import java.io.File;

import dynamite.core.IInstance;
import dynamite.core.ISchema;
import dynamite.core.SchemaMapping;

public final class MigrationExperiment {

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

    public static void runBenchmark(String majorName, int subBenchmarkCount) {
        System.out.println("Running " + majorName);
        String factDir = String.format("tmp/Souffle_input/%s/", majorName);
        generateFactsForBenchmark(majorName, factDir);
        for (int subBenchmark = 1; subBenchmark <= subBenchmarkCount; subBenchmark++) {
            runSubBenchmark(majorName, subBenchmark, factDir);
        }
    }

    public static void generateFactsForBenchmark(String majorName, String factDir) {
        String benchmarkDir = String.format("benchmarks/%s/", majorName);
        File dirFile = new File(benchmarkDir);
        String srcInstSchemaPath = ExperimentUtils.getFilePathByPrefix(dirFile, "SrcInstSchema");
        File instancesFolder = new File(benchmarkDir + "SrcInstances/");
        File[] instances = instancesFolder.listFiles();
        if (instances == null || instances.length < 1) {
            throw new IllegalArgumentException(String.format("Expected instances in %s", benchmarkDir));
        }
        long factStartTime = System.currentTimeMillis();
        for (int i = 0; i < instances.length; i++) {
            File instanceFile = instances[i];
            System.out.println("Generating facts from " + instanceFile);
            DataMigration.generateFacts(instanceFile.getPath(), srcInstSchemaPath, factDir);
        }
        long factEndTime = System.currentTimeMillis();
        long factDuration = (factEndTime - factStartTime);
        System.out.println(String.format("=== Fact Gen Time: %d s", factDuration / 1000));
    }

    public static void runSubBenchmark(String majorName, int subBenchmark, String factDir) {
        System.out.println("=== Sub Benchmark: " + subBenchmark);
        String subBenchmarkDir = String.format("benchmarks/%s/%s_%d", majorName, majorName, subBenchmark);
        File subDirFile = new File(subBenchmarkDir);
        long synthStartTime = System.currentTimeMillis();
        String srcSchemaPath = ExperimentUtils.getFilePathByPrefix(subDirFile, "SrcSchema");
        String tgtSchemaPath = ExperimentUtils.getFilePathByPrefix(subDirFile, "TgtSchema");
        String srcExamplePath = ExperimentUtils.getFilePathByPrefix(subDirFile, "SrcExample");
        String tgtExamplePath = ExperimentUtils.getFilePathByPrefix(subDirFile, "TgtExample");
        ISchema srcSchema = SchemaMapper.parseSchema(srcSchemaPath);
        ISchema tgtSchema = SchemaMapper.parseSchema(tgtSchemaPath);
        IInstance srcExample = SchemaMapper.parseExample(srcSchema, srcExamplePath);
        IInstance tgtExample = SchemaMapper.parseExample(tgtSchema, tgtExamplePath);
        SchemaMapping schemaMapping = SchemaMapper.mapSchema(srcSchema, tgtSchema, srcExample, tgtExample);
        ExperimentUtils.assertNotNull(schemaMapping);
        System.out.println("=== Synth Time: " + (System.currentTimeMillis() - synthStartTime) / 1000);
        long evalStartTime = System.currentTimeMillis();
        String outputPath = String.format("tmp/%s/", subBenchmarkDir);
        DataMigration.evaluateProgram(schemaMapping, outputPath, factDir);
        System.out.println("=== Eval Time: " + (System.currentTimeMillis() - evalStartTime) / 1000);
        long postStartTime = System.currentTimeMillis();
        DataMigration.postprocessProgramOutput(tgtSchema, schemaMapping, outputPath);
        System.out.println("=== Post Time: " + (System.currentTimeMillis() - postStartTime) / 1000);
        // assert the output is not empty
        ExperimentUtils.assertFilesNotEmpty(outputPath);
    }

}

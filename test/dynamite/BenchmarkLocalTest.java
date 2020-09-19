package dynamite;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dynamite.core.IInstance;
import dynamite.core.ISchema;
import dynamite.core.SchemaMapping;
import dynamite.datalog.DatalogOutput;
import dynamite.datalog.SouffleEvaluator;

@RunWith(Parameterized.class)
public class BenchmarkLocalTest {

    @Parameters(name = "{0}")
    public static Iterable<? extends Object> data() {
        return Arrays.asList(new Object[] {
                // benchmark 1
                "benchmark1_1",
                "benchmark1_2",
                "benchmark1_3",
                "benchmark1_4",
                "benchmark1_5",
                "benchmark1_6",
                "benchmark1_7",
                "benchmark1_8",
                // benchmark 2
                "benchmark2_1",
                "benchmark2_2",
                "benchmark2_3",
                "benchmark2_4",
                "benchmark2_5",
                "benchmark2_6",
                "benchmark2_7",
                "benchmark2_8",
                "benchmark2_9",
                // benchmark 3
                "benchmark3_1",
                "benchmark3_2",
                "benchmark3_3",
                "benchmark3_4",
                "benchmark3_5",
                "benchmark3_6",
                "benchmark3_7",
                "benchmark3_8",
                "benchmark3_9",
                // benchmark 4
                "benchmark4_1",
                "benchmark4_2",
                "benchmark4_3",
                "benchmark4_4",
                "benchmark4_5",
                "benchmark4_6",
                "benchmark4_7",
                "benchmark4_8",
                "benchmark4_9",
                "benchmark4_10",
                "benchmark4_11",
                "benchmark4_12",
                "benchmark4_13",
                "benchmark4_14",
                "benchmark4_15",
                "benchmark4_16",
                "benchmark4_17",
                "benchmark4_18",
                "benchmark4_19",
                "benchmark4_20",
                "benchmark4_21",
                "benchmark4_22",
                "benchmark4_23",
                "benchmark4_24",
                "benchmark4_25",
                // benchmark 5
                "benchmark5_1",
                "benchmark5_2",
                "benchmark5_3",
                "benchmark5_4",
                // benchmark 6
                "benchmark6_1",
                "benchmark6_2",
                "benchmark6_3",
                // benchmark 7
                "benchmark7_1",
                "benchmark7_2",
                "benchmark7_3",
                "benchmark7_4",
                "benchmark7_5",
                // benchmark 8
                "benchmark8_1",
                "benchmark8_2",
                "benchmark8_3",
                "benchmark8_4",
                "benchmark8_5",
                // benchmark 9
                "benchmark9_1",
                // benchmark 10
                "benchmark10_1",
                "benchmark10_2",
                // benchmark 11
                "benchmark11_1",
                "benchmark11_2",
                "benchmark11_3",
                "benchmark11_4",
                "benchmark11_5",
                // benchmark 12
                "benchmark12_1",
                "benchmark12_2",
                "benchmark12_3",
                "benchmark12_4",
                "benchmark12_5",
                // benchmark 13
                "benchmark13_1",
                // benchmark 14
                "benchmark14_1",
                "benchmark14_2",
                // benchmark 15
                "benchmark15_1",
                "benchmark15_2",
                // benchmark 16
                "benchmark16_1",
                "benchmark16_2",
                // benchmark 17
                "benchmark17_1",
                // benchmark 18
                "benchmark18_1",
                // benchmark 19
                "benchmark19_1",
                // benchmark 20
                "benchmark20_1",
                "benchmark20_2",
                "benchmark20_3",
                // benchmark 21
                "benchmark21_1",
                // benchmark 22
                "benchmark22_1",
                // benchmark 23
                "benchmark23_1",
                // benchmark 24
                "benchmark24_1",
                // benchmark 25
                "benchmark25_1",
                "benchmark25_2",
                "benchmark25_3",
                "benchmark25_4",
                // benchmark 26
                "benchmark26_1",
                "benchmark26_2",
                "benchmark26_3",
                // benchmark 27
                "benchmark27_1",
                "benchmark27_2",
                "benchmark27_3",
                "benchmark27_4",
                "benchmark27_5",
                // benchmark 28
                "benchmark28_1",
                "benchmark28_2",
                "benchmark28_3",
                "benchmark28_4",
                "benchmark28_5",
        });
    }

    private final String benchmark;

    public BenchmarkLocalTest(String benchmark) {
        this.benchmark = benchmark;
    }

    @Test
    public void test() {
        String majorBenchmarkName = getMajorBenchmarkName(benchmark);
        String benchmarkDir = String.format("benchmarks/%s/%s/", majorBenchmarkName, benchmark);
        File dir = new File(benchmarkDir);
        String srcSchemaPath = getFilePathByPrefix(dir, "SrcSchema");
        String tgtSchemaPath = getFilePathByPrefix(dir, "TgtSchema");
        String srcExamplePath = getFilePathByPrefix(dir, "SrcExample");
        String tgtExamplePath = getFilePathByPrefix(dir, "TgtExample");
        ISchema srcSchema = SchemaMapper.parseSchema(srcSchemaPath);
        ISchema tgtSchema = SchemaMapper.parseSchema(tgtSchemaPath);
        IInstance srcExample = SchemaMapper.parseExample(srcSchema, srcExamplePath);
        IInstance tgtExample = SchemaMapper.parseExample(tgtSchema, tgtExamplePath);
        SchemaMapping schemaMapping = SchemaMapper.mapSchema(srcSchema, tgtSchema, srcExample, tgtExample);
        Assert.assertNotNull(schemaMapping);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = tgtExample.toDocumentInstance().toDatalogOutputWithoutUniverse();
        Assert.assertEquals(expected, actual);
    }

    private String getMajorBenchmarkName(String benchmark) {
        int index = benchmark.indexOf('_');
        assert index > 0;
        return benchmark.substring(0, index);
    }

    private String getFilePathByPrefix(File directory, String prefix) {
        File[] files = directory.listFiles((dir, name) -> name.startsWith(prefix));
        assert files.length == 1;
        return files[0].getPath();
    }

}

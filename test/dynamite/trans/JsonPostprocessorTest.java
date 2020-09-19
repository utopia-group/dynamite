package dynamite.trans;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.util.FileUtils;

import static dynamite.datalog.DatalogTestUtils.buildDatalogProgram3;
import static dynamite.trans.JsonPostprocessor.emitJson;

public class JsonPostprocessorTest {

    @Test
    public void test1() {
        String output = "tmp/json_postprocessor_test/";
        Path outputPath = Paths.get(output);
        FileUtils.createDirectory(outputPath);
        FileUtils.writeStringToFile(output + "A.csv", "1\t2\t#k1#\n3\t4\t#k2#\n");
        FileUtils.writeStringToFile(output + "A?B.csv", "#k1#\t100\t101\n");
        DatalogProgram datalogProgram = buildDatalogProgram3();
        emitJson(datalogProgram, output);
        String jsonRes = FileUtils.readFromFile(output + "A.json");
        Assert.assertTrue(jsonRes.contains("\"a1\" : 1, \"a2\" : 2, \"B\" : [ { \"b1\" : 100, \"b2\" : 101 } ]")
                || jsonRes.contains("\"a1\":1,\"a2\":2,\"B\":[{\"b1\":100,\"b2\":101}]}"));
        Assert.assertTrue(jsonRes.contains("\"a1\" : 3, \"a2\" : 4, \"B\" : []")
                || jsonRes.contains("\"a1\":3,\"a2\":4,\"B\":[]"));
        FileUtils.deleteDirAndFiles(outputPath);
    }
}

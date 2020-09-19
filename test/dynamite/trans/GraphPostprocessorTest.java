package dynamite.trans;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.util.FileUtils;

import static dynamite.datalog.DatalogTestUtils.buildDatalogProgram4;
import static dynamite.graphdb.GraphDbTestUtils.buildGraphSchema2;
import static dynamite.trans.GraphPostprocessor.emitGraph;

public class GraphPostprocessorTest {

    @Test
    public void test1() {
        String output = "tmp/graph_postprocessor_test/";
        Path outputPath = Paths.get(output);
        FileUtils.createDirectory(outputPath);
        FileUtils.writeStringToFile(output + "LIVES_IN.csv",
                "92484856\tBrisbane, Queensland\n775647396\tWollongong, NSW, AUSTRALIA\n");
        FileUtils.writeStringToFile(output + "Location.csv",
                "Brisbane, Queensland\t-27.4697707\t153.0251235\nWollongong, NSW, AUSTRALIA\t-34.4278121\t150.8930607\n");
        FileUtils.writeStringToFile(output + "UserId.csv",
                "92484856\tPIPELINEPETE\tjocksjig\n775647396\tNarinder Parmar\tnparmar1957\n");
        FileUtils.writeStringToFile(output + "UserId?user_details.csv",
                "#92484856#\tPIPELINEPETE\tjocksjig\n#775647396#\tNarinder Parmar\tnparmar1957\n");
        DatalogProgram datalogProgram = buildDatalogProgram4();

        GraphSchema schema = buildGraphSchema2();
        emitGraph(schema, datalogProgram, output);
        String jsonRes = FileUtils.readFromFile(output + "wrapper.json");
        jsonRes = jsonRes.replace(" ", "");

        String expected1 = "\"type\":\"node\",\"labels\":[\"UserId\"],\"id\":775647396,\"properties\":{\"user_name\":\"Narinder Parmar\",\"user_screen_name\":\"nparmar1957\"}}";
        Assert.assertTrue(jsonRes.contains(expected1.replace(" ", "")));

        String expected2 = "\"type\":\"node\",\"labels\":[\"UserId\"],\"id\":92484856,\"properties\":{\"user_name\":\"PIPELINEPETE\",\"user_screen_name\":\"jocksjig\"}}";
        Assert.assertTrue(jsonRes.contains(expected2.replace(" ", "")));

        String expected3 = "\"type\":\"node\",\"labels\":[\"Location\"],\"id\":\"Brisbane, Queensland\",\"properties\":{\"lat\":-27.4697707,\"lon\":153.0251235}}";
        Assert.assertTrue(jsonRes.contains(expected3.replace(" ", "")));

        String expected4 = "\"type\":\"relationship\",\"label\":\"LIVES_IN\",\"start\":{\"id\":775647396},\"end\":{\"id\":\"Wollongong, NSW, AUSTRALIA\"},\"properties\":{}}";
        Assert.assertTrue(jsonRes.contains(expected4.replace(" ", "")));

        String expected5 = "\"type\":\"relationship\",\"label\":\"LIVES_IN\",\"start\":{\"id\":92484856},\"end\":{\"id\":\"Brisbane, Queensland\"},\"properties\":{}}";
        Assert.assertTrue(jsonRes.contains(expected5.replace(" ", "")));

        String expected6 = "\"type\":\"node\",\"labels\":[\"Location\"],\"id\":\"Wollongong, NSW, AUSTRALIA\",\"properties\":{\"lat\":-34.4278121,\"lon\":150.8930607}}";
        Assert.assertTrue(jsonRes.contains(expected6.replace(" ", "")));

        FileUtils.deleteDirAndFiles(outputPath);
    }
}

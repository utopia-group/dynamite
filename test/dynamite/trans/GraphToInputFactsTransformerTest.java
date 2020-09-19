package dynamite.trans;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import dynamite.graphdb.GraphDbTestUtils;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.util.FileUtils;

public class GraphToInputFactsTransformerTest {
    @Test
    public void testInstance1() {
        String jsonString = "["
                + "{"
                + "  id: 1,"
                + "  type: \"node\","
                + "  labels: [\"A\"],"
                + "  properties: {"
                + "     a1: \"P1\","
                + "     a2: 2"
                + "  }"
                + "}, {"
                + "  id: 2,"
                + "  type: \"node\","
                + "  labels: [\"B\"],"
                + "  properties: {"
                + "     b1: 1,"
                + "     b2: {"
                + "       b3: 3,"
                + "       b4: \"P4\""
                + "     }"
                + "  }"
                + "}, {"
                + "  id: \"3\","
                + "  type: \"relationship\","
                + "  label: \"C\","
                + "  properties: {"
                + "     c1: 1.1,"
                + "     c2: 2,"
                + "     c3: \"P3\","
                + "     c4: null"
                + "  },"
                + "  start: {"
                + "     id: \"1\","
                + "     labels: [\"A\"]"
                + "  },"
                + "  end: {"
                + "     id: \"2\","
                + "     labels: [\"B\"]"
                + "  }"
                + "}"
                + "]";
        GraphSchema actualSchema = GraphDbTestUtils.buildGraphInstanceSchema3();
        GraphToInputFactsTransformer.parseToFactsCsv(actualSchema, new StringReader(jsonString), "tmp/Souffle_input/");
        String aExpected = "1\tP1\t2";
        String bExpected = "2\t1\t#2#1#";
        String bb2Expected = "#2#1#\t3\tP4";
        String cExpected = "1\t2\t1.1\t2\tP3\tnull";
        String aActual = FileUtils.readFromFile("tmp/Souffle_input/A.facts");
        String bActual = FileUtils.readFromFile("tmp/Souffle_input/B.facts");
        String bb2Actual = FileUtils.readFromFile("tmp/Souffle_input/B?b2.facts");
        String cActual = FileUtils.readFromFile("tmp/Souffle_input/C.facts");
        Assert.assertEquals(aExpected, aActual);
        Assert.assertEquals(bExpected, bActual);
        Assert.assertEquals(bb2Expected, bb2Actual);
        Assert.assertEquals(cExpected, cActual);
    }

}

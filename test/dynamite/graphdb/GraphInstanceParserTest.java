package dynamite.graphdb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.GraphSchema;

public class GraphInstanceParserTest {

    @Test
    public void test1() {
        String jsonString = "["
                + "{"
                + "  id: \"1\","
                + "  type: \"node\","
                + "  labels: [\"A\"],"
                + "  properties: {"
                + "     prop1: \"P1\","
                + "     prop2: 2"
                + "  }"
                + "}"
                + "]";
        GraphSchema actualSchema = GraphDbTestUtils.buildGraphInstanceSchema1();
        GraphInstance actual = GraphInstanceParser.parse(jsonString, actualSchema);
        GraphInstance expected = GraphDbTestUtils.buildGraphInstance1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        String jsonString = "["
                + "{"
                + "  id: \"1\","
                + "  type: \"relationship\","
                + "  label: \"E\","
                + "  properties: {"
                + "     prop1: \"P1\","
                + "     prop2: 2"
                + "  },"
                + "  start: {"
                + "     id: \"1\","
                + "     labels: [\"A\", \"B\"]"
                + "  },"
                + "  end: {"
                + "     id: \"2\","
                + "     labels: [\"C\", \"D\"]"
                + "  }"
                + "}"
                + "]";
        GraphSchema actualSchema = GraphDbTestUtils.buildGraphInstanceSchema2();
        GraphInstance actual = GraphInstanceParser.parse(jsonString, actualSchema);
        GraphInstance expected = GraphDbTestUtils.buildGraphInstance2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
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
        GraphInstance actual = GraphInstanceParser.parse(jsonString, actualSchema);
        GraphInstance expected = GraphDbTestUtils.buildGraphInstance3();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
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
        GraphInstance actual = GraphInstanceParser.parse(jsonString, actualSchema);
        GraphInstance expected = GraphDbTestUtils.buildGraphInstance3();
        Assert.assertEquals(expected, actual);
    }

    /**
     * Actual instance has more labels than the schema
     * Ignore the extra label, because schema doesn't need it
     */
    @Test
    public void testLabels1() {
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
                + "  labels: [\"B\", \"X\"],"
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
        GraphInstance actual = GraphInstanceParser.parse(jsonString, actualSchema);
        GraphInstance expected = GraphDbTestUtils.buildGraphInstance3();
        Assert.assertEquals(expected, actual);
    }

    /**
     * Multiple labels, but the source and target agree
     */
    @Test
    public void testLabels2() {
        String jsonString = "["
                + "{"
                + "  id: \"1\","
                + "  type: \"node\","
                + "  labels: [\"A\", \"B\"],"
                + "  properties: {"
                + "     prop1: \"P1\","
                + "     prop2: 2"
                + "  }"
                + "}"
                + "]";
        GraphSchema actualSchema = GraphDbTestUtils.buildGraphInstanceSchema4();
        GraphInstance actual = GraphInstanceParser.parse(jsonString, actualSchema);
        GraphInstance expected = GraphDbTestUtils.buildGraphInstance4();
        Assert.assertEquals(expected, actual);
    }

    /**
     * Instance has node with only one label, but it doesn't exist in the schema
     */
    @Test(expected = RuntimeException.class)
    public void testLabels3() {
        String jsonString = "["
                + "{"
                + "  id: \"1\","
                + "  type: \"node\","
                + "  labels: [\"A\"],"
                + "  properties: {"
                + "     prop1: \"P1\""
                + "  }"
                + "},"
                + "{"
                + "  id: \"2\","
                + "  type: \"node\","
                + "  labels: [\"B\"],"
                + "  properties: {"
                + "     prop2: 2"
                + "  }"
                + "}"
                + "]";
        GraphSchema actualSchema = GraphDbTestUtils.buildGraphInstanceSchema5();
        GraphInstanceParser.parse(jsonString, actualSchema);
        Assert.fail("Expected to throw runtime exception because label not found in schema");
    }

}

package dynamite.graphdb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.exp.IntegrityConstraint;
import dynamite.exp.IntegrityConstraintParser;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.GraphSchema;

public class RandomGraphInstGenVisitorTest {

    public static final long RANDOM_SEED = 1;

    private static IntegrityConstraint buildIntegrityConstraint1() {
        return IntegrityConstraintParser.parse(""
                + "C?_start -> A?_id\n"
                + "C?_end -> B?_id\n");
    }

    @Test
    public void test1() {
        GraphSchema schema = GraphDbTestUtils.buildGraphSchema1();
        int size = 1;
        IntegrityConstraint constraint = buildIntegrityConstraint1();
        RandomGraphInstGenVisitor visitor = new RandomGraphInstGenVisitor(size, constraint, RANDOM_SEED);
        GraphInstance actual = schema.accept(visitor);
        String expectedText = ""
                + "["
                + "    {"
                + "        \"id\": \"V1\","
                + "        \"type\": \"node\","
                + "        \"labels\": [ \"A\" ],"
                + "        \"properties\": {"
                + "            \"a1\": 985,"
                + "            \"a2\": \"s588\""
                + "        }"
                + "    },"
                + "    {"
                + "        \"id\": \"V2\","
                + "        \"type\": \"node\","
                + "        \"labels\": [ \"B\" ],"
                + "        \"properties\": {"
                + "            \"b1\": 847,"
                + "            \"b2\": 313.0,"
                + "            \"b3\": \"s254\""
                + "        }"
                + "    },"
                + "    {"
                + "        \"id\": \"E1\","
                + "        \"type\": \"relationship\","
                + "        \"label\": \"C\","
                + "        \"properties\": {"
                + "            \"c1\": 904,"
                + "            \"c2\": \"s434\""
                + "        },"
                + "        \"start\": { \"id\": \"V1\", \"labels\": [ \"A\" ] },"
                + "        \"end\": { \"id\": \"V2\", \"labels\": [ \"B\" ] }"
                + "    }"
                + "]";
        GraphInstance expected = GraphInstanceParser.parse(expectedText, schema);
        Assert.assertEquals(expected, actual);
    }

}

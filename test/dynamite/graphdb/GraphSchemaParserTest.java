package dynamite.graphdb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.graphdb.ast.GraphSchema;

public class GraphSchemaParserTest {

    @Test
    public void test1() {
        String schemaString = ""
                + "[\n"
                + "  {\n"
                + "    \"type\": \"node\",\n"
                + "    \"labels\": [\"A\"],\n"
                + "    \"properties\": {\n"
                + "      \"a1\": \"Int\",\n"
                + "      \"a2\": \"String\"\n"
                + "    }\n"
                + "  }, {\n"
                + "    \"type\": \"node\",\n"
                + "    \"labels\": [\"B\"],\n"
                + "    \"properties\": {\n"
                + "      \"b1\": \"Int\",\n"
                + "      \"b2\": \"Float\",\n"
                + "      \"b3\": \"String\"\n"
                + "    }\n"
                + "  }, {\n"
                + "    \"type\": \"relationship\",\n"
                + "    \"label\": \"C\",\n"
                + "    \"properties\": {\n"
                + "      \"c1\": \"Int\",\n"
                + "      \"c2\": \"String\"\n"
                + "    }\n"
                + "  }\n"
                + "]\n";
        GraphSchema actual = GraphSchemaParser.parse(schemaString);
        GraphSchema expected = GraphDbTestUtils.buildGraphSchema1();
        Assert.assertEquals(expected, actual);
    }

}

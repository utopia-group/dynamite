package dynamite.reldb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.reldb.ast.RelationalSchema;

public class RelSchemaParserTest {

    @Test
    public void test1() {
        String schemaString = "R(a: Int, b: Int, c: String, d: String)";
        RelationalSchema actual = RelSchemaParser.parse(schemaString);
        RelationalSchema expected = RelDbTestUtils.buildRelationalSchema1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        String schemaString = ""
                + "A(a1: Int, a2: Int)\n"
                + "B(b1: Int, b2: Int)\n";
        RelationalSchema actual = RelSchemaParser.parse(schemaString);
        RelationalSchema expected = RelDbTestUtils.buildRelationalSchema2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat1() {
        String schemaString = "A(a: Float)";
        RelationalSchema actual = RelSchemaParser.parse(schemaString);
        RelationalSchema expected = RelDbTestUtils.buildRelationalSchemaFloat1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
        String schemaString = "A(a: Int, b: String)";
        RelationalSchema actual = RelSchemaParser.parse(schemaString);
        RelationalSchema expected = RelDbTestUtils.buildRelationalSchemaNull1();
        Assert.assertEquals(expected, actual);
    }

}

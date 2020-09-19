package dynamite.reldb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.reldb.ast.RelationalInstance;

public class RelInstanceParserTest {

    @Test
    public void test1() {
        String instString = ""
                + "R(a: Int, b: Int, c: String, d: String)\n"
                + "1, 2, \"s1\", \"s2\"\n"
                + "3, 4, \"s3\", \"s4\"\n";
        RelationalInstance actual = RelInstanceParser.parse(instString);
        RelationalInstance expected = RelDbTestUtils.buildRelationalInstance1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        String instString = ""
                + "A(a1: Int, a2: Int)\n"
                + "1, 2\n"
                + "3, 4\n"
                + "\n"
                + "B(b1: Int, b2: Int)\n"
                + "5, 6\n"
                + "7, 8\n";
        RelationalInstance actual = RelInstanceParser.parse(instString);
        RelationalInstance expected = RelDbTestUtils.buildRelationalInstance2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat1() {
        String instString = ""
                + "A(a: Float)\n"
                + "100.5\n";
        RelationalInstance actual = RelInstanceParser.parse(instString);
        RelationalInstance expected = RelDbTestUtils.buildRelationalInstanceFloat1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
        String instString = ""
                + "A(a: Int, b: String)\n"
                + "1, null\n";
        RelationalInstance actual = RelInstanceParser.parse(instString);
        RelationalInstance expected = RelDbTestUtils.buildRelationalInstanceNull1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull2() {
        String instString = ""
                + "R(a: Int, b: Int, c: String, d: String)\n"
                + "1, 2, \"s1\", \"s2\"\n"
                + "3, 4,, \"s4\"\n";
        RelationalInstance actual = RelInstanceParser.parse(instString);
        RelationalInstance expected = RelDbTestUtils.buildRelationalInstanceNull2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testComma1() {
        String instString = ""
                + "R(a: Int, b: Int, c: String, d: String)\n"
                + "1, 2, \"s1, and commas,,\", \"s2\"\n"
                + "3, 4, \"s3\", \"s4\"\n";
        RelationalInstance actual = RelInstanceParser.parse(instString);
        RelationalInstance expected = RelDbTestUtils.buildRelationalInstanceComma1();
        Assert.assertEquals(expected, actual);
    }
}

package dynamite.docdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;

public class DocJsonInstanceParserTest {

    @Test
    public void test1() {
        String jsonString = "{"
                + "Computers:[{"
                + "  cid: 100,"
                + "  name: \"C1\","
                + "  manufacturer: \"M1\","
                + "  catalog: \"C2\","
                + "  parts: ["
                + "    {value: 101},"
                + "    {value: 102}"
                + "  ]"
                + "}]"
                + "}";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        String jsonString = "{"
                + "Computers:[{"
                + "  cid: 100,"
                + "  name: \"C1\","
                + "  parts: ["
                + "    {value: 101},"
                + "    {value: 102}"
                + "  ],"
                + "  catalog: \"C2\","
                + "  manufacturer: \"M1\""
                + "}]"
                + "}";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
        String jsonString = "{"
                + "A: [{"
                + "  a1: 100,"
                + "  B: [{"
                + "    b1: 200,"
                + "    C: [{"
                + "      c1: 300,"
                + "      c2: 400"
                + "    }]"
                + "  }]"
                + "}]"
                + "}";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance3();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test4() {
        String jsonString = "{"
                + "A: [{"
                + "  a1: 1000,"
                + "  a2: 2000"
                + "}]"
                + ","
                + "B: [{"
                + "  b1: 3000,"
                + "  b2: 4000"
                + "}]"
                + "}";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance4();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testIllFormed1() {
        String jsonString = "{"
                + "Computers: {"
                + "  cid: 100,"
                + "  name: \"C1\","
                + "  manufacturer: \"M1\","
                + "  catalog: \"C2\","
                + "  parts: ["
                + "    {value: 101},"
                + "    {value: 102}"
                + "  ]"
                + "}"
                + "}";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testIllFormed2() {
        String jsonString = "{"
                + "Computers: {"
                + "  cid: 100,"
                + "  name: \"C1\","
                + "  parts: ["
                + "    {value: 101},"
                + "    {value: 102}"
                + "  ],"
                + "  catalog: \"C2\","
                + "  manufacturer: \"M1\""
                + "}"
                + "}";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testIllFormed3() {
        String jsonString = "{"
                + "A: {"
                + "  a1: 100,"
                + "  B: {"
                + "    b1: 200,"
                + "    C: {"
                + "      c1: 300,"
                + "      c2: 400"
                + "    }"
                + "  }"
                + "}"
                + "}";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance3();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testIllFormed4() {
        String jsonString = "{"
                + "A: {"
                + "  a1: 1000,"
                + "  a2: 2000"
                + "}"
                + ","
                + "B: {"
                + "  b1: 3000,"
                + "  b2: 4000"
                + "}"
                + "}";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance4();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat1() {
        String schemaString = "{"
                + "A:[{"
                + "  a: Float"
                + "}]"
                + "}";
        String jsonString = "{"
                + "A:[{"
                + "  a: 100.5"
                + "}]"
                + "}";
        DocumentSchema schema = DocSchemaParser.parse(schemaString);
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstanceFloat1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
        String schemaString = "{"
                + "A:[{"
                + "  a: Int,"
                + "  b: String"
                + "}]"
                + "}";
        String jsonString = "{"
                + "A:[{"
                + "  a: 1,"
                + "  b: null"
                + "}]"
                + "}";
        DocumentSchema schema = DocSchemaParser.parse(schemaString);
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstanceNull1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSorted1() {
        String jsonString = "{"
                + "Computers:[{"
                + "  cid: 100,"
                + "  name: \"C1\","
                + "  manufacturer: \"M1\","
                + "  catalog: \"C2\","
                + "  parts: ["
                + "    {value: 101},"
                + "    {value: 102}"
                + "  ]"
                + "}]"
                + "}";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DocumentInstance actual = DocJsonInstanceParser.parse(jsonString, schema);
        DocumentInstance expectedInst = DocDbTestUtils.buildDocumentInstance1();
        List<DIAttrValue> expected = new ArrayList<>(expectedInst.collections.get(0).documents.get(0).attributes);
        Collections.sort(expected, (x, y) -> x.getAttributeName().compareTo(y.getAttributeName()));
        Assert.assertEquals(expected, actual.collections.get(0).documents.get(0).attributes);
    }
}

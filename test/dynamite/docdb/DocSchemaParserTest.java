package dynamite.docdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DocumentSchema;

public class DocSchemaParserTest {

    @Test
    public void test1() {
        String jsonString = "{"
                + "Computers:[{"
                + "  cid: Int,"
                + "  name: String,"
                + "  manufacturer: String,"
                + "  catalog: String,"
                + "  parts: [{"
                + "    value: Int"
                + "  }]"
                + "}]"
                + "}";
        DocumentSchema actual = DocSchemaParser.parse(jsonString);
        DocumentSchema expected = DocDbTestUtils.buildDocumentSchema1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        String jsonString = "{"
                + "Computers:[{"
                + "  cid: Int,"
                + "  name: String,"
                + "  parts: [{"
                + "    value: Int"
                + "  }],"
                + "  catalog: String,"
                + "  manufacturer: String"
                + "}]"
                + "}";
        DocumentSchema actual = DocSchemaParser.parse(jsonString);
        DocumentSchema expected = DocDbTestUtils.buildDocumentSchema2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
        String jsonString = "{"
                + "A: [{"
                + "  a1: Int,"
                + "  B: [{"
                + "    b1: Int,"
                + "    C: [{"
                + "      c1: Int,"
                + "      c2: Int"
                + "    }]"
                + "  }]"
                + "}]"
                + "}";
        DocumentSchema actual = DocSchemaParser.parse(jsonString);
        DocumentSchema expected = DocDbTestUtils.buildDocumentSchema3();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test4() {
        String jsonString = "{"
                + "A: [{"
                + "  a1: Int,"
                + "  a2: Int"
                + "}]"
                + ","
                + "B: [{"
                + "  b1: Int,"
                + "  b2: Int"
                + "}]"
                + "}";
        DocumentSchema actual = DocSchemaParser.parse(jsonString);
        DocumentSchema expected = DocDbTestUtils.buildDocumentSchema4();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat1() {
        String jsonString = "{"
                + "A: [{"
                + "  a: Float"
                + "}]"
                + "}";
        DocumentSchema actual = DocSchemaParser.parse(jsonString);
        DocumentSchema expected = DocDbTestUtils.buildDocumentSchemaFloat1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
        String jsonString = "{"
                + "A: [{"
                + "  a: Int,"
                + "  b: String"
                + "}]"
                + "}";
        DocumentSchema actual = DocSchemaParser.parse(jsonString);
        DocumentSchema expected = DocDbTestUtils.buildDocumentSchemaNull1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSorted1() {
        String jsonString = "{"
                + "Computers:[{"
                + "  cid: Int,"
                + "  name: String,"
                + "  manufacturer: String,"
                + "  catalog: String,"
                + "  parts: [{"
                + "    value: Int"
                + "  }]"
                + "}]"
                + "}";
        DocumentSchema actual = DocSchemaParser.parse(jsonString);
        DocumentSchema expectedSchema = DocDbTestUtils.buildDocumentSchema1();
        List<DSAttribute> expected = new ArrayList<>(expectedSchema.collections.get(0).document.attributes);
        Collections.sort(expected, (x, y) -> x.getName().compareTo(y.getName()));
        Assert.assertEquals(expected, actual.collections.get(0).document.attributes);
    }

}

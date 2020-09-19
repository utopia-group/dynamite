package dynamite.docdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;

public class DocXmlInstanceParserTest {

    @Test
    public void test1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<root>" +
                "<Computers>\n" +
                "    <cid>100</cid>\n" +
                "    <name>C1</name>\n" +
                "    <manufacturer>M1</manufacturer>\n" +
                "    <catalog>C2</catalog>\n" +
                "    <parts>\n" +
                "        <value>101</value>\n" +
                "    </parts>\n" +
                "    <parts>\n" +
                "        <value>102</value>\n" +
                "    </parts>\n" +
                "</Computers></root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<root>" +
                "<Computers>\n" +
                "    <cid>100</cid>\n" +
                "    <name>C1</name>\n" +
                "    <parts>\n" +
                "        <value>101</value>\n" +
                "    </parts>\n" +
                "    <parts>\n" +
                "        <value>102</value>\n" +
                "    </parts>\n" +
                "    <catalog>C2</catalog>\n" +
                "    <manufacturer>M1</manufacturer>\n" +
                "</Computers>" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<A>\n" +
                "    <a1>100</a1>\n" +
                "    <B>\n" +
                "        <b1>200</b1>\n" +
                "        <C>\n" +
                "            <c1>300</c1>\n" +
                "            <c2>400</c2>\n" +
                "        </C>\n" +
                "    </B>\n" +
                "</A>" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance3();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test5() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<A a1=\"100\">\n" +
                "    <B>\n" +
                "        <b1>200</b1>\n" +
                "        <C c1=\"300\" c2=\"400\" />\n" +
                "    </B>\n" +
                "</A>\n" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance3();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test6() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<A>\n" +
                "    <a1>100</a1>\n" +
                "    <B b1=\"200\">\n" +
                "        300" +
                "    </B>\n" +
                "</A>\n" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema6();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance6();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testIllFormed1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<Computers>\n" +
                "    <cid>100</cid>\n" +
                "    <name>C1</name>\n" +
                "    <parts>\n" +
                "        <value>101</value>\n" +
                "    </parts>\n" +
                "    <parts>\n" +
                "        <value>102</value>\n" +
                "    </parts>\n" +
                "    <catalog>C2</catalog>\n" +
                "    <manufacturer>M1</manufacturer>\n" +
                "</Computers>\n" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testIllFormed2() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<Computers>\n" +
                "    <cid>100</cid>\n" +
                "    <name>C1</name>\n" +
                "    <parts>\n" +
                "        <value>101</value>\n" +
                "    </parts>\n" +
                "    <parts>\n" +
                "        <value>102</value>\n" +
                "    </parts>\n" +
                "    <catalog>C2</catalog>\n" +
                "    <manufacturer>M1</manufacturer>\n" +
                "</Computers>\n" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testIllFormed3() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<A>\n" +
                "    <a1>100</a1>\n" +
                "    <B>\n" +
                "        <b1>200</b1>\n" +
                "        <C>\n" +
                "            <c1>300</c1>\n" +
                "            <c2>400</c2>\n" +
                "        </C>\n" +
                "    </B>\n" +
                "</A>\n" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance3();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<A>\n" +
                "    <a>100.5</a>\n" +
                "</A>\n" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchemaFloat1();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstanceFloat1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat2() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<A>\n" +
                "    <a>1</a>\n" +
                "</A>\n" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchemaFloat1();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstanceFloat2();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<A>\n" +
                "\t<a>1</a>\n" +
                "\t<b></b>\n" +
                "</A>\n" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchemaNull1();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstanceNull1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSorted1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<root>" +
                "<Computers>\n" +
                "    <cid>100</cid>\n" +
                "    <name>C1</name>\n" +
                "    <manufacturer>M1</manufacturer>\n" +
                "    <catalog>C2</catalog>\n" +
                "    <parts>\n" +
                "        <value>101</value>\n" +
                "    </parts>\n" +
                "    <parts>\n" +
                "        <value>102</value>\n" +
                "    </parts>\n" +
                "</Computers></root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expectedInst = DocDbTestUtils.buildDocumentInstance1();
        List<DIAttrValue> expected = new ArrayList<>(expectedInst.collections.get(0).documents.get(0).attributes);
        Collections.sort(expected, (x, y) -> x.getAttributeName().compareTo(y.getAttributeName()));
        Assert.assertEquals(expected, actual.collections.get(0).documents.get(0).attributes);
    }

    @Test
    public void testUnexpected1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root>\n" +
                "<A a1=\"100\">\n" +
                "    <B unexpected=\"500\">\n" +
                "        <b1 unexpected=\"501\">200</b1>\n" +
                "        <C>\n" +
                "            <c1>300</c1>\n" +
                "            <c2>400</c2>\n" +
                "            <unexpected><a>502</a></unexpected>\n" +
                "        </C>\n" +
                "        <unexpected>503</unexpected>\n" +
                "    </B>\n" +
                "</A>\n" +
                "<unexpected><A>505</A></unexpected>" +
                "</root>";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DocXmlInstanceParser parser = new DocXmlInstanceParser(schema);
        DocumentInstance actual = parser.parse(xmlString);
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance3();
        Assert.assertEquals(expected, actual);
    }
}

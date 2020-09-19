package dynamite.trans;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.DocDbTestUtils;
import dynamite.docdb.ast.DocumentSchema;

import static dynamite.util.FileUtils.readFromFile;

public class XmlToInputFactsTransformerTest {
    @Test
    public void testInstance1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + "<root>" +
                "<A>\n" +
                "\t<a1>100</a1>\n" +
                "\t<B>\n" +
                "\t\t<b1>200</b1>\n" +
                "\t</B>\n" +
                "</A>" + "</root>";
        String aExpected = "#100#\t100";
        String abExpected = "#100#\t200";
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema7();
        XmlToInputFactsTransformer.parseToFactsCsv(schema, new StringReader(xmlString), "tmp/Souffle_input/");
        String aActual = readFromFile("tmp/Souffle_input/A.facts");
        String abActual = readFromFile("tmp/Souffle_input/A?B.facts");
        Assert.assertEquals(aExpected, aActual);
        Assert.assertEquals(abExpected, abActual);
    }
}

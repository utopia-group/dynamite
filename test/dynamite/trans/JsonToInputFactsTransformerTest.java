package dynamite.trans;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.DocSchemaParser;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.util.FileUtils;

public class JsonToInputFactsTransformerTest {

    @Test
    public void testInstance1() {
        String schemaString = "{"
                + "A: [{"
                + "  a1: Int,"
                + "  B: [{"
                + "    b1: Int"
                + "  }]"
                + "}]"
                + "}";
        String jsonString = "{"
                + "A: [{"
                + "  a1: 100,"
                + "  B: [{"
                + "    b1: 200"
                + "  }]"
                + "}]"
                + "}";
        String aExpected = "#100#\t100";
        String abExpected = "#100#\t200";
        DocumentSchema schema = DocSchemaParser.parse(schemaString);
        JsonToInputFactsTransformer.parseToFactsCsv(schema, new StringReader(jsonString), "tmp/Souffle_input/");
        String aActual = FileUtils.readFromFile("tmp/Souffle_input/A.facts");
        String abActual = FileUtils.readFromFile("tmp/Souffle_input/A?B.facts");
        Assert.assertEquals(aExpected, aActual);
        Assert.assertEquals(abExpected, abActual);
    }

}

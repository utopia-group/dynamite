package dynamite.graphdb.ast;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DocumentSchema;
import dynamite.graphdb.GraphDbTestUtils;

public class GraphSchemaTest {

    @Test
    public void test1() {
        GraphSchema schema = GraphDbTestUtils.buildGraphSchema1();
        DocumentSchema expected = GraphDbTestUtils.buildDocumentSchema1();
        Assert.assertEquals(expected, schema.toDocumentSchema());
    }

}

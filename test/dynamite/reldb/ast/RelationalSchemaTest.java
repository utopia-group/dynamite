package dynamite.reldb.ast;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DocumentSchema;
import dynamite.reldb.RelDbTestUtils;

public class RelationalSchemaTest {

    @Test
    public void test1() {
        RelationalSchema schema = RelDbTestUtils.buildRelationalSchema1();
        DocumentSchema expected = RelDbTestUtils.buildDocumentSchema1();
        Assert.assertEquals(expected, schema.toDocumentSchema());
    }

    @Test
    public void test2() {
        RelationalSchema schema = RelDbTestUtils.buildRelationalSchema2();
        DocumentSchema expected = RelDbTestUtils.buildDocumentSchema2();
        Assert.assertEquals(expected, schema.toDocumentSchema());
    }

}

package dynamite.docdb.ast;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.DocDbTestUtils;

public class DocSchemaEqualityTest {

    @Test
    public void testOrdered1() {
        DocumentSchema schema1 = DocDbTestUtils.buildDocumentSchema1();
        DocumentSchema schema2 = DocDbTestUtils.buildDocumentSchema1();
        Assert.assertEquals(schema1, schema2);
    }

    @Test
    public void testOrdered2() {
        DocumentSchema schema1 = DocDbTestUtils.buildDocumentSchema2();
        DocumentSchema schema2 = DocDbTestUtils.buildDocumentSchema2();
        Assert.assertEquals(schema1, schema2);
    }

    @Test
    public void testOrdered3() {
        DocumentSchema schema1 = DocDbTestUtils.buildDocumentSchema3();
        DocumentSchema schema2 = DocDbTestUtils.buildDocumentSchema3();
        Assert.assertEquals(schema1, schema2);
    }

    @Test
    public void testOrdered4() {
        DocumentSchema schema1 = DocDbTestUtils.buildDocumentSchema4();
        DocumentSchema schema2 = DocDbTestUtils.buildDocumentSchema4();
        Assert.assertEquals(schema1, schema2);
    }

    @Test
    public void testUnordered() {
        DocumentSchema schema1 = DocDbTestUtils.buildDocumentSchema1();
        DocumentSchema schema2 = DocDbTestUtils.buildDocumentSchema2();
        Assert.assertEquals(schema1, schema2);
    }

}

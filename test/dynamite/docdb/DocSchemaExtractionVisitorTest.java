package dynamite.docdb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;

public class DocSchemaExtractionVisitorTest {

    @Test
    public void test1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance1();
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        Assert.assertEquals(schema, instance.accept(new SchemaExtractionVisitor()));
    }

    @Test
    public void test2() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance2();
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        Assert.assertEquals(schema, instance.accept(new SchemaExtractionVisitor()));
    }

    @Test
    public void test3() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance3();
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        Assert.assertEquals(schema, instance.accept(new SchemaExtractionVisitor()));
    }

    @Test
    public void test4() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance4();
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        Assert.assertEquals(schema, instance.accept(new SchemaExtractionVisitor()));
    }

    @Test
    public void testFloat1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstanceFloat1();
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchemaFloat1();
        Assert.assertEquals(schema, instance.accept(new SchemaExtractionVisitor()));
    }

}

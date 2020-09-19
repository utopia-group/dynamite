package dynamite.docdb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.DatalogOutput;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;

public class OutputToDocInstVisitorTest {

    @Test
    public void test1() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance1();
        DatalogOutput output = expected.toDatalogOutput().getOutputWithoutUniverse();
        DocumentInstance actual = schema.accept(new OutputToDocInstVisitor(output));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance2();
        DatalogOutput output = expected.toDatalogOutput().getOutputWithoutUniverse();
        DocumentInstance actual = schema.accept(new OutputToDocInstVisitor(output));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance3();
        DatalogOutput output = expected.toDatalogOutput().getOutputWithoutUniverse();
        DocumentInstance actual = schema.accept(new OutputToDocInstVisitor(output));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test4() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstance4();
        DatalogOutput output = expected.toDatalogOutput().getOutputWithoutUniverse();
        DocumentInstance actual = schema.accept(new OutputToDocInstVisitor(output));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat1() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchemaFloat1();
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstanceFloat1();
        DatalogOutput output = expected.toDatalogOutput().getOutputWithoutUniverse();
        DocumentInstance actual = schema.accept(new OutputToDocInstVisitor(output));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchemaNull1();
        DocumentInstance expected = DocDbTestUtils.buildDocumentInstanceNull1();
        DatalogOutput output = expected.toDatalogOutput().getOutputWithoutUniverse();
        DocumentInstance actual = schema.accept(new OutputToDocInstVisitor(output));
        Assert.assertEquals(expected, actual);
    }

}

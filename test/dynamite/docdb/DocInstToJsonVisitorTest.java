package dynamite.docdb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;

public class DocInstToJsonVisitorTest {

    @Test
    public void test1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance1();
        String instString = instance.accept(new DocInstToJsonVisitor());
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DocumentInstance actual = DocJsonInstanceParser.parse(instString, schema);
        Assert.assertEquals(actual, instance);
    }

    @Test
    public void test2() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance2();
        String instString = instance.accept(new DocInstToJsonVisitor());
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        DocumentInstance actual = DocJsonInstanceParser.parse(instString, schema);
        Assert.assertEquals(actual, instance);
    }

    @Test
    public void test3() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance3();
        String instString = instance.accept(new DocInstToJsonVisitor());
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DocumentInstance actual = DocJsonInstanceParser.parse(instString, schema);
        Assert.assertEquals(actual, instance);
    }

    @Test
    public void test4() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance4();
        String instString = instance.accept(new DocInstToJsonVisitor());
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        DocumentInstance actual = DocJsonInstanceParser.parse(instString, schema);
        Assert.assertEquals(actual, instance);
    }

}

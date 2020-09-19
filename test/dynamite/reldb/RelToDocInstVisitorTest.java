package dynamite.reldb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DocumentInstance;
import dynamite.reldb.ast.RelationalInstance;

public class RelToDocInstVisitorTest {

    @Test
    public void test1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstance1();
        DocumentInstance expected = RelDbTestUtils.buildDocumentInstance1();
        DocumentInstance actual = instance.accept(new RelToDocInstVisitor());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstance2();
        DocumentInstance expected = RelDbTestUtils.buildDocumentInstance2();
        DocumentInstance actual = instance.accept(new RelToDocInstVisitor());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstanceFloat1();
        DocumentInstance expected = RelDbTestUtils.buildDocumentInstanceFloat1();
        DocumentInstance actual = instance.accept(new RelToDocInstVisitor());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstanceNull1();
        DocumentInstance expected = RelDbTestUtils.buildDocumentInstanceNull1();
        DocumentInstance actual = instance.accept(new RelToDocInstVisitor());
        Assert.assertEquals(expected, actual);
    }

}

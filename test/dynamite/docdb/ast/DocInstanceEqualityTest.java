package dynamite.docdb.ast;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.DocDbTestUtils;

public class DocInstanceEqualityTest {

    @Test
    public void testOrdered1() {
        DocumentInstance instance1 = DocDbTestUtils.buildDocumentInstance1();
        DocumentInstance instance2 = DocDbTestUtils.buildDocumentInstance1();
        Assert.assertEquals(instance1, instance2);
    }

    @Test
    public void testOrdered2() {
        DocumentInstance instance1 = DocDbTestUtils.buildDocumentInstance2();
        DocumentInstance instance2 = DocDbTestUtils.buildDocumentInstance2();
        Assert.assertEquals(instance1, instance2);
    }

    @Test
    public void testOrdered3() {
        DocumentInstance instance1 = DocDbTestUtils.buildDocumentInstance3();
        DocumentInstance instance2 = DocDbTestUtils.buildDocumentInstance3();
        Assert.assertEquals(instance1, instance2);
    }

    @Test
    public void testOrdered4() {
        DocumentInstance instance1 = DocDbTestUtils.buildDocumentInstance4();
        DocumentInstance instance2 = DocDbTestUtils.buildDocumentInstance4();
        Assert.assertEquals(instance1, instance2);
    }

    @Test
    public void testUnordered() {
        DocumentInstance instance1 = DocDbTestUtils.buildDocumentInstance1();
        DocumentInstance instance2 = DocDbTestUtils.buildDocumentInstance2();
        Assert.assertEquals(instance1, instance2);
    }

}

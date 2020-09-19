package dynamite.docdb;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DocumentSchema;

public class ParentMapGenVisitorTest {

    @Test
    public void testSchema1() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        Map<String, String> parentMap = schema.accept(new ParentMapGenVisitor());
        Assert.assertEquals(1, parentMap.size());
        for (String key : parentMap.keySet()) {
            if (key.equals("parts")) {
                Assert.assertEquals("Computers", parentMap.get(key));
            }
        }
    }

    @Test
    public void testSchema2() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        Map<String, String> parentMap = schema.accept(new ParentMapGenVisitor());
        Assert.assertEquals(1, parentMap.size());
        for (String key : parentMap.keySet()) {
            if (key.equals("parts")) {
                Assert.assertEquals("Computers", parentMap.get(key));
            }
        }
    }

    @Test
    public void testSchema3() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        Map<String, String> parentMap = schema.accept(new ParentMapGenVisitor());
        Assert.assertEquals(2, parentMap.size());
        for (String key : parentMap.keySet()) {
            if (key.equals("B")) {
                Assert.assertEquals("A", parentMap.get(key));
            } else if (key.equals("C")) {
                Assert.assertEquals("B", parentMap.get(key));
            }
        }
    }

    @Test
    public void testSchema4() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        Map<String, String> parentMap = schema.accept(new ParentMapGenVisitor());
        Assert.assertEquals(0, parentMap.size());
    }

}

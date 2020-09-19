package dynamite.docdb;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DocumentSchema;

public class NameMapGenVisitorTest {

    @Test
    public void testSchema1() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        Map<String, DSCollection> nameMap = schema.accept(new NameMapGenVisitor());
        Assert.assertEquals(2, nameMap.size());
        for (String key : nameMap.keySet()) {
            Assert.assertEquals(key, nameMap.get(key).getCanonicalName());
        }
    }

    @Test
    public void testSchema2() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        Map<String, DSCollection> nameMap = schema.accept(new NameMapGenVisitor());
        Assert.assertEquals(2, nameMap.size());
        for (String key : nameMap.keySet()) {
            Assert.assertEquals(key, nameMap.get(key).getCanonicalName());
        }
    }

    @Test
    public void testSchema3() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        Map<String, DSCollection> nameMap = schema.accept(new NameMapGenVisitor());
        Assert.assertEquals(3, nameMap.size());
        for (String key : nameMap.keySet()) {
            Assert.assertEquals(key, nameMap.get(key).getCanonicalName());
        }
    }

    @Test
    public void testSchema4() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        Map<String, DSCollection> nameMap = schema.accept(new NameMapGenVisitor());
        Assert.assertEquals(2, nameMap.size());
        for (String key : nameMap.keySet()) {
            Assert.assertEquals(key, nameMap.get(key).getCanonicalName());
        }
    }

}

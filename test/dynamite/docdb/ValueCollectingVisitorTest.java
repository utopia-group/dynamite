package dynamite.docdb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DocumentInstance;
import dynamite.util.SetMultiMap;

public class ValueCollectingVisitorTest {

    @Test
    public void test1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance1();
        SetMultiMap<String, Object> actual = instance.accept(new ValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("Computers?cid", 100);
        expected.put("Computers?name", "C1");
        expected.put("Computers?manufacturer", "M1");
        expected.put("Computers?catalog", "C2");
        expected.put("Computers?parts?value", 101);
        expected.put("Computers?parts?value", 102);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance2();
        SetMultiMap<String, Object> actual = instance.accept(new ValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("Computers?cid", 100);
        expected.put("Computers?name", "C1");
        expected.put("Computers?manufacturer", "M1");
        expected.put("Computers?catalog", "C2");
        expected.put("Computers?parts?value", 101);
        expected.put("Computers?parts?value", 102);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance3();
        SetMultiMap<String, Object> actual = instance.accept(new ValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("A?a1", 100);
        expected.put("A?B?b1", 200);
        expected.put("A?B?C?c1", 300);
        expected.put("A?B?C?c2", 400);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test4() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance4();
        SetMultiMap<String, Object> actual = instance.accept(new ValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("A?a1", 1000);
        expected.put("A?a2", 2000);
        expected.put("B?b1", 3000);
        expected.put("B?b2", 4000);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test5() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance5();
        SetMultiMap<String, Object> actual = instance.accept(new ValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("A?a1", 100);
        expected.put("A?B?b1", 200);
        expected.put("A?B?C?c1", 300);
        expected.put("A?B?C?c2", 400);
        expected.put("A?B?C?c2", 500);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstanceFloat1();
        SetMultiMap<String, Object> actual = instance.accept(new ValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("A?a", "100.5");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstanceNull1();
        SetMultiMap<String, Object> actual = instance.accept(new ValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("A?a", 1);
        expected.put("A?b", "null");
        Assert.assertEquals(expected, actual);
    }

}

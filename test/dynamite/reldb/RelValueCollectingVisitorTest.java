package dynamite.reldb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.reldb.ast.RelationalInstance;
import dynamite.util.SetMultiMap;

public class RelValueCollectingVisitorTest {

    @Test
    public void test1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstance1();
        SetMultiMap<String, Object> actual = instance.accept(new RelValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("R?a", 1);
        expected.put("R?a", 3);
        expected.put("R?b", 2);
        expected.put("R?b", 4);
        expected.put("R?c", "s1");
        expected.put("R?c", "s3");
        expected.put("R?d", "s2");
        expected.put("R?d", "s4");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstance2();
        SetMultiMap<String, Object> actual = instance.accept(new RelValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("A?a1", 1);
        expected.put("A?a1", 3);
        expected.put("A?a2", 2);
        expected.put("A?a2", 4);
        expected.put("B?b1", 5);
        expected.put("B?b1", 7);
        expected.put("B?b2", 6);
        expected.put("B?b2", 8);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFloat1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstanceFloat1();
        SetMultiMap<String, Object> actual = instance.accept(new RelValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("A?a", "100.5");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNull1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstanceNull1();
        SetMultiMap<String, Object> actual = instance.accept(new RelValueCollectingVisitor());
        SetMultiMap<String, Object> expected = new SetMultiMap<>();
        expected.put("A?a", 1);
        expected.put("A?b", "null");
        Assert.assertEquals(expected, actual);
    }

}

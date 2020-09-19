package dynamite.reldb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.reldb.ast.RelationalInstance;

public class RelInstToTextVisitorTest {

    @Test
    public void test1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstance1();
        String instString = instance.accept(new RelInstToTextVisitor());
        RelationalInstance actual = RelInstanceParser.parse(instString);
        Assert.assertEquals(instance, actual);
    }

    @Test
    public void test2() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstance2();
        String instString = instance.accept(new RelInstToTextVisitor());
        RelationalInstance actual = RelInstanceParser.parse(instString);
        Assert.assertEquals(instance, actual);
    }

}

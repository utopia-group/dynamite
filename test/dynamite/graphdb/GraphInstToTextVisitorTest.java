package dynamite.graphdb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.GraphSchema;

public class GraphInstToTextVisitorTest {

    @Test
    public void test1() {
        GraphSchema schema = GraphDbTestUtils.buildGraphInstanceSchema1();
        GraphInstance instance = GraphDbTestUtils.buildGraphInstance1();
        String instString = instance.accept(new GraphInstToTextVisitor());
        GraphInstance actual = GraphInstanceParser.parse(instString, schema);
        Assert.assertEquals(instance, actual);
    }

    @Test
    public void test2() {
        GraphSchema schema = GraphDbTestUtils.buildGraphInstanceSchema2();
        GraphInstance instance = GraphDbTestUtils.buildGraphInstance2();
        String instString = instance.accept(new GraphInstToTextVisitor());
        GraphInstance actual = GraphInstanceParser.parse(instString, schema);
        Assert.assertEquals(instance, actual);
    }

    @Test
    public void test3() {
        GraphSchema schema = GraphDbTestUtils.buildGraphInstanceSchema3();
        GraphInstance instance = GraphDbTestUtils.buildGraphInstance3();
        String instString = instance.accept(new GraphInstToTextVisitor());
        GraphInstance actual = GraphInstanceParser.parse(instString, schema);
        Assert.assertEquals(instance, actual);
    }

}

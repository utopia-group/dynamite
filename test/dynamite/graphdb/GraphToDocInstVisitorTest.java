package dynamite.graphdb;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DocumentInstance;
import dynamite.graphdb.ast.GraphInstance;

public class GraphToDocInstVisitorTest {

    @Test
    public void test3() {
        GraphInstance instance = GraphDbTestUtils.buildGraphInstance3();
        DocumentInstance actual = instance.accept(new GraphToDocInstVisitor());
        DocumentInstance expected = GraphDbTestUtils.buildDocumentInstance3();
        Assert.assertEquals(expected, actual);
    }

}

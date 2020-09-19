package dynamite.datalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class RelationOutputTest {

    private static RelationOutput buildRelationOutput() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList(new String[] { "1", "1", "1" }));
        data.add(Arrays.asList(new String[] { "1", "2", "3" }));
        data.add(Arrays.asList(new String[] { "1", "3", "2" }));
        data.add(Arrays.asList(new String[] { "1", "1", "2" }));
        data.add(Arrays.asList(new String[] { "3", "2", "3" }));
        return new RelationOutput("R", data);
    }

    private static RelationOutput buildRelationOutput01() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList(new String[] { "1", "1" }));
        data.add(Arrays.asList(new String[] { "1", "2" }));
        data.add(Arrays.asList(new String[] { "1", "3" }));
        data.add(Arrays.asList(new String[] { "3", "2" }));
        return new RelationOutput("R[0, 1]", data);
    }

    private static RelationOutput buildRelationOutput02() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList(new String[] { "1", "1" }));
        data.add(Arrays.asList(new String[] { "1", "3" }));
        data.add(Arrays.asList(new String[] { "1", "2" }));
        data.add(Arrays.asList(new String[] { "3", "3" }));
        return new RelationOutput("R[0, 2]", data);
    }

    private static RelationOutput buildRelationOutput12() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList(new String[] { "1", "1" }));
        data.add(Arrays.asList(new String[] { "2", "3" }));
        data.add(Arrays.asList(new String[] { "3", "2" }));
        data.add(Arrays.asList(new String[] { "1", "2" }));
        return new RelationOutput("R[1, 2]", data);
    }

    @Test
    public void testProject01() {
        RelationOutput rel = buildRelationOutput();
        List<Integer> colIndices = Arrays.asList(new Integer[] { 0, 1 });
        RelationOutput expected = buildRelationOutput01();
        Assert.assertEquals(expected, rel.project(colIndices));
    }

    @Test
    public void testProject02() {
        RelationOutput rel = buildRelationOutput();
        List<Integer> colIndices = Arrays.asList(new Integer[] { 0, 2 });
        RelationOutput expected = buildRelationOutput02();
        Assert.assertEquals(expected, rel.project(colIndices));
    }

    @Test
    public void testProject12() {
        RelationOutput rel = buildRelationOutput();
        List<Integer> colIndices = Arrays.asList(new Integer[] { 1, 2 });
        RelationOutput expected = buildRelationOutput12();
        Assert.assertEquals(expected, rel.project(colIndices));
    }

}

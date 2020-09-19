package dynamite.graphdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.graphdb.ast.GIAtomicProp;
import dynamite.graphdb.ast.GIEdge;
import dynamite.graphdb.ast.GIIntValue;
import dynamite.graphdb.ast.GIPropList;
import dynamite.graphdb.ast.GIProperty;
import dynamite.graphdb.ast.GIStrValue;
import dynamite.graphdb.ast.GIVertex;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.VertexInfo;

public class GraphInstSamplingVisitorTest {

    private static GraphInstance buildOriginalGraphInstance() {
        List<GIVertex> vertices = new ArrayList<>();
        {
            String id = "1";
            List<String> labels = Arrays.asList("A");
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("a1", new GIStrValue("P1")),
                    new GIAtomicProp("a2", new GIIntValue(101)),
            }));
            vertices.add(new GIVertex(id, labels, propList));
        }
        {
            String id = "2";
            List<String> labels = Arrays.asList("B");
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("b1", new GIStrValue("P2")),
                    new GIAtomicProp("b2", new GIIntValue(102)),
            }));
            vertices.add(new GIVertex(id, labels, propList));
        }
        {
            String id = "3";
            List<String> labels = Arrays.asList("A");
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("a1", new GIStrValue("P3")),
                    new GIAtomicProp("a2", new GIIntValue(103)),
            }));
            vertices.add(new GIVertex(id, labels, propList));
        }
        {
            String id = "4";
            List<String> labels = Arrays.asList("B");
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("a1", new GIStrValue("P4")),
                    new GIAtomicProp("a2", new GIIntValue(104)),
            }));
            vertices.add(new GIVertex(id, labels, propList));
        }
        List<GIEdge> edges = new ArrayList<>();
        {
            String id = "5";
            String label = "C";
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("c1", new GIIntValue(105)),
            }));
            VertexInfo start = new VertexInfo("1", Arrays.asList("A"));
            VertexInfo end = new VertexInfo("2", Arrays.asList("B"));
            edges.add(new GIEdge(id, label, propList, start, end));
        }
        {
            String id = "6";
            String label = "C";
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("c1", new GIIntValue(106)),
            }));
            VertexInfo start = new VertexInfo("3", Arrays.asList("A"));
            VertexInfo end = new VertexInfo("4", Arrays.asList("B"));
            edges.add(new GIEdge(id, label, propList, start, end));
        }
        return new GraphInstance(vertices, edges);
    }

    private static GraphInstance buildExpectedGraphInstance1() {
        GraphInstance instance = buildOriginalGraphInstance();
        List<GIVertex> vertices = new ArrayList<>();
        vertices.add(instance.vertices.get(0));
        vertices.add(instance.vertices.get(1));
        List<GIEdge> edges = new ArrayList<>();
        edges.add(instance.edges.get(0));
        return new GraphInstance(vertices, edges);
    }

    private static GraphInstance buildExpectedGraphInstance2() {
        GraphInstance instance = buildOriginalGraphInstance();
        List<GIVertex> vertices = new ArrayList<>();
        vertices.add(instance.vertices.get(2));
        vertices.add(instance.vertices.get(3));
        List<GIEdge> edges = new ArrayList<>();
        edges.add(instance.edges.get(1));
        return new GraphInstance(vertices, edges);
    }

    @Test
    public void testSample() {
        GraphInstance instance = buildOriginalGraphInstance();
        BitSet indices1 = BitSet.valueOf(new long[] { 1 });
        GraphInstance actual1 = instance.accept(new GraphInstSamplingVisitor(indices1));
        GraphInstance expected1 = buildExpectedGraphInstance1();
        Assert.assertEquals(expected1, actual1);
        BitSet indices2 = BitSet.valueOf(new long[] { 2 });
        GraphInstance actual2 = instance.accept(new GraphInstSamplingVisitor(indices2));
        GraphInstance expected2 = buildExpectedGraphInstance2();
        Assert.assertEquals(expected2, actual2);
    }

}

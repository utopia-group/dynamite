package dynamite.graphdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dynamite.docdb.InstCanonicalNameVisitor;
import dynamite.docdb.SchemaCanonicalNameVisitor;
import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIFloatValue;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DINullValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.graphdb.ast.GIAtomicProp;
import dynamite.graphdb.ast.GICollection;
import dynamite.graphdb.ast.GICollectionProp;
import dynamite.graphdb.ast.GIEdge;
import dynamite.graphdb.ast.GIFloatValue;
import dynamite.graphdb.ast.GIIntValue;
import dynamite.graphdb.ast.GINullValue;
import dynamite.graphdb.ast.GIPropList;
import dynamite.graphdb.ast.GIProperty;
import dynamite.graphdb.ast.GIStrValue;
import dynamite.graphdb.ast.GIVertex;
import dynamite.graphdb.ast.GSAtomicProp;
import dynamite.graphdb.ast.GSAtomicProp.PropType;
import dynamite.graphdb.ast.GSCollection;
import dynamite.graphdb.ast.GSCollectionProp;
import dynamite.graphdb.ast.GSEdge;
import dynamite.graphdb.ast.GSPropList;
import dynamite.graphdb.ast.GSProperty;
import dynamite.graphdb.ast.GSVertex;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.graphdb.ast.VertexInfo;

public class GraphDbTestUtils {

    //build both Instances and their schema for Instance Parser Test

    // Test 1

    public static GraphInstance buildGraphInstance1() {
        List<GIVertex> vertices = new ArrayList<>();
        {
            String id = "1";
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("prop1", new GIStrValue("P1")),
                    new GIAtomicProp("prop2", new GIIntValue(2)),
            }));
            List<String> labels = Arrays.asList("A");
            vertices.add(new GIVertex(id, labels, propList));
        }
        List<GIEdge> edges = new ArrayList<>();
        return new GraphInstance(vertices, edges);
    }

    public static GraphSchema buildGraphInstanceSchema1() {
        List<GSVertex> vertices = new ArrayList<>();
        {
            List<String> labels = Arrays.asList("A");
            GSPropList propList = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("prop1", PropType.STRING),
                    new GSAtomicProp("prop2", PropType.INT),
            }));
            vertices.add(new GSVertex(labels, propList));
        }
        List<GSEdge> edges = new ArrayList<>();
        return new GraphSchema(vertices, edges);
    }

    // Test 2

    public static GraphInstance buildGraphInstance2() {
        List<GIVertex> vertices = new ArrayList<>();
        List<GIEdge> edges = new ArrayList<>();
        {
            String id = "1";
            String label = "E";
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("prop1", new GIStrValue("P1")),
                    new GIAtomicProp("prop2", new GIIntValue(2)),
            }));
            VertexInfo start = new VertexInfo("1", Arrays.asList("A", "B"));
            VertexInfo end = new VertexInfo("2", Arrays.asList("C", "D"));
            edges.add(new GIEdge(id, label, propList, start, end));
        }
        return new GraphInstance(vertices, edges);
    }

    public static GraphSchema buildGraphInstanceSchema2() {
        List<GSVertex> vertices = new ArrayList<>();
        List<GSEdge> edges = new ArrayList<>();
        {
            String label = "E";
            GSPropList propList = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("prop1", PropType.STRING),
                    new GSAtomicProp("prop2", PropType.INT),
            }));
            edges.add(new GSEdge(label, propList));
        }

        return new GraphSchema(vertices, edges);
    }

    // Test 3 and Test Null 1 and Test Labels 1

    public static GraphInstance buildGraphInstance3() {
        List<GIVertex> vertices = new ArrayList<>();
        {
            String id = "1";
            List<String> labels = Arrays.asList("A");
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("a1", new GIStrValue("P1")),
                    new GIAtomicProp("a2", new GIIntValue(2)),
            }));
            vertices.add(new GIVertex(id, labels, propList));
        }
        {
            String id = "2";
            List<String> labels = Arrays.asList("B");
            List<GIProperty> properties = new ArrayList<>();
            {
                properties.add(new GIAtomicProp("b1", new GIIntValue(1)));
                GIPropList nestedPropList = new GIPropList(Arrays.asList(new GIProperty[] {
                        new GIAtomicProp("b3", new GIIntValue(3)),
                        new GIAtomicProp("b4", new GIStrValue("P4")),
                }));
                List<GIPropList> nestedPropLists = Collections.singletonList(nestedPropList);
                properties.add(new GICollectionProp(new GICollection("b2", nestedPropLists)));
            }
            GIPropList propList = new GIPropList(properties);
            vertices.add(new GIVertex(id, labels, propList));
        }
        List<GIEdge> edges = new ArrayList<>();
        {
            String id = "3";
            String label = "C";
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("c1", new GIFloatValue("1.1")),
                    new GIAtomicProp("c2", new GIIntValue(2)),
                    new GIAtomicProp("c3", new GIStrValue("P3")),
                    new GIAtomicProp("c4", GINullValue.getInstance()),
            }));
            VertexInfo start = new VertexInfo("1", Arrays.asList("A"));
            VertexInfo end = new VertexInfo("2", Arrays.asList("B"));
            edges.add(new GIEdge(id, label, propList, start, end));
        }
        return new GraphInstance(vertices, edges);
    }

    public static GraphSchema buildGraphInstanceSchema3() {
        List<GSVertex> vertices = new ArrayList<>();
        {
            List<String> labels = Arrays.asList("A");
            GSPropList propList = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("a1", PropType.STRING),
                    new GSAtomicProp("a2", PropType.INT),
            }));
            vertices.add(new GSVertex(labels, propList));
        }
        {
            List<String> labels = Arrays.asList("B");
            GSPropList propList = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("b1", PropType.INT),
                    new GSCollectionProp(new GSCollection("b2", new GSPropList(Arrays.asList(new GSProperty[] {
                            new GSAtomicProp("b3", PropType.INT),
                            new GSAtomicProp("b4", PropType.STRING),
                    }))))
            }));
            vertices.add(new GSVertex(labels, propList));
        }
        List<GSEdge> edges = new ArrayList<>();
        {
            String label = "C";
            GSPropList propList = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("c1", PropType.FLOAT),
                    new GSAtomicProp("c2", PropType.INT),
                    new GSAtomicProp("c3", PropType.STRING),
                    new GSAtomicProp("c4", PropType.STRING),
            }));
            edges.add(new GSEdge(label, propList));
        }

        return new GraphSchema(vertices, edges);
    }

    public static DocumentInstance buildDocumentInstance3() {
        List<DICollection> collections = new ArrayList<>();
        { // A
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = Arrays.asList(new DIAttrValue[] {
                        new DIAtomicAttrValue("_id", new DIStrValue("1")),
                        new DIAtomicAttrValue("a1", new DIStrValue("P1")),
                        new DIAtomicAttrValue("a2", new DIIntValue(2)),
                });
                documents.add(new DIDocument(attributes));
            }
            DICollection computers = new DICollection("A", documents);
            collections.add(computers);
        }
        { // B
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("_id", new DIStrValue("2")));
                    attributes.add(new DIAtomicAttrValue("b1", new DIIntValue(1)));
                    { // b2
                        List<DIDocument> docs = new ArrayList<>();
                        {
                            List<DIAttrValue> attrs = Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("b3", new DIIntValue(3)),
                                    new DIAtomicAttrValue("b4", new DIStrValue("P4")),
                            });
                            docs.add(new DIDocument(attrs));
                        }
                        DICollection nestedCollection = new DICollection("b2", docs);
                        attributes.add(new DICollectionAttrValue(nestedCollection));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            DICollection computers = new DICollection("B", documents);
            collections.add(computers);
        }
        { // C
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = Arrays.asList(new DIAttrValue[] {
                        new DIAtomicAttrValue("_start", new DIStrValue("1")),
                        new DIAtomicAttrValue("_end", new DIStrValue("2")),
                        new DIAtomicAttrValue("c1", new DIFloatValue("1.1")),
                        new DIAtomicAttrValue("c2", new DIIntValue(2)),
                        new DIAtomicAttrValue("c3", new DIStrValue("P3")),
                        new DIAtomicAttrValue("c4", DINullValue.getInstance()),
                });
                documents.add(new DIDocument(attributes));
            }
            DICollection computers = new DICollection("C", documents);
            collections.add(computers);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    // Test Labels 2

    public static GraphInstance buildGraphInstance4() {
        List<GIVertex> vertices = new ArrayList<>();
        {
            String id = "1";
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("prop1", new GIStrValue("P1")),
                    new GIAtomicProp("prop2", new GIIntValue(2)),
            }));
            List<String> labels = Arrays.asList("A, B");
            vertices.add(new GIVertex(id, labels, propList));
        }
        List<GIEdge> edges = new ArrayList<>();
        return new GraphInstance(vertices, edges);
    }

    public static GraphSchema buildGraphInstanceSchema4() {
        List<GSVertex> vertices = new ArrayList<>();
        {
            List<String> labelA = Arrays.asList("A");
            GSPropList propListA = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("prop1", PropType.STRING)
            }));
            List<String> labelB = Arrays.asList("B");
            GSPropList propListB = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("prop2", PropType.INT),
            }));
            vertices.add(new GSVertex(labelA, propListA));
            vertices.add(new GSVertex(labelB, propListB));
        }
        List<GSEdge> edges = new ArrayList<>();
        return new GraphSchema(vertices, edges);
    }

    // Test Labels 3

    public static GraphInstance buildGraphInstance5() {
        List<GIVertex> vertices = new ArrayList<>();
        {
            String id = "1";
            GIPropList propList = new GIPropList(Arrays.asList(new GIProperty[] {
                    new GIAtomicProp("prop1", new GIStrValue("P1"))
            }));
            List<String> labels = Arrays.asList("A");
            vertices.add(new GIVertex(id, labels, propList));
        }
        List<GIEdge> edges = new ArrayList<>();
        return new GraphInstance(vertices, edges);
    }

    public static GraphSchema buildGraphInstanceSchema5() {
        List<GSVertex> vertices = new ArrayList<>();
        {
            List<String> labelA = Arrays.asList("A");
            GSPropList propListA = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("prop1", PropType.STRING)
            }));
            vertices.add(new GSVertex(labelA, propListA));
        }
        List<GSEdge> edges = new ArrayList<>();
        return new GraphSchema(vertices, edges);
    }

    /**
     * @return the following graph schema
     *
     * <pre>
     * Vertices:
     * A(a1: Int, a2: String)
     * B(b1: Int, b2: Float, b3: String)
     * Edges:
     * C(c1: Int, c2: String)
     *         </pre>
     */
    public static GraphSchema buildGraphSchema1() {
        List<GSVertex> vertices = new ArrayList<>();
        {
            List<String> labels = Arrays.asList("A");
            GSPropList propList = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("a1", PropType.INT),
                    new GSAtomicProp("a2", PropType.STRING),
            }));
            vertices.add(new GSVertex(labels, propList));
        }
        {
            List<String> labels = Arrays.asList("B");
            GSPropList propList = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("b1", PropType.INT),
                    new GSAtomicProp("b2", PropType.FLOAT),
                    new GSAtomicProp("b3", PropType.STRING),
            }));
            vertices.add(new GSVertex(labels, propList));
        }
        List<GSEdge> edges = new ArrayList<>();
        {
            String label = "C";
            GSPropList propList = new GSPropList(Arrays.asList(new GSProperty[] {
                    new GSAtomicProp("c1", PropType.INT),
                    new GSAtomicProp("c2", PropType.STRING),
            }));
            edges.add(new GSEdge(label, propList));
        }
        return new GraphSchema(vertices, edges);
    }

    public static GraphSchema buildGraphSchema2() {
        return GraphSchemaParser.parse("[\n" +
                "    {\n" +
                "        \"type\": \"node\", \n" +
                "        \"labels\": [\"UserId\"],\n" +
                "        \"properties\": {\n" +
                "            \"user_screen_name\": \"String\",\n" +
                "            \"user_name\": \"String\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"type\": \"node\", \n" +
                "        \"labels\": [\"Location\"],\n" +
                "        \"properties\": {\n" +
                "            \"lat\": \"Float\",\n" +
                "            \"lon\": \"Float\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"type\": \"relationship\",\n" +
                "        \"label\": \"LIVES_IN\",\n" +
                "        \"properties\": {\n" +
                "        }\n" +
                "    }\n" +
                "]");
    }

    /**
     * @return the following document schema
     *
     * <pre>
     * A: [{ a1: Int, a2: String }]
     * B: [{ b1: Int, b2: Float, b3: String }]
     * C: [{ c1: Int, c2: String }]
     *         </pre>
     */
    public static DocumentSchema buildDocumentSchema1() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> attrs = new ArrayList<>();
            {
                attrs.add(new DSAtomicAttr("_id", AttrType.STRING));
                attrs.add(new DSAtomicAttr("a1", AttrType.INT));
                attrs.add(new DSAtomicAttr("a2", AttrType.STRING));
            }
            collections.add(new DSCollection("A", new DSDocument(attrs)));
        }
        {
            List<DSAttribute> attrs = new ArrayList<>();
            {
                attrs.add(new DSAtomicAttr("_id", AttrType.STRING));
                attrs.add(new DSAtomicAttr("b1", AttrType.INT));
                attrs.add(new DSAtomicAttr("b2", AttrType.FLOAT));
                attrs.add(new DSAtomicAttr("b3", AttrType.STRING));
            }
            collections.add(new DSCollection("B", new DSDocument(attrs)));
        }
        {
            List<DSAttribute> attrs = new ArrayList<>();
            {
                attrs.add(new DSAtomicAttr("_start", AttrType.STRING));
                attrs.add(new DSAtomicAttr("_end", AttrType.STRING));
                attrs.add(new DSAtomicAttr("c1", AttrType.INT));
                attrs.add(new DSAtomicAttr("c2", AttrType.STRING));
            }
            collections.add(new DSCollection("C", new DSDocument(attrs)));
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

}

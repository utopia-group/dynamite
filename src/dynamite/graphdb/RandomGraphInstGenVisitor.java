package dynamite.graphdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.exp.Equality;
import dynamite.exp.ForeignKey;
import dynamite.exp.IntegrityConstraint;
import dynamite.graphdb.ast.GIAstNode;
import dynamite.graphdb.ast.GIAtomicProp;
import dynamite.graphdb.ast.GICollection;
import dynamite.graphdb.ast.GICollectionProp;
import dynamite.graphdb.ast.GIEdge;
import dynamite.graphdb.ast.GIFloatValue;
import dynamite.graphdb.ast.GIIntValue;
import dynamite.graphdb.ast.GIPropList;
import dynamite.graphdb.ast.GIProperty;
import dynamite.graphdb.ast.GIStrValue;
import dynamite.graphdb.ast.GIVertex;
import dynamite.graphdb.ast.GSAtomicProp;
import dynamite.graphdb.ast.GSCollection;
import dynamite.graphdb.ast.GSCollectionProp;
import dynamite.graphdb.ast.GSEdge;
import dynamite.graphdb.ast.GSPropList;
import dynamite.graphdb.ast.GSVertex;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.graphdb.ast.IGSAstVisitor;
import dynamite.graphdb.ast.IGraphSchemaVisitor;
import dynamite.graphdb.ast.VertexInfo;
import dynamite.util.NameUtils;
import dynamite.util.RandomValueFactory;
import dynamite.util.SetMultiMap;

/**
 * A graph schema visitor that generates random graph instances.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class RandomGraphInstGenVisitor implements
        IGraphSchemaVisitor<GraphInstance>,
        IGSAstVisitor<GIAstNode> {

    // probability in percentage to use existing values for equalities
    public static final int PROBABILITY = 50;
    // probability in percentage to add new edges based on foreign keys
    public static final int ADD_PROBABILITY = 80;

    private static final String ID_KEY_NAME = "_id";
    private static final String START_KEY_NAME = "_start";
    private static final String END_KEY_NAME = "_end";
    private static final String DEFAULT_VERTEX_ID = "0";

    // random value factory
    private final RandomValueFactory randFactory;
    // size of the table
    private final int size;
    // integrity constraint
    private final IntegrityConstraint constraint;
    // edge label -> label of starting node
    private final Map<String, String> edgeToStartLabelMap;
    // edge label -> label of ending node
    private final Map<String, String> edgeToEndLabelMap;
    // index of current vertex
    private int currVertexIndex = 0;
    // index of current edge
    private int currEdgeIndex = 0;
    // label -> vertex
    private Map<String, GSVertex> labelToVertexMap;
    // generated vertices
    private Set<GSVertex> generatedVertices = new HashSet<>();
    // vertex instances
    private final List<GIVertex> vertexInsts = new ArrayList<>();

    public RandomGraphInstGenVisitor(int size, IntegrityConstraint constraint, long randomSeed) {
        this.size = size;
        this.constraint = constraint;
        this.randFactory = new RandomValueFactory(randomSeed);
        this.edgeToStartLabelMap = buildEdgeToStartLabelMap(constraint);
        this.edgeToEndLabelMap = buildEdgeToEndLabelMap(constraint);
    }

    @Override
    public GIVertex visit(GSVertex vertex) {
        return new GIVertex(nextVertexID(), vertex.labels, visit(vertex.propList));
    }

    @Override
    public GIEdge visit(GSEdge edge) {
        if (!edgeToStartLabelMap.containsKey(edge.label)) {
            throw new IllegalStateException("No starting node schema for edge: " + edge.label);
        }
        if (!edgeToEndLabelMap.containsKey(edge.label)) {
            throw new IllegalStateException("No ending node schema for edge: " + edge.label);
        }
        GSVertex startVertex = labelToVertexMap.get(edgeToStartLabelMap.get(edge.label));
        GSVertex endVertex = labelToVertexMap.get(edgeToEndLabelMap.get(edge.label));
        VertexInfo startVertexInfo = new VertexInfo(DEFAULT_VERTEX_ID);
        VertexInfo endVertexInfo = new VertexInfo(DEFAULT_VERTEX_ID);
        if (startVertex != null) {
            generatedVertices.add(startVertex);
            GIVertex startVertexInst = visit(startVertex);
            vertexInsts.add(startVertexInst);
            startVertexInfo = new VertexInfo(startVertexInst.id, startVertexInst.labels);
        }
        if (endVertex != null) {
            generatedVertices.add(endVertex);
            GIVertex endVertexInst = visit(endVertex);
            vertexInsts.add(endVertexInst);
            endVertexInfo = new VertexInfo(endVertexInst.id, endVertexInst.labels);
        }
        return new GIEdge(nextEdgeID(), edge.label, visit(edge.propList), startVertexInfo, endVertexInfo);
    }

    @Override
    public GICollection visit(GSCollection collection) {
        // NOTE: always generate one property list here
        return new GICollection(collection.name, Collections.singletonList(visit(collection.propList)));
    }

    @Override
    public GIPropList visit(GSPropList propList) {
        return new GIPropList(propList.properties.stream()
                .map(prop -> prop.accept(this))
                .map(GIProperty.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public GIAtomicProp visit(GSAtomicProp atomicProp) {
        switch (atomicProp.type) {
        case INT:
            return new GIAtomicProp(atomicProp.name, new GIIntValue(randFactory.mkInt()));
        case STRING:
            return new GIAtomicProp(atomicProp.name, new GIStrValue(randFactory.mkString()));
        case FLOAT:
            return new GIAtomicProp(atomicProp.name, new GIFloatValue(randFactory.mkFloatString()));
        default:
            throw new IllegalStateException("Unknown property type: " + atomicProp.type.name());
        }
    }

    @Override
    public GICollectionProp visit(GSCollectionProp collectionProp) {
        return new GICollectionProp(visit(collectionProp.collection));
    }

    @Override
    public GraphInstance visit(GraphSchema schema) {
        labelToVertexMap = Collections.unmodifiableMap(buildLabelToVertexMap(schema));
        // generate edges for each edge type
        // also generate connected nodes for each edge
        List<GIEdge> edgeInsts = new ArrayList<>();
        for (GSEdge edge : schema.edges) {
            for (int i = 0; i < size; ++i) {
                edgeInsts.add(visit(edge));
            }
        }
        // generate nodes for each node type that has never been generated
        for (GSVertex vertex : schema.vertices) {
            if (!generatedVertices.contains(vertex)) {
                for (int i = 0; i < size; ++i) {
                    vertexInsts.add(visit(vertex));
                }
            }
        }
        GraphInstance instance = new GraphInstance(vertexInsts, edgeInsts);
        // update graph instance based on integrity constraints
        return updateByConstraint(instance, constraint);
    }

    private GraphInstance updateByConstraint(GraphInstance instance, IntegrityConstraint constraint) {
        GraphInstance updatedInst = instance;
        // update values based on equalities
        for (Equality equality : constraint.equalities) {
            updatedInst = updateByEquality(instance, equality);
        }
        // add more edges based on foreign keys
        List<ForeignKey> pureFKs = extractPureForeignKeys(constraint);
        for (ForeignKey fk : pureFKs) {
            updatedInst = updateByForeignKey(instance, fk);
        }
        return updatedInst;
    }

    private GraphInstance updateByEquality(GraphInstance instance, Equality equality) {
        String fromAttrName = NameUtils.computeSimpleAttrName(equality.from);
        String fromRecName = NameUtils.computeRecordTypeName(equality.from);
        String toAttrName = NameUtils.computeSimpleAttrName(equality.to);
        String toRecName = NameUtils.computeRecordTypeName(equality.to);
        // NOTE: only handles properties in the same object
        if (!toRecName.equals(fromRecName)) throw new IllegalStateException(toRecName);
        // NOTE: only handles ID
        if (!toAttrName.equals(ID_KEY_NAME)) throw new IllegalStateException(toAttrName);

        List<GIVertex> newVertices = new ArrayList<>();
        for (GIVertex vertex : instance.vertices) {
            if (vertex.labels.contains(fromRecName) && toBeReplaced()) {
                GIPropList newPropList = updatePropList(vertex.propList, fromAttrName, vertex.id);
                String newId = equality.replace ? "V" + vertex.id : vertex.id;
                newVertices.add(new GIVertex(newId, vertex.labels, newPropList));
            } else {
                newVertices.add(vertex);
            }
        }

        List<GIEdge> newEdges = new ArrayList<>();
        for (GIEdge edge : instance.edges) {
            if (edge.label.equals(fromRecName)) {
                GIPropList newPropList = updatePropList(edge.propList, fromAttrName, edge.id);
                newEdges.add(new GIEdge(edge.id, edge.label, newPropList, edge.start, edge.end));
            } else {
                newEdges.add(edge);
            }
        }

        return new GraphInstance(newVertices, newEdges);
    }

    private GIPropList updatePropList(GIPropList propList, String attrName, String idName) {
        // NOTE: only handles atomic properties
        List<GIProperty> newProperties = new ArrayList<>();
        for (GIProperty property : propList.properties) {
            if (property.getName().equals(attrName)) {
                newProperties.add(new GIAtomicProp(attrName, new GIStrValue(idName)));
            } else {
                newProperties.add(property);
            }
        }
        return new GIPropList(newProperties);
    }

    private GraphInstance updateByForeignKey(GraphInstance instance, ForeignKey foreignKey) {
        String fromAttrName = NameUtils.computeSimpleAttrName(foreignKey.from);
        String fromRecName = NameUtils.computeRecordTypeName(foreignKey.from);
        // NOTE: only handles edges
        if (!fromAttrName.equals(START_KEY_NAME) && !fromAttrName.equals(END_KEY_NAME)) throw new IllegalStateException(fromAttrName);

        SetMultiMap<String, Object> attrToValuesMap = instance.collectValuesByAttr();
        List<String> values = attrToValuesMap.get(foreignKey.to).stream()
                .map(String.class::cast)
                .collect(Collectors.toList());

        List<GIEdge> newEdges = new ArrayList<>();
        for (GIEdge edge : instance.edges) {
            newEdges.add(edge);
            if (edge.label.equals(fromRecName) && toAdd()) {
                int index = randFactory.mkBoundedInt(values.size());
                String value = values.get(index);
                VertexInfo newStart = fromAttrName.equals(START_KEY_NAME)
                        ? new VertexInfo(value, edge.start.labels)
                        : edge.start;
                VertexInfo newEnd = fromAttrName.equals(END_KEY_NAME)
                        ? new VertexInfo(value, edge.end.labels)
                        : edge.end;
                newEdges.add(new GIEdge(nextEdgeID(), edge.label, edge.propList, newStart, newEnd));
            }
        }
        return new GraphInstance(instance.vertices, newEdges);
    }

    private static Map<String, GSVertex> buildLabelToVertexMap(GraphSchema schema) {
        Map<String, GSVertex> labelToVertexMap = new HashMap<>();
        for (GSVertex vertex : schema.vertices) {
            for (String label : vertex.labels) {
                if (labelToVertexMap.containsKey(label)) {
                    throw new IllegalStateException("Dupliated label: " + label);
                }
                labelToVertexMap.put(label, vertex);
            }
        }
        return labelToVertexMap;
    }

    private static Map<String, String> buildEdgeToStartLabelMap(IntegrityConstraint constraint) {
        Map<String, String> edgeToStartLabelMap = new HashMap<>();
        for (ForeignKey fk : constraint.foreignKeys) {
            String fromAttrName = NameUtils.computeSimpleAttrName(fk.from);
            String toAttrName = NameUtils.computeSimpleAttrName(fk.to);
            if (fromAttrName.equals(START_KEY_NAME) && toAttrName.equals(ID_KEY_NAME)) {
                String edgeName = NameUtils.computeRecordTypeName(fk.from);
                String vertexName = NameUtils.computeRecordTypeName(fk.to);
                if (edgeToStartLabelMap.containsKey(edgeName)) {
                    throw new IllegalStateException("Duplicated edge constraint: " + edgeName);
                }
                edgeToStartLabelMap.put(edgeName, vertexName);
            }
        }
        return edgeToStartLabelMap;
    }

    private static Map<String, String> buildEdgeToEndLabelMap(IntegrityConstraint constraint) {
        Map<String, String> edgeToEndLabelMap = new HashMap<>();
        for (ForeignKey fk : constraint.foreignKeys) {
            String fromAttrName = NameUtils.computeSimpleAttrName(fk.from);
            String toAttrName = NameUtils.computeSimpleAttrName(fk.to);
            if (fromAttrName.equals(END_KEY_NAME) && toAttrName.equals(ID_KEY_NAME)) {
                String edgeName = NameUtils.computeRecordTypeName(fk.from);
                String vertexName = NameUtils.computeRecordTypeName(fk.to);
                if (edgeToEndLabelMap.containsKey(edgeName)) {
                    throw new IllegalStateException("Duplicated edge constraint: " + edgeName);
                }
                edgeToEndLabelMap.put(edgeName, vertexName);
            }
        }
        return edgeToEndLabelMap;
    }

    private static List<ForeignKey> extractPureForeignKeys(IntegrityConstraint constraint) {
        List<ForeignKey> pureFKs = new ArrayList<>();
        for (ForeignKey fk : constraint.foreignKeys) {
            String toAttrName = NameUtils.computeSimpleAttrName(fk.to);
            if (!toAttrName.equals(ID_KEY_NAME)) {
                pureFKs.add(fk);
            }
        }
        return pureFKs;
    }

    private String nextVertexID() {
        return "V" + (++currVertexIndex);
    }

    private String nextEdgeID() {
        return "E" + (++currEdgeIndex);
    }

    private boolean toAdd() {
        int num = randFactory.mkBoundedInt(100);
        return num < ADD_PROBABILITY;
    }

    private boolean toBeReplaced() {
        int num = randFactory.mkBoundedInt(100);
        return num < PROBABILITY;
    }

}

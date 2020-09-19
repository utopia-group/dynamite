package dynamite.graphdb;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import dynamite.graphdb.ast.GIAtomicProp;
import dynamite.graphdb.ast.GICollection;
import dynamite.graphdb.ast.GICollectionProp;
import dynamite.graphdb.ast.GIEdge;
import dynamite.graphdb.ast.GIFloatValue;
import dynamite.graphdb.ast.GIIntValue;
import dynamite.graphdb.ast.GINullValue;
import dynamite.graphdb.ast.GIPropList;
import dynamite.graphdb.ast.GIStrValue;
import dynamite.graphdb.ast.GIVertex;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.IGIAstVisitor;
import dynamite.graphdb.ast.IGIValueVisitor;
import dynamite.graphdb.ast.IGraphInstVisitor;
import dynamite.graphdb.ast.VertexInfo;

/**
 * A visitor that converts a graph instance to text.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class GraphInstToTextVisitor implements
        IGraphInstVisitor<String>,
        IGIAstVisitor<String>,
        IGIValueVisitor<String> {

    @Override
    public String visit(GIIntValue value) {
        return value.toString();
    }

    @Override
    public String visit(GIStrValue value) {
        return value.toString();
    }

    @Override
    public String visit(GIFloatValue value) {
        return value.toString();
    }

    @Override
    public String visit(GINullValue value) {
        return value.toString();
    }

    @Override
    public String visit(GIVertex vertex) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        // type
        builder.append(genAttrJsonName("type")).append(":\"node\",");
        // labels
        builder.append(genAttrJsonName("labels")).append(":[");
        builder.append(vertex.labels.stream()
                .map(label -> genQuotedString(label))
                .collect(Collectors.joining(",")));
        builder.append("],");
        // id
        builder.append(genAttrJsonName("id")).append(":");
        builder.append(genQuotedString(vertex.id)).append(",");
        // properties
        builder.append(genAttrJsonName("properties")).append(":");
        builder.append(vertex.propList.accept(this));
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String visit(GIEdge edge) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        // type
        builder.append(genAttrJsonName("type")).append(":\"relationship\",");
        // label
        builder.append(genAttrJsonName("label")).append(":");
        builder.append(genQuotedString(edge.label)).append(",");
        // id
        builder.append(genAttrJsonName("id")).append(":");
        builder.append(genQuotedString(edge.id)).append(",");
        // properties
        builder.append(genAttrJsonName("properties")).append(":");
        builder.append(edge.propList.accept(this)).append(",");
        // start
        builder.append(genAttrJsonName("start")).append(":");
        builder.append(visit(edge.start)).append(",");
        // end
        builder.append(genAttrJsonName("end")).append(":");
        builder.append(visit(edge.end));
        builder.append("}");
        return builder.toString();
    }

    public String visit(VertexInfo vertexInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        // id
        builder.append(genAttrJsonName("id")).append(":");
        builder.append(genQuotedString(vertexInfo.id));
        // labels
        if (vertexInfo.labels != null) {
            builder.append(",").append(genAttrJsonName("labels")).append(":[");
            builder.append(vertexInfo.labels.stream()
                    .map(label -> genQuotedString(label))
                    .collect(Collectors.joining(",")));
            builder.append("]");
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String visit(GICollection collection) {
        StringBuilder builder = new StringBuilder();
        builder.append(genAttrJsonName(collection.name)).append(":[");
        builder.append(collection.propLists.stream()
                .map(propList -> propList.accept(this))
                .collect(Collectors.joining(",")));
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String visit(GIPropList propList) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(propList.properties.stream()
                .map(prop -> prop.accept(this))
                .collect(Collectors.joining(",")));
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String visit(GIAtomicProp atomicProp) {
        StringBuilder builder = new StringBuilder();
        builder.append(genAttrJsonName(atomicProp.name)).append(":");
        builder.append(atomicProp.value.accept(this));
        return builder.toString();
    }

    @Override
    public String visit(GICollectionProp collectionProp) {
        return collectionProp.collection.accept(this);
    }

    @Override
    public String visit(GraphInstance instance) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(Stream.concat(instance.vertices.stream(), instance.edges.stream())
                .map(astNode -> astNode.accept(this))
                .collect(Collectors.joining(",")));
        builder.append("]");
        return builder.toString();
    }

    private String genAttrJsonName(String attrName) {
        return genQuotedString(attrName);
    }

    private String genQuotedString(String str) {
        return "\"" + str + "\"";
    }

}

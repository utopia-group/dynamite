package dynamite.graphdb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.core.ISchema;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.graphdb.GraphToDocSchemaVisitor;

/**
 * Data structure for graph database schemas.
 */
public final class GraphSchema implements ISchema {

    public final List<GSVertex> vertices;
    public final List<GSEdge> edges;

    // vertex set for equality comparison
    private final Set<GSVertex> vertexSet;
    // edge set for equality comparison
    private final Set<GSEdge> edgeSet;

    public GraphSchema(final List<GSVertex> vertices, final List<GSEdge> edges) {
        Objects.requireNonNull(vertices, "Vertices cannot be null");
        Objects.requireNonNull(edges, "Edges cannot be null");
        this.vertices = Collections.unmodifiableList(vertices);
        this.vertexSet = new HashSet<>(vertices);
        this.edges = Collections.unmodifiableList(edges);
        this.edgeSet = new HashSet<>(edges);
    }

    public List<GSVertex> getVertexSchemas() {
        return vertices;
    }

    public List<GSEdge> getEdgeSchemas() {
        return edges;
    }

    @Override
    public DocumentSchema toDocumentSchema() {
        GraphToDocSchemaVisitor visitor = new GraphToDocSchemaVisitor();
        return visitor.visit(this);
    }

    public <T> T accept(IGraphSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vertices:\n").append(vertices.stream()
                .map(GSVertex::toString)
                .collect(Collectors.joining("\n")));
        builder.append("Edges:\n").append(edges.stream()
                .map(GSEdge::toString)
                .collect(Collectors.joining("\n")));
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexSet, edgeSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GraphSchema))
            return false;
        GraphSchema o = (GraphSchema) obj;
        return Objects.equals(vertexSet, o.vertexSet) && Objects.equals(edgeSet, o.edgeSet);
    }

}

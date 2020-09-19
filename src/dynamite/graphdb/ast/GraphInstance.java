package dynamite.graphdb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.core.IInstance;
import dynamite.datalog.ast.DatalogFact;
import dynamite.docdb.FactGenVisitor;
import dynamite.docdb.ValueCollectingVisitor;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.graphdb.GraphInstToTextVisitor;
import dynamite.graphdb.GraphToDocInstVisitor;
import dynamite.util.SetMultiMap;

/**
 * Data structure for graph database instances.
 */
public final class GraphInstance implements IInstance {

    public final List<GIVertex> vertices;
    public final List<GIEdge> edges;
    // set representation for equality comparison
    private final Set<GIVertex> vertexSet;
    private final Set<GIEdge> edgeSet;

    /**
     * The corresponding document instance of this graph instance.
     * Lazily instantiated in {@code toDocumentInstance()}.
     */
    private DocumentInstance docInstance = null;

    public GraphInstance(final List<GIVertex> vertices, final List<GIEdge> edges) {
        Objects.requireNonNull(vertices, "Vertices cannot be null");
        Objects.requireNonNull(edges, "Edges cannot be null");
        this.vertices = Collections.unmodifiableList(vertices);
        this.edges = Collections.unmodifiableList(edges);
        this.vertexSet = new HashSet<>(vertices);
        this.edgeSet = new HashSet<>(edges);
    }

    @Override
    public List<DatalogFact> toDatalogFacts() {
        DocumentInstance docInst = toDocumentInstance();
        assert docInst != null;
        return docInst.accept(new FactGenVisitor());
    }

    @Override
    public DocumentInstance toDocumentInstance() {
        if (docInstance == null) {
            docInstance = this.accept(new GraphToDocInstVisitor());
        }
        return docInstance;
    }

    @Override
    public SetMultiMap<String, Object> collectValuesByAttr() {
        DocumentInstance docInst = toDocumentInstance();
        assert docInst != null;
        return docInst.accept(new ValueCollectingVisitor());
    }

    public <T> T accept(IGraphInstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toInstanceString() {
        return this.accept(new GraphInstToTextVisitor());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vertices:\n").append(vertices.stream()
                .map(GIVertex::toString)
                .collect(Collectors.joining("\n")));
        builder.append("Edges:\n").append(edges.stream()
                .map(GIEdge::toString)
                .collect(Collectors.joining("\n")));
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, edges);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof GraphInstance)) return false;
        GraphInstance o = (GraphInstance) obj;
        return Objects.equals(vertexSet, o.vertexSet) && Objects.equals(edgeSet, o.edgeSet);
    }

}

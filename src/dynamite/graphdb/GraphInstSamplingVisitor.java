package dynamite.graphdb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.graphdb.ast.GIEdge;
import dynamite.graphdb.ast.GIVertex;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.IGraphInstVisitor;

/**
 * A graph instance visitor that samples the instance.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class GraphInstSamplingVisitor implements
        IGraphInstVisitor<GraphInstance> {

    // indices of edges to sample
    private final BitSet indicesBitSet;

    public GraphInstSamplingVisitor(BitSet indicesBitSet) {
        Objects.requireNonNull(indicesBitSet);
        this.indicesBitSet = indicesBitSet;
    }

    @Override
    public GraphInstance visit(GraphInstance instance) {
        List<GIEdge> edges = new ArrayList<>();
        for (int i = 0; i < instance.edges.size(); ++i) {
            if (indicesBitSet.get(i)) {
                edges.add(instance.edges.get(i));
            }
        }
        List<GIVertex> vertices = findVertices(instance, edges);
        return new GraphInstance(vertices, edges);
    }

    // find all edges that connect to the selected vertices
    @SuppressWarnings("unused")
    private List<GIEdge> findEdges(GraphInstance instance, List<GIVertex> vertices) {
        Set<String> vertexIds = vertices.stream()
                .map(vertex -> vertex.id)
                .collect(Collectors.toSet());
        List<GIEdge> edges = new ArrayList<>();
        for (GIEdge edge : instance.edges) {
            if (vertexIds.contains(edge.start.id) || vertexIds.contains(edge.end.id)) {
                edges.add(edge);
            }
        }
        return edges;
    }

    // find all vertices that connect to the selected edges
    private List<GIVertex> findVertices(GraphInstance instance, List<GIEdge> edges) {
        Set<String> vertexIds = new HashSet<>();
        for (GIEdge edge : edges) {
            vertexIds.add(edge.start.id);
            vertexIds.add(edge.end.id);
        }
        return instance.vertices.stream()
                .filter(vertex -> vertexIds.contains(vertex.id))
                .collect(Collectors.toList());
    }

}

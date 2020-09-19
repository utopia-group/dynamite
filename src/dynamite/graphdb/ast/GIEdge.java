package dynamite.graphdb.ast;

import java.util.Objects;

public final class GIEdge extends GIAstNode {

    public final String id;
    public final String label;
    public final GIPropList propList;
    public final VertexInfo start;
    public final VertexInfo end;

    public GIEdge(String id, String label, GIPropList propList, VertexInfo start, VertexInfo end) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        this.id = id;
        this.label = label;
        this.propList = propList;
        this.start = start;
        this.end = end;
    }

    @Override
    public <T> T accept(IGIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("E(").append(id).append(", ").append(label).append(",\n");
        builder.append("  props: ").append(propList).append("\n");
        builder.append("  start: ").append(start).append("\n");
        builder.append("  end: ").append(end).append("\n");
        builder.append(")\n");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        // equality comparison is purely based on id
        if (obj == this) return true;
        if (!(obj instanceof GIEdge)) return false;
        GIEdge o = (GIEdge) obj;
        return Objects.equals(id, o.id);
    }

}

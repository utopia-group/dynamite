package dynamite.graphdb.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GIVertex extends GIAstNode {

    public final String id;
    public final List<String> labels;
    public final GIPropList propList;

    public GIVertex(String id, final List<String> labels, GIPropList propList) {
        Objects.requireNonNull(labels, "Labels cannot be null");
        this.id = id;
        this.labels = Collections.unmodifiableList(labels);
        this.propList = propList;
    }

    @Override
    public <T> T accept(IGIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("V(%s, %s,\n%s)", id, labels, propList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        // equality comparison is purely based on id
        if (obj == this) return true;
        if (!(obj instanceof GIVertex)) return false;
        GIVertex o = (GIVertex) obj;
        return Objects.equals(id, o.id);
    }

}

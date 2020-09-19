package dynamite.graphdb.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GSVertex extends GSAstNode {

    public final List<String> labels;
    public final GSPropList propList;

    public GSVertex(final List<String> labels, GSPropList propList) {
        Objects.requireNonNull(labels, "Labels cannot be null");
        assert !labels.isEmpty();
        this.labels = Collections.unmodifiableList(labels);
        this.propList = propList;
    }

    public List<String> getLabels() {
        return labels;
    }

    public GSPropList getPropList() {
        return propList;
    }

    @Override
    public <T> T accept(IGSAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("V(%s,\n%s)", labels, propList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labels, propList);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GSVertex))
            return false;
        GSVertex o = (GSVertex) obj;
        return Objects.equals(labels, o.labels) && Objects.equals(propList, o.propList);
    }

}

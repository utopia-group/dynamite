package dynamite.graphdb.ast;

import java.util.Objects;

public final class GSEdge extends GSAstNode {

    public final String label;
    public final GSPropList propList;

    public GSEdge(String label, GSPropList propList) {
        this.label = label;
        this.propList = propList;
    }

    public String getLabel() {
        return label;
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
        return String.format("E(%s,\n%s)", label, propList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, propList);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GSEdge))
            return false;
        GSEdge o = (GSEdge) obj;
        return Objects.equals(label, o.label) && Objects.equals(propList, o.propList);
    }

}

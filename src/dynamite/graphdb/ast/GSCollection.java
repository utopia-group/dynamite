package dynamite.graphdb.ast;

import java.util.Objects;

public final class GSCollection extends GSAstNode {

    public final String name;
    public final GSPropList propList;

    public GSCollection(String name, GSPropList propList) {
        this.name = name;
        this.propList = propList;
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
        return String.format("GSCollection(%s, %s)", name, propList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, propList);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GSCollection))
            return false;
        GSCollection o = (GSCollection) obj;
        return Objects.equals(name, o.name) && Objects.equals(propList, o.propList);
    }

}

package dynamite.graphdb.ast;

import java.util.Objects;

public final class GIAtomicProp extends GIProperty {

    public final String name;
    public final GIValue value;

    public GIAtomicProp(String name, GIValue value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(IGIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof GIAtomicProp)) return false;
        GIAtomicProp o = (GIAtomicProp) obj;
        return Objects.equals(name, o.name) && Objects.equals(value, o.value);
    }

}

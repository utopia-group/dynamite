package dynamite.graphdb.ast;

import java.util.Objects;

public final class GICollectionProp extends GIProperty {

    public final GICollection collection;

    public GICollectionProp(GICollection collection) {
        this.collection = collection;
    }

    @Override
    public String getName() {
        return collection.name;
    }

    @Override
    public <T> T accept(IGIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("GICollectionProp(%s)", collection);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(collection);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof GICollectionProp)) return false;
        GICollectionProp o = (GICollectionProp) obj;
        return Objects.equals(collection, o.collection);
    }

}

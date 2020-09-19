package dynamite.graphdb.ast;

import java.util.Objects;

public final class GSCollectionProp extends GSProperty {

    public final GSCollection collection;

    public GSCollection getCollection() {
        return collection;
    }

    public GSCollectionProp(GSCollection collection) {
        this.collection = collection;
    }

    @Override
    public <T> T accept(IGSAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getName() {
        return collection.name;
    }

    @Override
    public String toString() {
        return String.format("GSCollectionProp(%s)", collection);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(collection);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GSCollectionProp))
            return false;
        GSCollectionProp o = (GSCollectionProp) obj;
        return Objects.equals(collection, o.collection);
    }

}

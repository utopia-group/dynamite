package dynamite.docdb.ast;

import java.util.Objects;

/**
 * Document instance AST node for collection attribute-value pairs.
 */
public final class DICollectionAttrValue extends DIAttrValue {

    public final DICollection collection;

    public DICollectionAttrValue(DICollection collection) {
        this.collection = collection;
    }

    @Override
    public String getCanonicalName() {
        return collection.getCanonicalName();
    }

    @Override
    public String getAttributeName() {
        return collection.name;
    }

    @Override
    public <T> T accept(IDIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("CollectionAttrValue(%s)", collection);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(collection);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DICollectionAttrValue)) return false;
        DICollectionAttrValue o = (DICollectionAttrValue) obj;
        return Objects.equals(collection, o.collection);
    }

}

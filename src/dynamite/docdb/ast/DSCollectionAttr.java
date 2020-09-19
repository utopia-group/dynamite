package dynamite.docdb.ast;

import java.util.Objects;

/**
 * Document schema AST node for collection attributes.
 */
public final class DSCollectionAttr extends DSAttribute {

    public final DSCollection collection;

    public DSCollectionAttr(DSCollection collection) {
        this.collection = collection;
    }

    @Override
    public String getCanonicalName() {
        return collection.getCanonicalName();
    }

    @Override
    public String getName() {
        return collection.name;
    }

    @Override
    public <T> T accept(IDSAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("CollectionAttr(%s)", collection);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(collection);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DSCollectionAttr)) return false;
        DSCollectionAttr o = (DSCollectionAttr) obj;
        return Objects.equals(collection, o.collection);
    }

}

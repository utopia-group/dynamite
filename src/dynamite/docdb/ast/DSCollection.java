package dynamite.docdb.ast;

import java.util.Objects;

/**
 * Document schema AST node for collections.
 */
public final class DSCollection extends DSAstNode {

    public final String name;
    public final DSDocument document;

    private String canonicalName = null;

    public DSCollection(String name, DSDocument document) {
        this.name = name;
        this.document = document;
    }

    public void setCanonicalName(String canonicalName) {
        if (this.canonicalName != null) {
            throw new IllegalStateException(String.format("Canonical name %s is immutable", this.canonicalName));
        }
        this.canonicalName = canonicalName;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    @Override
    public <T> T accept(IDSAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("Collection(%s, %s)", name, document);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, document);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DSCollection)) return false;
        DSCollection o = (DSCollection) obj;
        return Objects.equals(name, o.name) && Objects.equals(document, o.document);
    }

}

package dynamite.docdb.ast;

import java.util.Objects;

/**
 * Document instance AST node for atomic attribute-value pairs.
 */
public final class DIAtomicAttrValue extends DIAttrValue {

    public final String name;
    public final DIValue value;

    // canonical name of this attribute
    private String canonicalName;

    public DIAtomicAttrValue(String name, DIValue value) {
        this.name = name;
        this.value = value;
    }

    public void setCanonicalName(String canonicalName) {
        if (this.canonicalName != null) {
            throw new IllegalStateException(String.format("Canonical name %s is immutable", this.canonicalName));
        }
        this.canonicalName = canonicalName;
    }

    @Override
    public String getCanonicalName() {
        return canonicalName;
    }

    @Override
    public String getAttributeName() {
        return name;
    }

    @Override
    public <T> T accept(IDIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DIAtomicAttrValue)) return false;
        DIAtomicAttrValue o = (DIAtomicAttrValue) obj;
        return Objects.equals(name, o.name) && Objects.equals(value, o.value);
    }

}

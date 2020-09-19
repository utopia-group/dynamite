package dynamite.docdb.ast;

import java.util.Objects;

/**
 * Document schema AST node for atomic attributes.
 */
public final class DSAtomicAttr extends DSAttribute {

    public static enum AttrType {
        INT, STRING, FLOAT
    }

    public final String name;
    public final AttrType type;

    private String canonicalName;

    public DSAtomicAttr(String name, AttrType type) {
        this.name = name;
        this.type = type;
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
    public String getName() {
        return name;
    }

    public static String attrTypeToString(AttrType type) {
        switch (type) {
        case INT:
            return "Int";
        case STRING:
            return "String";
        case FLOAT:
            return "Float";
        default:
            throw new RuntimeException(String.format("Unknown type: %s", type.name()));
        }
    }

    public static AttrType stringToAttrType(String typeString) {
        switch (typeString) {
        case "Int":
            return AttrType.INT;
        case "String":
            return AttrType.STRING;
        case "Float":
            return AttrType.FLOAT;
        default:
            throw new RuntimeException(String.format("Unknown type string: %s", typeString));
        }
    }

    @Override
    public <T> T accept(IDSAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("AtomicAttr(%s, %s)", name, attrTypeToString(type));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DSAtomicAttr)) return false;
        DSAtomicAttr o = (DSAtomicAttr) obj;
        return Objects.equals(name, o.name) && Objects.equals(type, o.type);
    }

}

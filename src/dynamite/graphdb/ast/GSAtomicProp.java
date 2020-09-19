package dynamite.graphdb.ast;

import java.util.Objects;

public final class GSAtomicProp extends GSProperty {

    public enum PropType {
        INT, STRING, FLOAT
    }

    public final String name;
    public final PropType type;

    public GSAtomicProp(String name, PropType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    public static String propTypeToString(PropType type) {
        switch (type) {
        case INT:
            return "Int";
        case STRING:
            return "String";
        case FLOAT:
            return "Float";
        default:
            throw new IllegalArgumentException("Unknown property type: " + type.name());
        }
    }

    public static PropType stringToPropType(String typeString) {
        switch (typeString) {
        case "Int":
            return PropType.INT;
        case "String":
            return PropType.STRING;
        case "Float":
            return PropType.FLOAT;
        default:
            throw new IllegalArgumentException("Unknown property type string: " + typeString);
        }
    }

    @Override
    public <T> T accept(IGSAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, propTypeToString(type));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof GSAtomicProp)) return false;
        GSAtomicProp o = (GSAtomicProp) obj;
        return Objects.equals(name, o.name) && Objects.equals(type, o.type);
    }

}

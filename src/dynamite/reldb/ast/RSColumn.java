package dynamite.reldb.ast;

import java.util.Objects;

public final class RSColumn {

    public enum ColumnType {
        INT, STRING, FLOAT
    }

    public final String name;
    public final ColumnType type;
    // canonical name
    private String canonicalName;

    public RSColumn(String name, ColumnType type) {
        this.name = name;
        this.type = type;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        if (this.canonicalName != null) {
            throw new IllegalStateException("Canonical name has been set to " + canonicalName);
        }
        this.canonicalName = canonicalName;
    }

    public static String columnTypeToString(ColumnType type) {
        switch (type) {
        case INT:
            return "Int";
        case STRING:
            return "String";
        case FLOAT:
            return "Float";
        default:
            throw new IllegalArgumentException("Unknown column type: " + type.name());
        }
    }

    public static ColumnType stringToColumnType(String typeString) {
        switch (typeString) {
        case "Int":
            return ColumnType.INT;
        case "String":
            return ColumnType.STRING;
        case "Float":
            return ColumnType.FLOAT;
        default:
            throw new IllegalStateException("Unknown column type string: " + typeString);
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, columnTypeToString(type));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RSColumn)) return false;
        RSColumn o = (RSColumn) obj;
        return Objects.equals(name, o.name) && Objects.equals(type, o.type);
    }

}

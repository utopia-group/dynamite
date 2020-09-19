package dynamite.docdb.ast;

/**
 * Document instance AST node for integer values.
 */
public final class DIIntValue extends DIValue {

    public final int value;

    public DIIntValue(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(IDIValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DIIntValue)) return false;
        DIIntValue o = (DIIntValue) obj;
        return value == o.value;
    }
}

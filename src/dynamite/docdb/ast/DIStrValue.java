package dynamite.docdb.ast;

import java.util.Objects;

/**
 * Document instance AST node for string values.
 */
public final class DIStrValue extends DIValue {

    public final String value;

    public DIStrValue(String value) {
        this.value = value;
    }

    @Override
    public <T> T accept(IDIValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DIStrValue)) return false;
        DIStrValue o = (DIStrValue) obj;
        return Objects.equals(value, o.value);
    }
}

package dynamite.docdb.ast;

import java.util.Objects;

/**
 * Document instance AST node for floating point values.
 */
public final class DIFloatValue extends DIValue {

    public final double value;
    // for equality check
    public final String valueString;

    public DIFloatValue(String valueString) {
        this.valueString = valueString;
        this.value = Double.parseDouble(valueString);
    }

    @Override
    public <T> T accept(IDIValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return valueString;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valueString);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DIFloatValue)) return false;
        DIFloatValue o = (DIFloatValue) obj;
        return Objects.equals(valueString, o.valueString);
    }

}

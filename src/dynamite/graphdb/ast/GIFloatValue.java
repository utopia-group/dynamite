package dynamite.graphdb.ast;

import java.util.Objects;

public final class GIFloatValue extends GIValue {

    public final double value;
    // for equality comparison
    public final String valueString;

    public GIFloatValue(String valueString) {
        this.valueString = valueString;
        this.value = Double.parseDouble(valueString);
    }

    @Override
    public <T> T accept(IGIValueVisitor<T> visitor) {
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
        if (!(obj instanceof GIFloatValue)) return false;
        GIFloatValue o = (GIFloatValue) obj;
        return Objects.equals(valueString, o.valueString);
    }
}

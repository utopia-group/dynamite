package dynamite.reldb.ast;

import java.util.Objects;

public final class RIFloatValue extends RIValue {

    public final double value;
    public final String valueString;

    public RIFloatValue(String valueString) {
        this.value = Double.parseDouble(valueString);
        this.valueString = valueString;
    }

    @Override
    public <T> T accept(IRIValueVisitor<T> visitor) {
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
        if (!(obj instanceof RIFloatValue)) return false;
        RIFloatValue o = (RIFloatValue) obj;
        return Objects.equals(valueString, o.valueString);
    }

}

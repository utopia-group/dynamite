package dynamite.reldb.ast;

import java.util.Objects;

public final class RIStrValue extends RIValue {

    public final String value;

    public RIStrValue(String value) {
        this.value = value;
    }

    @Override
    public <T> T accept(IRIValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RIStrValue)) return false;
        RIStrValue o = (RIStrValue) obj;
        return Objects.equals(value, o.value);
    }

}

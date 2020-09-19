package dynamite.graphdb.ast;

import java.util.Objects;

public final class GIIntValue extends GIValue {

    public final int value;

    public GIIntValue(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(IGIValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof GIIntValue)) return false;
        GIIntValue o = (GIIntValue) obj;
        return Objects.equals(value, o.value);
    }

}

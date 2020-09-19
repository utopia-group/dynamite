package dynamite.graphdb.ast;

import java.util.Objects;

public final class GIStrValue extends GIValue {

    public final String value;

    public GIStrValue(String value) {
        this.value = value;
    }

    @Override
    public <T> T accept(IGIValueVisitor<T> visitor) {
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
        if (!(obj instanceof GIStrValue)) return false;
        GIStrValue o = (GIStrValue) obj;
        return Objects.equals(value, o.value);
    }
}

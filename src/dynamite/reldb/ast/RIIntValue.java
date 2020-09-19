package dynamite.reldb.ast;

public final class RIIntValue extends RIValue {

    public final int value;

    public RIIntValue(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(IRIValueVisitor<T> visitor) {
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
        if (!(obj instanceof RIIntValue)) return false;
        RIIntValue o = (RIIntValue) obj;
        return value == o.value;
    }

}

package dynamite.datalog.ast;

public final class IntLiteral extends DatalogExpression {

    public final int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(IExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return String.format("IntLiteral(%d)", value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof IntLiteral)) return false;
        IntLiteral o = (IntLiteral) obj;
        return value == o.value;
    }

}

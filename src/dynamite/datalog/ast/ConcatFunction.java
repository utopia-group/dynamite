package dynamite.datalog.ast;

public final class ConcatFunction extends DatalogExpression {

    public final DatalogExpression lhs;
    public final DatalogExpression rhs;

    public ConcatFunction(DatalogExpression lhs, DatalogExpression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public <T> T accept(IExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.format("cat(%s, %s)", lhs.toSouffle(), rhs.toSouffle());
    }

    @Override
    public String toString() {
        return String.format("Concat(%s, %s)", lhs, rhs);
    }

}

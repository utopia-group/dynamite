package dynamite.smt.ast;

/**
 * AST node for integer literals.
 */
public final class LIntLiteral extends LExpr {

    public final int value;

    public LIntLiteral(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ILExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}

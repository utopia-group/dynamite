package dynamite.smt.ast;

/**
 * AST node for constants (integer variables).
 */
public final class LConst extends LExpr {

    public final String name;

    public LConst(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(ILExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return name;
    }

}

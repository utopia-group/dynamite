package dynamite.smt.ast;

/**
 * AST node for range predicate: lowerBound <= expr <= upperBound
 */
public final class LRangePred extends LFormula {

    public final LExpr expr;
    public final LExpr lowerBound;
    public final LExpr upperBound;

    public LRangePred(LExpr expr, LExpr lowerBound, LExpr upperBound) {
        this.expr = expr;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public <T> T accept(ILFormulaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s <= %s <= %s", lowerBound, expr, upperBound);
    }

}

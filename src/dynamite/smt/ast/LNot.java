package dynamite.smt.ast;

/**
 * AST node for negations.
 */
public final class LNot extends LFormula {

    public final LFormula formula;

    public LNot(LFormula formula) {
        this.formula = formula;
    }

    @Override
    public <T> T accept(ILFormulaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("not(%s)", formula);
    }

}

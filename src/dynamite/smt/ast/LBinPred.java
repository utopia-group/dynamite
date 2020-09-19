package dynamite.smt.ast;

/**
 * AST node for binary predicates.
 */
public final class LBinPred extends LFormula {

    public enum Lop {
        EQ, NE
    }

    public final Lop op;
    public final LExpr lhs;
    public final LExpr rhs;

    public LBinPred(Lop op, LExpr lhs, LExpr rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public static String operatorToString(Lop op) {
        switch (op) {
        case EQ:
            return "=";
        case NE:
            return "!=";
        default:
            throw new RuntimeException(String.format("Unknown operator: %s", op.name()));
        }
    }

    public static Lop stringToOperator(String opString) {
        switch (opString) {
        case "=":
            return Lop.EQ;
        case "!=":
            return Lop.NE;
        default:
            throw new RuntimeException(String.format("Unknown operator: %s", opString));
        }
    }

    @Override
    public <T> T accept(ILFormulaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", lhs, operatorToString(op), rhs);
    }

}

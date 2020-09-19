package dynamite.datalog.ast;

public final class BinaryPredicate extends DatalogPredicate {

    public static enum Operator {
        EQ, NE, LT, LE, GT, GE
    }

    public final DatalogExpression lhs;
    public final Operator op;
    public final DatalogExpression rhs;

    public BinaryPredicate(DatalogExpression lhs, Operator op, DatalogExpression rhs) {
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }

    public static String operatorToString(Operator op) {
        switch (op) {
        case EQ:
            return "=";
        case NE:
            return "!=";
        case LT:
            return "<";
        case LE:
            return "<=";
        case GT:
            return ">";
        case GE:
            return ">=";
        default:
            throw new RuntimeException(String.format("Unknown operator: %s", op.name()));
        }
    }

    public static Operator stringToOperator(String opString) {
        switch (opString) {
        case "=":
            return Operator.EQ;
        case "!=":
            return Operator.NE;
        case "<":
            return Operator.LT;
        case "<=":
            return Operator.LE;
        case ">":
            return Operator.GT;
        case ">=":
            return Operator.GE;
        default:
            throw new RuntimeException(String.format("Unknown operator: %s", opString));
        }
    }

    @Override
    public <T> T accept(IPredicateVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.format("%s %s %s", lhs.toSouffle(), operatorToString(op), rhs.toSouffle());
    }

    @Override
    public String toString() {
        return String.format("BinaryPred(%s, %s, %s)", lhs, op, rhs);
    }

}

package dynamite.smt.ast;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AST node for boolean functions (predicates).
 */
public final class LBoolFunc extends LFormula {

    public final String name;
    public final List<LExpr> arguments;

    public LBoolFunc(String name, List<LExpr> arguments) {
        this.name = name;
        this.arguments = Collections.unmodifiableList(arguments);
    }

    @Override
    public <T> T accept(ILFormulaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", name, arguments.stream()
                .map(LExpr::toString)
                .collect(Collectors.joining(", ")));
    }

}

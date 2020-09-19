package dynamite.smt.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AST node for conjunctions.
 */
public final class LAnd extends LFormula {

    public final List<LFormula> conjuncts;

    public LAnd(List<LFormula> conjuncts) {
        Objects.requireNonNull(conjuncts);
        assert conjuncts.size() >= 1;
        this.conjuncts = Collections.unmodifiableList(conjuncts);
    }

    @Override
    public <T> T accept(ILFormulaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("and(%s)", conjuncts.stream()
                .map(LFormula::toString)
                .collect(Collectors.joining(", ")));
    }

}

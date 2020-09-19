package dynamite.smt.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AST node for disjunctions.
 */
public final class LOr extends LFormula {

    public final List<LFormula> disjuncts;

    public LOr(List<LFormula> disjuncts) {
        Objects.requireNonNull(disjuncts);
        assert disjuncts.size() >= 1;
        this.disjuncts = Collections.unmodifiableList(disjuncts);
    }

    @Override
    public <T> T accept(ILFormulaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("or(%s)", disjuncts.stream()
                .map(LFormula::toString)
                .collect(Collectors.joining(", ")));
    }

}

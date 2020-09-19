package dynamite.smt.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Data structure for SMT constraints, which is a conjunction of formulas.
 */
public final class Constraint {

    public final List<LFormula> formulas;

    public Constraint(List<LFormula> formulas) {
        Objects.requireNonNull(formulas);
        this.formulas = Collections.unmodifiableList(formulas);
    }

    public static Constraint merge(Constraint... constraints) {
        Objects.requireNonNull(constraints);
        List<LFormula> list = new ArrayList<>();
        for (Constraint constraint : constraints) {
            assert constraint != null;
            list.addAll(constraint.formulas);
        }
        return new Constraint(list);
    }

    public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formulas.stream()
                .map(LFormula::toString)
                .collect(Collectors.joining("\n"));
    }

}

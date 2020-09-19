package dynamite.datalog.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DatalogRule extends DatalogStatement {

    public final List<RelationPredicate> heads;
    public final List<DatalogPredicate> bodies;

    public DatalogRule(final List<RelationPredicate> heads, final List<DatalogPredicate> bodies) {
        Objects.requireNonNull(heads, "Heads cannot be null");
        Objects.requireNonNull(bodies, "Bodies cannot be null");
        this.heads = Collections.unmodifiableList(heads);
        this.bodies = Collections.unmodifiableList(bodies);
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.format("%s :-%s.",
                heads.stream().map(RelationPredicate::toSouffle).collect(Collectors.joining(", ")),
                bodies.stream().map(pred -> "\n    " + pred.toSouffle()).collect(Collectors.joining(",")));
    }

    @Override
    public String toString() {
        return String.format("Rule(%s, %s)", heads, bodies);
    }

}

package dynamite.datalog.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RelationPredicate extends DatalogPredicate {

    public final String relation;
    public final List<DatalogExpression> arguments;

    public RelationPredicate(String relation, final List<DatalogExpression> arguments) {
        Objects.requireNonNull(arguments, "Arguments cannot be null");
        this.relation = relation;
        this.arguments = Collections.unmodifiableList(arguments);
    }

    public int arity() {
        return arguments.size();
    }

    @Override
    public <T> T accept(IPredicateVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.format("%s(%s)", relation, arguments.stream()
                .map(DatalogExpression::toSouffle)
                .collect(Collectors.joining(", ")));
    }

    @Override
    public String toString() {
        return String.format("RelationPred(%s, %s)", relation, arguments);
    }

}

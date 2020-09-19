package dynamite.datalog.ast;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ExpressionHole extends DatalogExpression {

    /**
     * The name of the hole, which must be unique.
     */
    public final String name;
    /**
     * The domain of the hole, which is a list of Datalog expressions.
     */
    public final List<DatalogExpression> domain;

    public ExpressionHole(String name, List<DatalogExpression> domain) {
        this.name = name;
        this.domain = Collections.unmodifiableList(domain);
    }

    @Override
    public <T> T accept(IExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.format("?%s {%s}", name, domain.stream()
                .map(DatalogExpression::toSouffle)
                .collect(Collectors.joining(", ")));
    }

    @Override
    public String toString() {
        return String.format("ExprHole(%s, %s)", name, domain);
    }

}

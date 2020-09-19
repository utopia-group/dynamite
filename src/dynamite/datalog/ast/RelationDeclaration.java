package dynamite.datalog.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RelationDeclaration extends DatalogStatement {

    public final String relation;
    public final List<TypedAttribute> attributes;

    public RelationDeclaration(String relation, final List<TypedAttribute> attributes) {
        Objects.requireNonNull(attributes, "Attributes cannot be null");
        this.relation = relation;
        this.attributes = Collections.unmodifiableList(attributes);
    }

    public int arity() {
        return attributes.size();
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.format(".decl %s(%s)", relation, attributes.stream()
                .map(TypedAttribute::toSouffle)
                .collect(Collectors.joining(", ")));
    }

    @Override
    public String toString() {
        return String.format("RelationDecl(%s, %s)", relation, attributes);
    }

}

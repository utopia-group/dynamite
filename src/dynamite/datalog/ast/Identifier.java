package dynamite.datalog.ast;

import java.util.Objects;

public final class Identifier extends DatalogExpression {

    public final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(IExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("Ident(%s)", name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Identifier)) return false;
        Identifier o = (Identifier) obj;
        return Objects.equals(name, o.name);
    }

}

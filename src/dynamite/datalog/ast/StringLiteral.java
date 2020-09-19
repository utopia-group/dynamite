package dynamite.datalog.ast;

import java.util.Objects;

public final class StringLiteral extends DatalogExpression {

    public final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public <T> T accept(IExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.format("\"%s\"", value);
    }

    @Override
    public String toString() {
        return String.format("StrLiteral(%s)", value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof StringLiteral)) return false;
        StringLiteral o = (StringLiteral) obj;
        return Objects.equals(value, o.value);
    }

}

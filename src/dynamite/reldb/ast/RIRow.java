package dynamite.reldb.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class RIRow {

    public final List<RIValue> values;

    public RIRow(List<RIValue> values) {
        this.values = Collections.unmodifiableList(values);
    }

    public <T> T accept(IRIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return values.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RIRow)) return false;
        RIRow o = (RIRow) obj;
        return Objects.equals(values, o.values);
    }

}

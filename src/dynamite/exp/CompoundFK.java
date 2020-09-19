package dynamite.exp;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CompoundFK {

    // attribute names from which the foreign key points
    public final List<String> fromAttrs;
    // attribute names to which the foreign key points
    public final List<String> toAttrs;

    public CompoundFK(List<String> fromAttrs, List<String> toAttrs) {
        this.fromAttrs = Collections.unmodifiableList(fromAttrs);
        this.toAttrs = Collections.unmodifiableList(toAttrs);
    }

    @Override
    public String toString() {
        return String.format("%s --> %s", fromAttrs, toAttrs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromAttrs, toAttrs);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof CompoundFK)) return false;
        CompoundFK o = (CompoundFK) obj;
        return Objects.equals(fromAttrs, o.fromAttrs) && Objects.equals(toAttrs, o.toAttrs);
    }

}

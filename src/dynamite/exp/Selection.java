package dynamite.exp;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Selection {

    // attribute name
    public final String attribute;
    // list of values
    public final List<String> values;

    public Selection(String attribute, final List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.attribute = attribute;
        this.values = Collections.unmodifiableList(values);
    }

    @Override
    public String toString() {
        return String.format("%s <- %s", attribute, values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Selection)) return false;
        Selection o = (Selection) obj;
        return Objects.equals(attribute, o.attribute) && Objects.equals(values, o.values);
    }

}

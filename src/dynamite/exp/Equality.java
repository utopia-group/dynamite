package dynamite.exp;

import java.util.Objects;

public final class Equality {

    // attribute name from which the equality assigns
    public final String from;
    // attribute name to which the equality assigns
    public final String to;
    // flag indicating whether randomly replace the from name
    public final boolean replace;

    public Equality(String from, String to, boolean replace) {
        this.from = from;
        this.to = to;
        this.replace = replace;
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s", from, to, replace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Equality)) return false;
        Equality o = (Equality) obj;
        return Objects.equals(from, o.from) && Objects.equals(to, o.to);
    }

}

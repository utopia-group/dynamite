package dynamite.exp;

import java.util.Objects;

public final class ForeignKey {

    // attribute name from which the foreign key points
    public final String from;
    // attribute name to which the foreign key points
    public final String to;

    public ForeignKey(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", from, to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ForeignKey)) return false;
        ForeignKey o = (ForeignKey) obj;
        return Objects.equals(from, o.from) && Objects.equals(to, o.to);
    }

}

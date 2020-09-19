package dynamite.util;

import java.util.Objects;

/**
 * A generic data structure for immutable pairs.
 *
 * @param <T1> type for the first element
 * @param <T2> type for the second element
 */
public class ImmutablePair<T1, T2> {
    public final T1 first;
    public final T2 second;

    public ImmutablePair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ImmutablePair)) return false;
        @SuppressWarnings("rawtypes")
        ImmutablePair o = (ImmutablePair) obj;
        return Objects.equals(first, o.first) && Objects.equals(second, o.second);
    }

}

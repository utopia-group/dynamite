package dynamite.graphdb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class GICollection extends GIAstNode {

    public final String name;
    public final List<GIPropList> propLists;
    // set representation for equality comparison
    private final Set<GIPropList> propListSet;

    public GICollection(String name, final List<GIPropList> propLists) {
        this.name = name;
        this.propLists = Collections.unmodifiableList(propLists);
        this.propListSet = new HashSet<>(propLists);
    }

    @Override
    public <T> T accept(IGIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("GICollection(%s, %s)", name, propLists.stream()
                .map(GIPropList::toString)
                .collect(Collectors.joining("\n")));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, propListSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof GICollection)) return false;
        GICollection o = (GICollection) obj;
        return Objects.equals(name, o.name) && Objects.equals(propListSet, o.propListSet);
    }

}

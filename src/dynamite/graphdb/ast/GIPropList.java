package dynamite.graphdb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class GIPropList extends GIAstNode {

    public final List<GIProperty> properties;
    // property set for equality comparison
    private final Set<GIProperty> propertySet;

    public GIPropList(final List<GIProperty> properties) {
        this.properties = Collections.unmodifiableList(properties);
        this.propertySet = new HashSet<>(properties);
    }

    @Override
    public <T> T accept(IGIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return properties.stream()
                .map(GIProperty::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(propertySet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof GIPropList)) return false;
        GIPropList o = (GIPropList) obj;
        return Objects.equals(propertySet, o.propertySet);
    }

}

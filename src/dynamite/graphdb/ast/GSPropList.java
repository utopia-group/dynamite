package dynamite.graphdb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class GSPropList extends GSAstNode {

    public final List<GSProperty> properties;
    // property set for equality comparison
    private final Set<GSProperty> propertySet;

    public GSPropList(final List<GSProperty> properties) {
        Objects.requireNonNull(properties);
        this.properties = Collections.unmodifiableList(properties);
        this.propertySet = new HashSet<>(properties);
    }

    public List<GSProperty> getProperties() {
        return properties;
    }

    @Override
    public <T> T accept(IGSAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return properties.stream()
                .map(GSProperty::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(propertySet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GSPropList))
            return false;
        GSPropList o = (GSPropList) obj;
        return Objects.equals(propertySet, o.propertySet);
    }

}

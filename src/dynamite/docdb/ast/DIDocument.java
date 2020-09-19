package dynamite.docdb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Document instance AST node for documents.
 */
public final class DIDocument extends DIAstNode {

    public final List<DIAttrValue> attributes;
    // set representation for equality comparison
    private final Set<DIAttrValue> attributeSet;

    public DIDocument(final List<DIAttrValue> attributes) {
        Objects.requireNonNull(attributes, "Attributes cannot be null");
        this.attributes = Collections.unmodifiableList(attributes);
        this.attributeSet = new HashSet<>(attributes);
    }

    @Override
    public <T> T accept(IDIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("{\n%s }", attributes.stream()
                .map(DIAttrValue::toString)
                .collect(Collectors.joining(",\n")));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributeSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DIDocument)) return false;
        DIDocument o = (DIDocument) obj;
        return Objects.equals(attributeSet, o.attributeSet);
    }
}

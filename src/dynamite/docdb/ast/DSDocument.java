package dynamite.docdb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Document schema AST node for documents.
 */
public final class DSDocument extends DSAstNode {

    public final List<DSAttribute> attributes;
    // set representation for equality comparison
    private final Set<DSAttribute> attributeSet;

    public DSDocument(final List<DSAttribute> attributes) {
        Objects.requireNonNull(attributes, "Attributes cannot be null");
        this.attributes = Collections.unmodifiableList(attributes);
        this.attributeSet = new HashSet<>(attributes);
    }

    @Override
    public <T> T accept(IDSAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("{\n%s }", attributes.stream()
                .map(DSAttribute::toString)
                .collect(Collectors.joining("\n")));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributeSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DSDocument)) return false;
        DSDocument o = (DSDocument) obj;
        return Objects.equals(attributeSet, o.attributeSet);
    }
}

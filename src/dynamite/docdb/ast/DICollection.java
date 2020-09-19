package dynamite.docdb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Document instance AST node for collections.
 */
public final class DICollection extends DIAstNode {

    public final String name;
    public final List<DIDocument> documents;
    // set representation for equality comparison
    private final Set<DIDocument> documentSet;

    private String canonicalName = null;

    public DICollection(String name, final List<DIDocument> documents) {
        Objects.requireNonNull(documents, "Documents cannot be null");
        this.name = name;
        this.documents = Collections.unmodifiableList(documents);
        this.documentSet = new HashSet<>(documents);
    }

    public void setCanonicalName(String canonicalName) {
        if (this.canonicalName != null) {
            throw new IllegalStateException(String.format("Canonical name %s is immutable", this.canonicalName));
        }
        this.canonicalName = canonicalName;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    @Override
    public <T> T accept(IDIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("Collection(%s,\n%s)", name, documents.stream()
                .map(DIDocument::toString)
                .collect(Collectors.joining("\n")));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, documentSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof DICollection))
            return false;
        DICollection o = (DICollection) obj;
        return Objects.equals(name, o.name) && Objects.equals(documentSet, o.documentSet);
    }
}

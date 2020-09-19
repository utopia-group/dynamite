package dynamite.docdb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.core.ISchema;

/**
 * Data structure for document database schemas.
 */
public final class DocumentSchema implements ISchema {

    public final List<DSCollection> collections;
    // set representation for equality comparison
    private final Set<DSCollection> collectionSet;
    // collection name -> collection
    private final Map<String, DSCollection> nameToCollectionMap;

    public DocumentSchema(final List<DSCollection> collections) {
        Objects.requireNonNull(collections, "Collections cannot be null");
        this.collections = Collections.unmodifiableList(collections);
        this.collectionSet = new HashSet<>(collections);
        this.nameToCollectionMap = buildNameToDocMap(collections);
    }

    public boolean hasCollection(String collectionName) {
        return nameToCollectionMap.containsKey(collectionName);
    }

    public DSCollection getCollectionByName(String collectionName) {
        assert nameToCollectionMap.containsKey(collectionName) : collectionName;
        return nameToCollectionMap.get(collectionName);
    }

    @Override
    public DocumentSchema toDocumentSchema() {
        return this;
    }

    public <T> T accept(IDocSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("DocSchema(\n%s)", collections.stream()
                .map(DSCollection::toString)
                .collect(Collectors.joining("\n")));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(collectionSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DocumentSchema)) return false;
        DocumentSchema o = (DocumentSchema) obj;
        return Objects.equals(collectionSet, o.collectionSet);
    }

    private static Map<String, DSCollection> buildNameToDocMap(List<DSCollection> collections) {
        return collections.stream().collect(Collectors.toMap(c -> c.name, c -> c));
    }

}

package dynamite.util;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dynamite.core.IInstance;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.reldb.ast.RITable;
import dynamite.reldb.ast.RelationalInstance;

public final class InstanceUtils {

    /**
     * Merge two collections in document instances. The new collection contains all distinct documents
     * in the original collections.
     *
     * @param lhs one collection
     * @param rhs another collection
     * @return the merged collection
     */
    public static DICollection mergeCollection(DICollection lhs, DICollection rhs) {
        if (!lhs.getCanonicalName().equals(rhs.getCanonicalName())) {
            throw new IllegalStateException("Two collections have different canonical names");
        }
        List<DIDocument> documents = Stream.concat(lhs.documents.stream(), rhs.documents.stream())
                .distinct()
                .collect(Collectors.toList());
        DICollection collection = new DICollection(lhs.name, documents);
        collection.setCanonicalName(lhs.getCanonicalName());
        return collection;
    }

    /**
     * Compute the size of a given database instance.
     *
     * @param instance the specified database instance
     * @return the size of the instance
     */
    public static int computeInstanceSize(IInstance instance) {
        if (instance instanceof RelationalInstance) {
            return computeRelInstSize((RelationalInstance) instance);
        } else if (instance instanceof DocumentInstance) {
            return computeDocInstSize((DocumentInstance) instance);
        } else if (instance instanceof GraphInstance) {
            return computeGraphInstSize((GraphInstance) instance);
        } else {
            throw new RuntimeException("Unknown type of instance");
        }
    }

    // the size of a relational instance is the size of its largest table
    private static int computeRelInstSize(RelationalInstance instance) {
        return instance.tables.stream()
                .map(RITable::getSize)
                .max(Comparator.comparing(Integer::valueOf))
                .orElseThrow(NoSuchElementException::new);
    }

    // the size of a document instance is the size of its largest collection
    private static int computeDocInstSize(DocumentInstance instance) {
        return instance.collections.stream()
                .map(collection -> collection.documents.size())
                .max(Comparator.comparing(Integer::valueOf))
                .orElseThrow(NoSuchElementException::new);
    }

    // the size of a graph instance is the number of edges
    private static int computeGraphInstSize(GraphInstance instance) {
        return instance.edges.size();
    }

}

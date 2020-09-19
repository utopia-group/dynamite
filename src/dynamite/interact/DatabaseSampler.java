package dynamite.interact;

import java.util.BitSet;

import dynamite.core.IInstance;
import dynamite.docdb.DocInstSamplingVisitor;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.graphdb.GraphInstSamplingVisitor;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.reldb.RelInstSamplingVisitor;
import dynamite.reldb.ast.RelationalInstance;

public final class DatabaseSampler {

    /**
     * Maximum number of documents for each nested collection.
     */
    public static final int MAX_DOCUMENT_COUNT = 2;

    /**
     * Sample a certain number of records from a database instance.
     *
     * @param instance the database instance
     * @param bitSet   the indices to sample
     * @return the sampled database instance
     */
    public static IInstance sample(IInstance instance, BitSet bitSet) {
        if (instance instanceof DocumentInstance) {
            return sampleDocDB((DocumentInstance) instance, bitSet);
        } else if (instance instanceof RelationalInstance) {
            return sampleRelDB((RelationalInstance) instance, bitSet);
        } else if (instance instanceof GraphInstance) {
            return sampleGraphDB((GraphInstance) instance, bitSet);
        } else {
            throw new RuntimeException("Unknown type of instance");
        }
    }

    /**
     * Extract a slice of document instance. For each nested collection, keep
     * at most {@code MAX_DOCUMENT_COUNT} documents.
     *
     * @param instance the document instance
     * @param bitSet   the indices of documents in top-level collections
     * @return the sliced document instance
     */
    public static DocumentInstance sampleDocDB(DocumentInstance instance, BitSet bitSet) {
        return instance.accept(new DocInstSamplingVisitor(bitSet, MAX_DOCUMENT_COUNT));
    }

    /**
     * Extract a slice of relational instance.
     *
     * @param instance the relational instance
     * @param bitSet   the indices of rows to sample
     * @return the sliced relational instance
     */
    public static RelationalInstance sampleRelDB(RelationalInstance instance, BitSet bitSet) {
        return instance.accept(new RelInstSamplingVisitor(bitSet));
    }

    /**
     * Extract a slice of graph instance by selecting a subset of vertices and all edges
     * that connect to any vertex in the subset.
     *
     * @param instance the graph instance
     * @param bitSet   the indices of vertices to sample
     * @return the sliced graph instance
     */
    public static GraphInstance sampleGraphDB(GraphInstance instance, BitSet bitSet) {
        return instance.accept(new GraphInstSamplingVisitor(bitSet));
    }

}

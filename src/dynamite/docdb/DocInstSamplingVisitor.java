package dynamite.docdb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dynamite.docdb.ast.DIAstNode;
import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.IDIAstVisitor;
import dynamite.docdb.ast.IDocInstVisitor;

/**
 * A document instance visitor for sampling the instance.
 * <br>
 * This visitor does not modify the instance AST.
 */
public final class DocInstSamplingVisitor implements
        IDocInstVisitor<DocumentInstance>,
        IDIAstVisitor<DIAstNode> {

    // indices of the documents to sample in top-level collections
    private final BitSet indicesBitSet;
    // maximum number of documents for nested collections
    private final int maxDocCount;
    // current nesting level
    private int currNestingLevel = -1;

    public DocInstSamplingVisitor(BitSet indicesBitSet, int maxDocCount) {
        Objects.requireNonNull(indicesBitSet);
        if (maxDocCount <= 0) throw new IllegalArgumentException("Maximum number of documents should be positive");
        this.indicesBitSet = indicesBitSet;
        this.maxDocCount = maxDocCount;
    }

    @Override
    public DICollection visit(DICollection collection) {
        ++currNestingLevel;
        List<DIDocument> documents = new ArrayList<>();
        if (currNestingLevel > 0) { // nested collection
            int endIndex = Math.min(collection.documents.size(), maxDocCount);
            for (int i = 0; i < endIndex; ++i) {
                documents.add(visit(collection.documents.get(i)));
            }
        } else { // top-level collection
            for (int i = 0; i < collection.documents.size(); ++i) {
                if (indicesBitSet.get(i)) {
                    documents.add(visit(collection.documents.get(i)));
                }
            }
        }
        --currNestingLevel;
        DICollection newCollection = new DICollection(collection.name, documents);
        newCollection.setCanonicalName(collection.getCanonicalName());
        return newCollection;
    }

    @Override
    public DIDocument visit(DIDocument document) {
        return new DIDocument(document.attributes.stream()
                .map(attr -> attr.accept(this))
                .map(DIAttrValue.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public DIAtomicAttrValue visit(DIAtomicAttrValue atomicAttrValue) {
        return atomicAttrValue;
    }

    @Override
    public DICollectionAttrValue visit(DICollectionAttrValue collectionAttrValue) {
        return new DICollectionAttrValue(visit(collectionAttrValue.collection));
    }

    @Override
    public DocumentInstance visit(DocumentInstance instance) {
        return new DocumentInstance(instance.collections.stream()
                .map(collection -> visit(collection))
                .collect(Collectors.toList()));
    }

}

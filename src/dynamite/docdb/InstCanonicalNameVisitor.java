package dynamite.docdb;

import java.util.Stack;

import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.IDIAstVisitor;
import dynamite.docdb.ast.IDocInstVisitor;

/**
 * Document instance visitor for setting document and attribute canonical names.
 * This visitor modifies the {@code canonicalName} of the instance AST.
 */
public final class InstCanonicalNameVisitor implements
        IDocInstVisitor<Void>,
        IDIAstVisitor<Void> {

    // delimiter used for canonical names
    public static final String DELIMITER = "?";
    // current prefix for canonical names
    private Stack<String> nameStack = new Stack<>();

    @Override
    public Void visit(DICollection collection) {
        String canonicalName = nameStack.isEmpty()
                ? collection.name
                : nameStack.peek() + DELIMITER + collection.name;
        collection.setCanonicalName(canonicalName);
        nameStack.push(canonicalName);
        collection.documents.forEach(document -> document.accept(this));
        nameStack.pop();
        return null;
    }

    @Override
    public Void visit(DIDocument document) {
        document.attributes.forEach(attr -> attr.accept(this));
        return null;
    }

    @Override
    public Void visit(DIAtomicAttrValue atomicAttrValue) {
        String canonicalName = nameStack.peek() + DELIMITER + atomicAttrValue.name;
        atomicAttrValue.setCanonicalName(canonicalName);
        return null;
    }

    @Override
    public Void visit(DICollectionAttrValue collectionAttrValue) {
        collectionAttrValue.collection.accept(this);
        return null;
    }

    @Override
    public Void visit(DocumentInstance instance) {
        for (DICollection collection : instance.collections) {
            collection.accept(this);
        }
        return null;
    }

}

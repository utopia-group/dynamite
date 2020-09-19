package dynamite.docdb;

import java.util.Stack;

import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.docdb.ast.IDSAstVisitor;
import dynamite.docdb.ast.IDocSchemaVisitor;

/**
 * Document schema visitor for setting document and attribute canonical names.
 * This visitor modifies the {@code canonicalName} of the schema AST.
 */
public final class SchemaCanonicalNameVisitor implements
        IDocSchemaVisitor<Void>,
        IDSAstVisitor<Void> {

    // delimiter used for canonical names
    public static final String DELIMITER = "?";
    // current prefix for canonical names
    private Stack<String> nameStack = new Stack<>();

    @Override
    public Void visit(DSCollection collection) {
        String canonicalName = nameStack.isEmpty()
                ? collection.name
                : nameStack.peek() + DELIMITER + collection.name;
        collection.setCanonicalName(canonicalName);
        nameStack.push(canonicalName);
        collection.document.accept(this);
        nameStack.pop();
        return null;
    }

    @Override
    public Void visit(DSDocument document) {
        document.attributes.forEach(attr -> attr.accept(this));
        return null;
    }

    @Override
    public Void visit(DSAtomicAttr atomicAttr) {
        String canonicalName = nameStack.peek() + DELIMITER + atomicAttr.name;
        atomicAttr.setCanonicalName(canonicalName);
        return null;
    }

    @Override
    public Void visit(DSCollectionAttr collectionAttr) {
        collectionAttr.collection.accept(this);
        return null;
    }

    @Override
    public Void visit(DocumentSchema schema) {
        for (DSCollection collection : schema.collections) {
            collection.accept(this);
        }
        return null;
    }

}

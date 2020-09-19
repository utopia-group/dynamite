package dynamite.docdb;

import java.util.stream.Collectors;

import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIFloatValue;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DINullValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DSAstNode;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.docdb.ast.IDIAstVisitor;
import dynamite.docdb.ast.IDIValueVisitor;
import dynamite.docdb.ast.IDocInstVisitor;

/**
 * Visitor for extracting document schemas for document instances.
 * This visitor does not modify the instance AST.
 */
public final class SchemaExtractionVisitor implements
        IDocInstVisitor<DocumentSchema>,
        IDIAstVisitor<DSAstNode>,
        IDIValueVisitor<AttrType> {

    @Override
    public AttrType visit(DIIntValue value) {
        return AttrType.INT;
    }

    @Override
    public AttrType visit(DIStrValue value) {
        return AttrType.STRING;
    }

    @Override
    public AttrType visit(DIFloatValue value) {
        return AttrType.FLOAT;
    }

    @Override
    public AttrType visit(DINullValue value) {
        // FIXME: this assumes that `null` values only appear in attributes of type string
        return AttrType.STRING;
    }

    @Override
    public DSCollection visit(DICollection collection) {
        if (collection.documents.isEmpty()) {
            throw new IllegalArgumentException("Empty collection: " + collection.getCanonicalName());
        }
        DIDocument document = collection.documents.iterator().next();
        return new DSCollection(collection.name, (DSDocument) document.accept(this));
    }

    @Override
    public DSDocument visit(DIDocument document) {
        return new DSDocument(document.attributes.stream()
                .map(attribute -> (DSAttribute) attribute.accept(this))
                .collect(Collectors.toList()));
    }

    @Override
    public DSAtomicAttr visit(DIAtomicAttrValue atomicAttrValue) {
        AttrType type = atomicAttrValue.value.accept(this);
        return new DSAtomicAttr(atomicAttrValue.name, type);
    }

    @Override
    public DSCollectionAttr visit(DICollectionAttrValue collectionAttrValue) {
        return new DSCollectionAttr((DSCollection) collectionAttrValue.collection.accept(this));
    }

    @Override
    public DocumentSchema visit(DocumentInstance instance) {
        return new DocumentSchema(instance.collections.stream()
                .map(collection -> (DSCollection) collection.accept(this))
                .collect(Collectors.toList()));
    }

}

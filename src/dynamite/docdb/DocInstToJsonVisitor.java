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
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.IDIAstVisitor;
import dynamite.docdb.ast.IDIValueVisitor;
import dynamite.docdb.ast.IDocInstVisitor;

/**
 * Visitor for generating JSON string for document instance.
 * <br>
 * This visitor does not modify the instance AST.
 */
public final class DocInstToJsonVisitor implements
        IDocInstVisitor<String>,
        IDIAstVisitor<String>,
        IDIValueVisitor<String> {

    @Override
    public String visit(DIIntValue value) {
        return value.toString();
    }

    @Override
    public String visit(DIStrValue value) {
        return value.toString();
    }

    @Override
    public String visit(DIFloatValue value) {
        return value.toString();
    }

    @Override
    public String visit(DINullValue value) {
        return value.toString();
    }

    @Override
    public String visit(DICollection collection) {
        StringBuilder builder = new StringBuilder();
        builder.append(genAttrJsonName(collection.name));
        builder.append(":[");
        builder.append(collection.documents.stream()
                .map(doc -> doc.accept(this))
                .collect(Collectors.joining(",")));
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String visit(DIDocument document) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(document.attributes.stream()
                .map(attr -> attr.accept(this))
                .collect(Collectors.joining(",")));
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String visit(DIAtomicAttrValue atomicAttrValue) {
        StringBuilder builder = new StringBuilder();
        builder.append(genAttrJsonName(atomicAttrValue.getAttributeName()));
        builder.append(":");
        builder.append(atomicAttrValue.value.accept(this));
        return builder.toString();
    }

    @Override
    public String visit(DICollectionAttrValue collectionAttrValue) {
        return collectionAttrValue.collection.accept(this);
    }

    @Override
    public String visit(DocumentInstance instance) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(instance.collections.stream()
                .map(collection -> collection.accept(this))
                .collect(Collectors.joining(",")));
        builder.append("}");
        return builder.toString();
    }

    private String genAttrJsonName(String attrName) {
        return "\"" + attrName + "\"";
    }

}

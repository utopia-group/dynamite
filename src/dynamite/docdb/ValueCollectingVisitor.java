package dynamite.docdb;

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
import dynamite.util.SetMultiMap;

/**
 * A document instance visitor that collects the value set for all atomic attributes.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class ValueCollectingVisitor implements
        IDocInstVisitor<SetMultiMap<String, Object>>,
        IDIAstVisitor<SetMultiMap<String, Object>>,
        IDIValueVisitor<Object> {

    public static final String NULL_LITERAL = "null";

    @Override
    public Integer visit(DIIntValue value) {
        return Integer.valueOf(value.value);
    }

    @Override
    public String visit(DIStrValue value) {
        return value.value;
    }

    @Override
    public String visit(DIFloatValue value) {
        return value.valueString;
    }

    @Override
    public String visit(DINullValue value) {
        return NULL_LITERAL;
    }

    @Override
    public SetMultiMap<String, Object> visit(DICollection collection) {
        return collection.documents.stream()
                .map(document -> document.accept(this))
                .reduce(new SetMultiMap<>(), SetMultiMap::unionSetMultiMaps);
    }

    @Override
    public SetMultiMap<String, Object> visit(DIDocument document) {
        return document.attributes.stream()
                .map(attr -> attr.accept(this))
                .reduce(new SetMultiMap<>(), SetMultiMap::unionSetMultiMaps);
    }

    @Override
    public SetMultiMap<String, Object> visit(DIAtomicAttrValue atomicAttrValue) {
        SetMultiMap<String, Object> map = new SetMultiMap<>();
        map.put(atomicAttrValue.getCanonicalName(), atomicAttrValue.value.accept(this));
        return map;
    }

    @Override
    public SetMultiMap<String, Object> visit(DICollectionAttrValue collectionAttrValue) {
        return collectionAttrValue.collection.accept(this);
    }

    @Override
    public SetMultiMap<String, Object> visit(DocumentInstance instance) {
        return instance.collections.stream()
                .map(collection -> collection.accept(this))
                .reduce(new SetMultiMap<>(), SetMultiMap::unionSetMultiMaps);
    }

}

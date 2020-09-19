package dynamite.docdb;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.docdb.ast.IDSAstVisitor;
import dynamite.docdb.ast.IDocSchemaVisitor;

/**
 * A document schema visitor that generates the map from canonical names to collections,
 * including nested ones.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class NameMapGenVisitor implements
        IDSAstVisitor<Map<String, DSCollection>>,
        IDocSchemaVisitor<Map<String, DSCollection>> {

    @Override
    public Map<String, DSCollection> visit(DSCollection collection) {
        Map<String, DSCollection> map = new HashMap<>();
        map.put(collection.getCanonicalName(), collection);
        map.putAll(collection.document.accept(this));
        return map;
    }

    @Override
    public Map<String, DSCollection> visit(DSDocument document) {
        return document.attributes.stream()
                .map(attr -> attr.accept(this))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, DSCollection> visit(DSAtomicAttr atomicAttr) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, DSCollection> visit(DSCollectionAttr collectionAttr) {
        return collectionAttr.collection.accept(this);
    }

    @Override
    public Map<String, DSCollection> visit(DocumentSchema schema) {
        return schema.collections.stream()
                .map(collection -> collection.accept(this))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}

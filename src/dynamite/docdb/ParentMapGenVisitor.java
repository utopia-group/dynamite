package dynamite.docdb;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.docdb.ast.IDSAstVisitor;
import dynamite.docdb.ast.IDocSchemaVisitor;

/**
 * A document schema visitor that generates the parent map.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class ParentMapGenVisitor implements
        IDSAstVisitor<Map<String, String>>,
        IDocSchemaVisitor<Map<String, String>> {

    @Override
    public Map<String, String> visit(DSCollection collection) {
        Map<String, String> map = new HashMap<>();
        for (DSAttribute attr : collection.document.attributes) {
            if (attr instanceof DSCollectionAttr) {
                DSCollection child = ((DSCollectionAttr) attr).collection;
                assert !map.containsKey(child.getCanonicalName()) : child.getCanonicalName();
                map.put(child.getCanonicalName(), collection.getCanonicalName());
            }
        }
        map.putAll(collection.document.accept(this));
        return map;
    }

    @Override
    public Map<String, String> visit(DSDocument document) {
        return document.attributes.stream()
                .map(attr -> attr.accept(this))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, String> visit(DSAtomicAttr atomicAttr) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> visit(DSCollectionAttr collectionAttr) {
        return collectionAttr.collection.accept(this);
    }

    @Override
    public Map<String, String> visit(DocumentSchema schema) {
        return schema.collections.stream()
                .map(collection -> collection.accept(this))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}

package dynamite.docdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.TypeDeclaration;
import dynamite.datalog.ast.TypedAttribute;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.docdb.ast.IDSAstVisitor;
import dynamite.docdb.ast.IDocSchemaVisitor;

/**
 * Visitor for translating document schemas to Datalog relation declarations.
 * This visitor does not modify the schema AST.
 */
public final class RelationDeclGenVisitor implements
        IDocSchemaVisitor<List<RelationDeclaration>>, IDSAstVisitor<List<TypedAttribute>> {

    private static final String TYPE_ATTR_INT = TypeDeclaration.TYPE_ATTR_INT;
    private static final String TYPE_ATTR_STR = TypeDeclaration.TYPE_ATTR_STR;
    private static final String TYPE_RELATION = TypeDeclaration.TYPE_RELATION;

    // list to store translated Datalog relation declarations
    private List<RelationDeclaration> declarations = new ArrayList<>();
    // current nesting level
    private int currNestingLevel;

    @Override
    public List<TypedAttribute> visit(DSCollection collection) {
        ++currNestingLevel;
        List<TypedAttribute> typedAttrs = collection.document.accept(this);
        if (currNestingLevel > 0) {
            typedAttrs.add(0, new TypedAttribute("__id", TYPE_RELATION));
        }
        --currNestingLevel;
        declarations.add(new RelationDeclaration(collection.getCanonicalName(), typedAttrs));
        // do not return because the declarations are stored in the global list
        return Collections.emptyList();
    }

    @Override
    public List<TypedAttribute> visit(DSDocument document) {
        return document.attributes.stream()
                .flatMap(attr -> attr.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<TypedAttribute> visit(DSAtomicAttr atomicAttr) {
        String type;
        switch (atomicAttr.type) {
        case INT:
            type = TYPE_ATTR_INT;
            break;
        case STRING:
        case FLOAT:
            type = TYPE_ATTR_STR;
            break;
        default:
            throw new RuntimeException(String.format("Unrecognized attribute type %s", atomicAttr.type.name()));
        }
        return Collections.singletonList(new TypedAttribute(atomicAttr.name, type));
    }

    @Override
    public List<TypedAttribute> visit(DSCollectionAttr collectionAttr) {
        collectionAttr.collection.accept(this);
        return Collections.singletonList(new TypedAttribute(collectionAttr.collection.name, TYPE_RELATION));
    }

    @Override
    public List<RelationDeclaration> visit(DocumentSchema schema) {
        for (DSCollection collection : schema.collections) {
            currNestingLevel = -1;
            collection.accept(this);
            assert currNestingLevel == -1;
        }
        return declarations;
    }

}

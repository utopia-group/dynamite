package dynamite.graphdb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.docdb.SchemaCanonicalNameVisitor;
import dynamite.docdb.ast.DSAstNode;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.graphdb.ast.GSAtomicProp;
import dynamite.graphdb.ast.GSAtomicProp.PropType;
import dynamite.graphdb.ast.GSCollection;
import dynamite.graphdb.ast.GSCollectionProp;
import dynamite.graphdb.ast.GSEdge;
import dynamite.graphdb.ast.GSPropList;
import dynamite.graphdb.ast.GSVertex;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.graphdb.ast.IGSAstVisitor;
import dynamite.graphdb.ast.IGraphSchemaVisitor;

/**
 * A graph schema visitor that builds the corresponding document schema.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class GraphToDocSchemaVisitor implements
        IGraphSchemaVisitor<DocumentSchema>,
        IGSAstVisitor<DSAstNode> {

    private static final String ID_KEY_NAME = "_id";
    private static final String START_KEY_NAME = "_start";
    private static final String END_KEY_NAME = "_end";
    private List<DSCollection> collections = new ArrayList<>();

    @Override
    public DSAstNode visit(GSVertex vertex) {
        DSDocument propDoc = visit(vertex.propList);
        List<DSAttribute> attributes = new ArrayList<>();
        attributes.add(new DSAtomicAttr(ID_KEY_NAME, AttrType.STRING));
        attributes.addAll(propDoc.attributes);
        DSDocument document = new DSDocument(attributes);
        for (String label : vertex.labels) {
            collections.add(new DSCollection(label, document));
        }
        // return value is meaningless
        return null;
    }

    @Override
    public DSAstNode visit(GSEdge edge) {
        DSDocument propDoc = visit(edge.propList);
        List<DSAttribute> attributes = new ArrayList<>();
        attributes.add(new DSAtomicAttr(START_KEY_NAME, AttrType.STRING));
        attributes.add(new DSAtomicAttr(END_KEY_NAME, AttrType.STRING));
        attributes.addAll(propDoc.attributes);
        DSDocument document = new DSDocument(attributes);
        collections.add(new DSCollection(edge.label, document));
        // return value is meaningless
        return null;
    }

    @Override
    public DSCollection visit(GSCollection collection) {
        return new DSCollection(collection.name, visit(collection.propList));
    }

    @Override
    public DSDocument visit(GSPropList propList) {
        return new DSDocument(propList.properties.stream()
                .map(property -> property.accept(this))
                .map(DSAttribute.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public DSAtomicAttr visit(GSAtomicProp atomicProp) {
        return new DSAtomicAttr(atomicProp.name, propTypeToAttrType(atomicProp.type));
    }

    @Override
    public DSCollectionAttr visit(GSCollectionProp collectionProp) {
        return new DSCollectionAttr(visit(collectionProp.collection));
    }

    @Override
    public DocumentSchema visit(GraphSchema schema) {
        schema.vertices.forEach(vertex -> vertex.accept(this));
        schema.edges.forEach(edge -> edge.accept(this));
        DocumentSchema docSchema = new DocumentSchema(collections);
        docSchema.accept(new SchemaCanonicalNameVisitor());
        return docSchema;
    }

    private static AttrType propTypeToAttrType(PropType type) {
        switch (type) {
        case INT:
            return AttrType.INT;
        case STRING:
            return AttrType.STRING;
        case FLOAT:
            return AttrType.FLOAT;
        default:
            throw new IllegalArgumentException("Unknown property type: " + type.name());
        }
    }

}

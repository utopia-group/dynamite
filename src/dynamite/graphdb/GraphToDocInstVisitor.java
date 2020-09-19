package dynamite.graphdb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.docdb.InstCanonicalNameVisitor;
import dynamite.docdb.ast.DIAstNode;
import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIFloatValue;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DINullValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DIValue;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.graphdb.ast.GIAtomicProp;
import dynamite.graphdb.ast.GICollection;
import dynamite.graphdb.ast.GICollectionProp;
import dynamite.graphdb.ast.GIEdge;
import dynamite.graphdb.ast.GIFloatValue;
import dynamite.graphdb.ast.GIIntValue;
import dynamite.graphdb.ast.GINullValue;
import dynamite.graphdb.ast.GIPropList;
import dynamite.graphdb.ast.GIStrValue;
import dynamite.graphdb.ast.GIVertex;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.IGIAstVisitor;
import dynamite.graphdb.ast.IGIValueVisitor;
import dynamite.graphdb.ast.IGraphInstVisitor;
import dynamite.util.ListMultiMap;

/**
 * A graph instance visitor that builds the corresponding document instance.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class GraphToDocInstVisitor implements
        IGraphInstVisitor<DocumentInstance>,
        IGIAstVisitor<DIAstNode>,
        IGIValueVisitor<DIValue> {

    private static final String ID_KEY_NAME = "_id";
    private static final String START_KEY_NAME = "_start";
    private static final String END_KEY_NAME = "_end";
    // label -> list of documents
    private ListMultiMap<String, DIDocument> labelToDocs = new ListMultiMap<>();

    @Override
    public DIIntValue visit(GIIntValue value) {
        return new DIIntValue(value.value);
    }

    @Override
    public DIStrValue visit(GIStrValue value) {
        return new DIStrValue(value.value);
    }

    @Override
    public DIFloatValue visit(GIFloatValue value) {
        return new DIFloatValue(value.valueString);
    }

    @Override
    public DINullValue visit(GINullValue value) {
        return DINullValue.getInstance();
    }

    @Override
    public DIAstNode visit(GIVertex vertex) {
        // NOTE: create a deep copy of document (property list) for each label
        for (String label : vertex.labels) {
            DIDocument propDoc = visit(vertex.propList);
            List<DIAttrValue> attributes = new ArrayList<>();
            attributes.add(new DIAtomicAttrValue(ID_KEY_NAME, new DIStrValue(vertex.id)));
            attributes.addAll(propDoc.attributes);
            DIDocument document = new DIDocument(attributes);
            labelToDocs.put(label, document);
        }
        // return value is meaningless
        return null;
    }

    @Override
    public DIAstNode visit(GIEdge edge) {
        DIDocument propDoc = visit(edge.propList);
        List<DIAttrValue> attributes = new ArrayList<>();
        attributes.add(new DIAtomicAttrValue(START_KEY_NAME, new DIStrValue(edge.start.id)));
        attributes.add(new DIAtomicAttrValue(END_KEY_NAME, new DIStrValue(edge.end.id)));
        attributes.addAll(propDoc.attributes);
        DIDocument document = new DIDocument(attributes);
        labelToDocs.put(edge.label, document);
        // return value is meaningless
        return null;
    }

    @Override
    public DICollection visit(GICollection collection) {
        List<DIDocument> documents = collection.propLists.stream()
                .map(propList -> visit(propList))
                .collect(Collectors.toList());
        return new DICollection(collection.name, documents);
    }

    @Override
    public DIDocument visit(GIPropList propList) {
        return new DIDocument(propList.properties.stream()
                .map(property -> property.accept(this))
                .map(DIAttrValue.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public DIAtomicAttrValue visit(GIAtomicProp atomicProp) {
        return new DIAtomicAttrValue(atomicProp.name, atomicProp.value.accept(this));
    }

    @Override
    public DICollectionAttrValue visit(GICollectionProp collectionProp) {
        return new DICollectionAttrValue(visit(collectionProp.collection));
    }

    @Override
    public DocumentInstance visit(GraphInstance instance) {
        instance.vertices.forEach(vertex -> vertex.accept(this));
        instance.edges.forEach(edge -> edge.accept(this));
        List<DICollection> collections = new ArrayList<>();
        for (String label : labelToDocs.keySet()) {
            collections.add(new DICollection(label, labelToDocs.get(label)));
        }
        DocumentInstance docInst = new DocumentInstance(collections);
        docInst.accept(new InstCanonicalNameVisitor());
        return docInst;
    }

}

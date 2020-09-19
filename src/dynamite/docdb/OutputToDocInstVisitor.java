package dynamite.docdb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.datalog.DatalogOutput;
import dynamite.datalog.RelationOutput;
import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIFloatValue;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DINullValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.docdb.ast.IDocSchemaVisitor;

/**
 * A document schema visitor that generates document instances from Datalog outputs.
 * <br>
 * This visitor does not modify the schema AST.
 */
public final class OutputToDocInstVisitor implements
        IDocSchemaVisitor<DocumentInstance> {

    public static final String NULL_LITERAL = "null";
    // output of the Datalog program
    private final DatalogOutput output;
    // current nesting level
    private int currNestingLevel = -1;

    public OutputToDocInstVisitor(DatalogOutput output) {
        this.output = output;
    }

    // top-level collections
    private DICollection visit(DSCollection collection) {
        enterCollection();
        RelationOutput relOutput = output.getRelationOutputByName(collection.getCanonicalName());
        List<DIDocument> docInsts = new ArrayList<>();
        for (List<String> row : relOutput.data) {
            docInsts.add(visit(collection.document, row));
        }
        exitCollection();
        return new DICollection(collection.name, docInsts);
    }

    // nested collections
    private DICollection visit(DSCollection collection, String value) {
        enterCollection();
        assert !isTopLevelCollection();
        RelationOutput relOutput = output.getRelationOutputByName(collection.getCanonicalName());
        List<DIDocument> docInsts = new ArrayList<>();
        for (List<String> row : relOutput.data) {
            if (row.get(0).equals(value)) { // check if `__id` equals the provided hash value
                docInsts.add(visit(collection.document, row));
            }
        }
        exitCollection();
        return new DICollection(collection.name, docInsts);
    }

    private DIDocument visit(DSDocument document, List<String> row) {
        assert isTopLevelCollection()
                ? document.attributes.size() == row.size()
                : document.attributes.size() == row.size() - 1; // the first value is `__id`
        List<DIAttrValue> attrInsts = new ArrayList<>();
        for (int i = 0; i < document.attributes.size(); ++i) {
            DSAttribute attr = document.attributes.get(i);
            String value = row.get(isTopLevelCollection() ? i : i + 1);
            if (attr instanceof DSAtomicAttr) {
                attrInsts.add(visit((DSAtomicAttr) attr, value));
            } else if (attr instanceof DSCollectionAttr) {
                attrInsts.add(visit((DSCollectionAttr) attr, value));
            } else {
                throw new RuntimeException("Unknown attribute type for " + attr);
            }
        }
        return new DIDocument(attrInsts);
    }

    private DIAtomicAttrValue visit(DSAtomicAttr atomicAttr, String value) {
        switch (atomicAttr.type) {
        case INT:
            return new DIAtomicAttrValue(atomicAttr.name, new DIIntValue(Integer.parseInt(value)));
        case STRING:
            return value.equals(NULL_LITERAL)
                    ? new DIAtomicAttrValue(atomicAttr.name, DINullValue.getInstance())
                    : new DIAtomicAttrValue(atomicAttr.name, new DIStrValue(value));
        case FLOAT:
            return new DIAtomicAttrValue(atomicAttr.name, new DIFloatValue(value));
        default:
            throw new RuntimeException("Unknown attribute type: " + atomicAttr.type.name());
        }
    }

    private DICollectionAttrValue visit(DSCollectionAttr collectionAttr, String value) {
        return new DICollectionAttrValue(visit(collectionAttr.collection, value));
    }

    @Override
    public DocumentInstance visit(DocumentSchema schema) {
        DocumentInstance instance = new DocumentInstance(schema.collections.stream()
                .map(collection -> visit(collection))
                .collect(Collectors.toList()));
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    private void enterCollection() {
        ++currNestingLevel;
    }

    private void exitCollection() {
        --currNestingLevel;
    }

    private boolean isTopLevelCollection() {
        return currNestingLevel == 0;
    }

}

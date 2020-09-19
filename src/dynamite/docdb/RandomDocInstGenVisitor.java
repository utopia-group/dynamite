package dynamite.docdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dynamite.docdb.ast.DIAstNode;
import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIFloatValue;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DIValue;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.docdb.ast.IDSAstVisitor;
import dynamite.docdb.ast.IDocSchemaVisitor;
import dynamite.exp.ForeignKey;
import dynamite.exp.IntegrityConstraint;
import dynamite.exp.Selection;
import dynamite.util.ListMultiMap;
import dynamite.util.RandomValueFactory;

/**
 * A document schema visitor that generates random document instances.
 * <br>
 * This visitor does not modify the schema AST.
 */
public final class RandomDocInstGenVisitor implements
        IDocSchemaVisitor<DocumentInstance>,
        IDSAstVisitor<DIAstNode> {

    // probability to use selected values
    public static final int PROBABILITY = 80;
    // probability to use primary key values for foreign keys
    public static final int UPDATE_PROBABILITY = 50;
    // maximum number of documents in nested collections
    public static final int MAX_NESTED_DOC_COUNT = 1;

    // random value factory
    private final RandomValueFactory randFactory;
    // size of the table
    private final int size;
    // integrity constraint
    private final IntegrityConstraint constraint;
    // current nesting level
    private int currNestingLevel = -1;
    // canonical name -> values used for primary or foreign keys
    private final ListMultiMap<String, DIValue> attrToValuesMap = new ListMultiMap<>();
    // foreign key names -> foreign keys
    private final Map<String, ForeignKey> foreignKeys = new HashMap<>();
    // primary key names referenced by foreign keys -> foreign keys
    private final Map<String, ForeignKey> primaryKeys = new HashMap<>();

    public RandomDocInstGenVisitor(int size, IntegrityConstraint constraint, long randomSeed) {
        this.size = size;
        this.constraint = constraint;
        this.randFactory = new RandomValueFactory(randomSeed);
        extractPKFKs(constraint);
    }

    @Override
    public DICollection visit(DSCollection collection) {
        enterCollection();
        List<DIDocument> documents = new ArrayList<>();
        int count = isTopLevelCollection() ? size : Math.min(size, MAX_NESTED_DOC_COUNT);
        for (int i = 0; i < count; ++i) {
            documents.add(visit(collection.document));
        }
        exitCollection();
        return new DICollection(collection.name, documents);
    }

    @Override
    public DIDocument visit(DSDocument document) {
        return new DIDocument(document.attributes.stream()
                .map(attr -> attr.accept(this))
                .map(DIAttrValue.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public DIAtomicAttrValue visit(DSAtomicAttr atomicAttr) {
        String name = atomicAttr.getCanonicalName();
        if (foreignKeys.containsKey(name)) {
            String pkName = foreignKeys.get(name).to;
            if (attrToValuesMap.containsKey(pkName) && toUpdate()) {
                List<DIValue> values = attrToValuesMap.get(pkName);
                int index = randFactory.mkBoundedInt(values.size());
                return new DIAtomicAttrValue(atomicAttr.name, values.get(index));
            } else {
                DIAtomicAttrValue atomicAttrValue = genNewAttrValue(atomicAttr);
                attrToValuesMap.put(name, atomicAttrValue.value);
                return atomicAttrValue;
            }
        }
        if (primaryKeys.containsKey(name)) {
            String fkName = primaryKeys.get(name).from;
            if (attrToValuesMap.containsKey(fkName) && toUpdate()) {
                List<DIValue> values = attrToValuesMap.get(fkName);
                int index = randFactory.mkBoundedInt(values.size());
                return new DIAtomicAttrValue(atomicAttr.name, values.get(index));
            } else {
                DIAtomicAttrValue atomicAttrValue = genNewAttrValue(atomicAttr);
                attrToValuesMap.put(name, atomicAttrValue.value);
                return atomicAttrValue;
            }
        }
        return genNewAttrValue(atomicAttr);
    }

    private DIAtomicAttrValue genNewAttrValue(DSAtomicAttr atomicAttr) {
        switch (atomicAttr.type) {
        case INT:
            return new DIAtomicAttrValue(atomicAttr.name, new DIIntValue(randFactory.mkInt()));
        case STRING:
            String canonicalName = atomicAttr.getCanonicalName();
            return constraint.isSelectionAttr(canonicalName) && toBeReplaced()
                    ? new DIAtomicAttrValue(atomicAttr.name, new DIStrValue(getRandomValue(constraint.getSelectionByAttr(canonicalName))))
                    : new DIAtomicAttrValue(atomicAttr.name, new DIStrValue(randFactory.mkString()));
        case FLOAT:
            return new DIAtomicAttrValue(atomicAttr.name, new DIFloatValue(randFactory.mkFloatString()));
        default:
            throw new IllegalStateException("Unknown attribute type: " + atomicAttr.type.name());
        }
    }

    @Override
    public DICollectionAttrValue visit(DSCollectionAttr collectionAttr) {
        return new DICollectionAttrValue(visit(collectionAttr.collection));
    }

    @Override
    public DocumentInstance visit(DocumentSchema schema) {
        DocumentInstance instance = new DocumentInstance(schema.collections.stream()
                .map(collection -> visit(collection))
                .collect(Collectors.toList()));
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    private void extractPKFKs(IntegrityConstraint constraint) {
        for (ForeignKey fk : constraint.foreignKeys) {
            // NOTE: assumed uniqueness
            if (foreignKeys.containsKey(fk.from)) throw new IllegalStateException(fk.toString());
            if (primaryKeys.containsKey(fk.to)) throw new IllegalStateException(fk.toString());
            foreignKeys.put(fk.from, fk);
            primaryKeys.put(fk.to, fk);
        }
    }

    private boolean toUpdate() {
        int num = randFactory.mkBoundedInt(100);
        return num < UPDATE_PROBABILITY;
    }

    private boolean toBeReplaced() {
        int num = randFactory.mkBoundedInt(100);
        return num < PROBABILITY;
    }

    private String getRandomValue(Selection selection) {
        int index = randFactory.mkBoundedInt(selection.values.size());
        return selection.values.get(index);
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

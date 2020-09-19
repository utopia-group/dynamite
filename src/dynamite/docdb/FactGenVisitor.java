package dynamite.docdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
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
 * Visitor for generating Datalog facts for document instance.
 * <br>
 * This visitor does not modify the instance AST.
 * <br>
 * Currently, it does not use collection attributes for computing the injective values.
 */
public final class FactGenVisitor implements
        IDocInstVisitor<List<DatalogFact>>,
        IDIAstVisitor<List<DatalogFact>>,
        IDIValueVisitor<StringLiteral> {

    // delimiter between different values
    public static final String DELIMITER = "#";
    // null literal
    public static final String NULL_LITERAL = "null";
    // collection stack
    private final Stack<DICollection> collectionStack = new Stack<>();
    // injective value stack
    private final Stack<String> injValueStack = new Stack<>();

    @Override
    public StringLiteral visit(DIIntValue value) {
        // NOTE: convert integers to strings to make concatenation easier
        return new StringLiteral(String.valueOf(value.value));
    }

    @Override
    public StringLiteral visit(DIStrValue value) {
        return new StringLiteral(value.value);
    }

    @Override
    public StringLiteral visit(DIFloatValue value) {
        return new StringLiteral(value.valueString);
    }

    @Override
    public StringLiteral visit(DINullValue value) {
        return new StringLiteral(NULL_LITERAL);
    }

    @Override
    public List<DatalogFact> visit(DICollection collection) {
        collectionStack.push(collection);
        List<DatalogFact> facts = collection.documents.stream()
                .flatMap(document -> document.accept(this).stream())
                .collect(Collectors.toList());
        collectionStack.pop();
        return facts;
    }

    @Override
    public List<DatalogFact> visit(DIDocument document) {
        List<DatalogFact> ret = new ArrayList<>();
        List<DatalogExpression> arguments = new ArrayList<>();
        if (!injValueStack.isEmpty()) {
            arguments.add(new StringLiteral(injValueStack.peek()));
        }
        String currInjectiveValue = computeInjectiveValue(document);
        for (DIAttrValue attrValue : document.attributes) {
            if (attrValue instanceof DIAtomicAttrValue) {
                DIAtomicAttrValue atomicAttrValue = (DIAtomicAttrValue) attrValue;
                arguments.add(atomicAttrValue.value.accept(this));
            } else if (attrValue instanceof DICollectionAttrValue) {
                arguments.add(new StringLiteral(currInjectiveValue));
                injValueStack.push(currInjectiveValue);
                ret.addAll(attrValue.accept(this));
                injValueStack.pop();
            } else {
                throw new RuntimeException("Unknown attr-value type: " + attrValue.getClass().toString());
            }
        }
        String relation = collectionStack.peek().getCanonicalName();
        ret.add(new DatalogFact(new RelationPredicate(relation, arguments)));
        return ret;
    }

    @Override
    public List<DatalogFact> visit(DIAtomicAttrValue atomicAttrValue) {
        return Collections.emptyList();
    }

    @Override
    public List<DatalogFact> visit(DICollectionAttrValue collectionAttrValue) {
        return collectionAttrValue.collection.accept(this);
    }

    @Override
    public List<DatalogFact> visit(DocumentInstance instance) {
        return instance.collections.stream()
                .flatMap(collection -> collection.accept(this).stream())
                .collect(Collectors.toList());
    }

    private String computeInjectiveValue(DIDocument document) {
        StringBuilder builder = new StringBuilder();
        builder.append(DELIMITER);
        if (!injValueStack.isEmpty()) {
            builder.append(injValueStack.peek()).append(DELIMITER);
        }
        for (DIAttrValue attrValue : document.attributes) {
            if (attrValue instanceof DIAtomicAttrValue) {
                DIAtomicAttrValue atomicAttrValue = (DIAtomicAttrValue) attrValue;
                StringLiteral strLiteral = atomicAttrValue.value.accept(this);
                builder.append(strLiteral.value);
                builder.append(DELIMITER);
            } else if (attrValue instanceof DICollectionAttrValue) {
                continue;
            } else {
                throw new RuntimeException("Unknown attr-value type: " + attrValue.getClass().toString());
            }
        }
        return builder.toString();
    }

}

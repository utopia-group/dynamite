package dynamite.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import dynamite.datalog.ast.BinaryPredicate;
import dynamite.datalog.ast.BinaryPredicate.Operator;
import dynamite.datalog.ast.ConcatFunction;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.IDSAstVisitor;

/**
 * A document schema visitor that generates heads of rules for Datalog sketches.
 * <br>
 * This visitor does not modify the AST nodes.
 */
final class SketchHeadGenerator implements
        IDSAstVisitor<List<DatalogExpression>> {

    // prefix of join variables in the head
    private static final String PREFIX = "v_";
    // delimiter of the injective values
    private static final String DELIMITER = "#";
    // current index of join variables
    private int currJoinIndex = -1;
    // collection stack that the visitor visits
    private Stack<DSCollection> collectionStack = new Stack<>();
    // relation predicates in the head
    private final List<RelationPredicate> heads = new ArrayList<>();
    // equality predicates for injective values
    private final List<DatalogPredicate> injectivePreds = new ArrayList<>();
    // atomic predicates that has been visited
    private final List<DSAtomicAttr> atomicAttributes = new ArrayList<>();
    // collection canonical name -> join variable index
    private final Map<String, Integer> collectionNameToIndex = new HashMap<>();

    /**
     * @return head predicates of the rule sketch.
     */
    public List<RelationPredicate> getHeads() {
        return heads;
    }

    /**
     * @return the equality predicates for the injective values in the head.
     */
    public List<DatalogPredicate> getInjectivePredicates() {
        return injectivePreds;
    }

    /**
     * @return all the atomic attributes that have been visited.
     */
    public List<DSAtomicAttr> getAtomicAttributes() {
        return atomicAttributes;
    }

    /**
     * The return value is meaningless. Please discard the return value.
     */
    @Override
    public List<DatalogExpression> visit(DSCollection collection) {
        collectionStack.push(collection);
        List<DatalogExpression> arguments = collection.document.accept(this);
        if (collectionStack.size() > 1) {
            arguments.add(0, new Identifier(getCollectionVarName(collection)));
        }
        heads.add(new RelationPredicate(collection.getCanonicalName(), arguments));
        collectionStack.pop();
        return Collections.emptyList();
    }

    @Override
    public List<DatalogExpression> visit(DSDocument document) {
        return document.attributes.stream()
                .flatMap(attr -> attr.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<DatalogExpression> visit(DSAtomicAttr atomicAttr) {
        atomicAttributes.add(atomicAttr);
        return Collections.singletonList(new Identifier(getAttrVarName(atomicAttr)));
    }

    @Override
    public List<DatalogExpression> visit(DSCollectionAttr collectionAttr) {
        assert !collectionNameToIndex.containsKey(collectionAttr.getCanonicalName());
        ++currJoinIndex;
        collectionNameToIndex.put(collectionAttr.getCanonicalName(), currJoinIndex);
        String identName = getCollectionVarName(collectionAttr.collection);
        injectivePreds.add(buildInjectivePred(collectionStack.peek(), identName));
        collectionAttr.collection.accept(this);
        return Collections.singletonList(new Identifier(identName));
    }

    // NOTE: No need to convert integers to strings because we use strings
    // for all the attributes in the Datalog program.
    private BinaryPredicate buildInjectivePred(DSCollection collection, String identName) {
        List<DSAtomicAttr> atomicAttrs = collection.document.attributes.stream()
                .filter(DSAtomicAttr.class::isInstance)
                .map(DSAtomicAttr.class::cast)
                .collect(Collectors.toList());
        if (atomicAttrs.size() == 0) {
            throw new IllegalArgumentException("No atomic attrs in collection: " + collection.getCanonicalName());
        }
        Identifier lhs = new Identifier(identName);
        DatalogExpression rhs = new StringLiteral(DELIMITER);
        for (int i = atomicAttrs.size() - 1; i >= 0; --i) {
            rhs = new ConcatFunction(new Identifier(getAttrVarName(atomicAttrs.get(i))), rhs);
            rhs = new ConcatFunction(new StringLiteral(DELIMITER), rhs);
        }
        // add the id field for nested collections
        if (collectionStack.size() > 1) {
            rhs = new ConcatFunction(new Identifier(getCollectionVarName(collection)), rhs);
            rhs = new ConcatFunction(new StringLiteral(DELIMITER), rhs);
        }
        return new BinaryPredicate(lhs, Operator.EQ, rhs);
    }

    private String getAttrVarName(DSAtomicAttr attr) {
        return PREFIX + attr.getCanonicalName();
    }

    private String getCollectionVarName(DSCollection collection) {
        int index = collectionNameToIndex.get(collection.getCanonicalName());
        return "_" + PREFIX + index;
    }

}

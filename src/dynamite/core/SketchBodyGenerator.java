package dynamite.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import dynamite.Config;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.ExpressionHole;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.PlaceHolder;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.docdb.NameMapGenVisitor;
import dynamite.docdb.ParentMapGenVisitor;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.docdb.ast.IDSAstVisitor;

/**
 * A document schema visitor that generates rule bodies for Datalog program sketches.
 * <br>
 * This visitor does not modify the AST nodes.
 */
final class SketchBodyGenerator implements
        IDSAstVisitor<List<DatalogExpression>> {

    // delimiter for canonical names
    private static final char DELIMITER = '?';
    // prefix of hole names
    private static final String PREFIX_HOLE = "h";
    // prefix of variable names
    private static final String PREFIX_VAR = "v_";

    // value correspondence
    private final ValueCorr valueCorr;
    // top level collection name -> collection definition
    private final Map<String, DSCollection> nameToCollection;
    // collection name -> parent collection name; doesn't include top-level collections in the key set
    private final Map<String, String> parentMap;
    // target (atomic) attributes that the body should cover
    private final List<DSAtomicAttr> tgtAttrs;
    // set of canonical target names that the body should cover
    private final Set<String> tgtAttrNames;
    // maximum number of copies for each direct collection
    private final int maxCopyNum;
    // index of current hole
    private int holeIndex = -1;
    // index of current join variables
    private int varIndex = -1;
    // collection canonical name -> direct copy number
    private final Map<String, Integer> collectionToCopyNum = new HashMap<>();

    public SketchBodyGenerator(ValueCorr valueCorr, DocumentSchema srcSchema, List<DSAtomicAttr> tgtAttrs) {
        this(valueCorr, srcSchema, tgtAttrs, Config.MAXIMUM_COPY_NUM);
    }

    public SketchBodyGenerator(ValueCorr valueCorr, DocumentSchema srcSchema, List<DSAtomicAttr> tgtAttrs, int maxCopyNum) {
        Objects.requireNonNull(valueCorr);
        Objects.requireNonNull(srcSchema);
        Objects.requireNonNull(tgtAttrs);
        if (maxCopyNum < 1) throw new IllegalStateException();
        this.valueCorr = valueCorr;
        this.nameToCollection = srcSchema.accept(new NameMapGenVisitor());
        this.parentMap = srcSchema.accept(new ParentMapGenVisitor());
        this.tgtAttrs = tgtAttrs;
        this.tgtAttrNames = computeAttributeNames(tgtAttrs);
        this.maxCopyNum = maxCopyNum;
    }

    /**
     * Generate the body predicates of the rule sketch.
     *
     * @return body predicates
     */
    public List<DatalogPredicate> genBodies() {
        genPredsWithoutDomain();
        Map<String, Long> collectionNameToCopyNum = computeCopyNum(predsWithoutDomain);
        List<DatalogPredicate> bodyPreds = fillHoleDomain(predsWithoutDomain, collectionNameToCopyNum);
        return bodyPreds;
    }

    private void genPredsWithoutDomain() {
        for (DSAtomicAttr attr : tgtAttrs) {
            String tgtName = attr.getCanonicalName();
            List<String> srcNames = valueCorr.getSourceAttrsFromTarget(tgtName);
            assert srcNames.size() > 0;
            for (String srcName : srcNames) {
                String srcCollectionName = computeCollectionName(srcName);
                int currCopyNum = collectionToCopyNum.getOrDefault(srcCollectionName, 0) + 1;
                collectionToCopyNum.put(srcCollectionName, currCopyNum);
                if (currCopyNum <= maxCopyNum) {
                    DSCollection srcCollection = nameToCollection.get(srcCollectionName);
                    assert srcCollection != null : srcCollectionName;
                    // generate extensional relations for the source collection
                    srcCollection.accept(this);
                }
            }
        }
        // results are stored in field: predsWithoutDomain
    }

    private static Map<String, Long> computeCopyNum(List<RelationPredicate> preds) {
        return Collections.unmodifiableMap(preds.stream()
                .map(relPred -> relPred.relation)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
    }

    private List<DatalogPredicate> fillHoleDomain(List<RelationPredicate> preds, Map<String, Long> copyNumMap) {
        return preds.stream()
                .map(pred -> fillOnePred(pred, copyNumMap))
                .collect(Collectors.toList());
    }

    private RelationPredicate fillOnePred(RelationPredicate pred, Map<String, Long> copyNumMap) {
        List<DatalogExpression> arguments = new ArrayList<>();
        for (DatalogExpression arg : pred.arguments) {
            if (arg instanceof ExpressionHole) {
                arguments.add(fillOneHole((ExpressionHole) arg, copyNumMap));
            } else {
                arguments.add(arg);
            }
        }
        return new RelationPredicate(pred.relation, arguments);
    }

    private ExpressionHole fillOneHole(ExpressionHole hole, Map<String, Long> copyNumMap) {
        String srcAttrName = hole.name;
        List<DatalogExpression> domain = new ArrayList<>();
        // self
        long srcAttrCopyNum = getCopyNum(srcAttrName, copyNumMap);
        for (long i = 0; i < srcAttrCopyNum; ++i) {
            domain.add(new Identifier(getVarName(srcAttrName, i)));
        }
        // aliasing source attributes
        if (valueCorr.containsSrcToSrcKey(srcAttrName)) {
            for (String name : valueCorr.getSourceAttrsFromSource(srcAttrName)) {
                long copyNum = getCopyNum(name, copyNumMap);
                for (long i = 0; i < copyNum; ++i) {
                    domain.add(new Identifier(getVarName(name, i)));
                }
            }
        }
        // corresponding target attributes
        if (valueCorr.containsSrcToTgtKey(srcAttrName)) {
            for (String tgtAttrName : valueCorr.getTargetAttrsFromSource(srcAttrName)) {
                // only include the target variables that occur in the head
                if (tgtAttrNames.contains(tgtAttrName)) {
                    domain.add(new Identifier(PREFIX_VAR + tgtAttrName));
                }
            }
        }
        // string literals
        if (valueCorr.containsSrcToConstsKey(srcAttrName)) {
            for (String value : valueCorr.getConstsFromSource(srcAttrName)) {
                domain.add(new StringLiteral(value));
            }
        }
        return new ExpressionHole(getFreshHoleName(), domain);
    }

    // ===============================================================
    // predicates in the body of the rule sketch, with empty domain
    private final LinkedList<RelationPredicate> predsWithoutDomain = new LinkedList<>();
    // previous collection that the visitor visited
    private DSCollection prevCollection = null;
    // name of previous join variables
    private String prevJoinVarName = null;

    @Override
    public List<DatalogExpression> visit(DSCollection collection) {
        String collectionName = collection.getCanonicalName();
        List<DatalogExpression> arguments = collection.document.accept(this);
        if (parentMap.containsKey(collectionName)) {
            String parent = parentMap.get(collectionName);
            String joinVarName = getFreshJoinVarName();
            arguments.add(0, new Identifier(joinVarName)); // add the join variable
            predsWithoutDomain.addFirst(new RelationPredicate(collection.getCanonicalName(), arguments));
            prevCollection = collection;
            prevJoinVarName = joinVarName;
            DSCollection parentCollection = nameToCollection.get(parent);
            assert parentCollection != null : parent;
            parentCollection.accept(this);
        } else { // top-level collection
            predsWithoutDomain.addFirst(new RelationPredicate(collection.getCanonicalName(), arguments));
            prevCollection = null;
            prevJoinVarName = null;
        }
        // the return value will not be used
        return null;
    }

    @Override
    public List<DatalogExpression> visit(DSDocument document) {
        return document.attributes.stream()
                .flatMap(attr -> attr.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<DatalogExpression> visit(DSAtomicAttr atomicAttr) {
        // use the attribute name as the hole name here
        // will be updated in the second pass
        return Collections.singletonList(new ExpressionHole(atomicAttr.getCanonicalName(), Collections.emptyList()));
    }

    @Override
    public List<DatalogExpression> visit(DSCollectionAttr collectionAttr) {
        DatalogExpression expr = collectionAttr.collection.equals(prevCollection)
                ? new Identifier(prevJoinVarName)
                : PlaceHolder.getInstance();
        return Collections.singletonList(expr);
    }
    // ===============================================================

    private static String computeCollectionName(String canonicalAttrName) {
        int index = canonicalAttrName.lastIndexOf(DELIMITER);
        assert index > 0;
        return canonicalAttrName.substring(0, index);
    }

    private static long getCopyNum(String canonicalAttrName, Map<String, Long> copyNumMap) {
        String collectionName = computeCollectionName(canonicalAttrName);
        // FIXME: what if there is an aliasing attribute in another relation?
        return copyNumMap.containsKey(collectionName) ? copyNumMap.get(collectionName) : 0;
    }

    private static Set<String> computeAttributeNames(List<DSAtomicAttr> attrs) {
        return attrs.stream()
                .map(DSAtomicAttr::getCanonicalName)
                .collect(Collectors.toSet());
    }

    private String getFreshHoleName() {
        ++holeIndex;
        return PREFIX_HOLE + holeIndex;
    }

    private String getFreshJoinVarName() {
        ++varIndex;
        return PREFIX_VAR + varIndex;
    }

    private String getVarName(String attrName, long index) {
        return PREFIX_VAR + attrName + "_" + index;
    }

}

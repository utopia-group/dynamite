package dynamite.datalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.IntLiteral;
import dynamite.datalog.ast.PlaceHolder;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.util.ImmutablePair;
import dynamite.util.ListMultiMap;
import dynamite.util.MapUtils;

/**
 * A visitor that aggressively simplifies the Datalog program.
 * In addition to the {@link SimplificationVisitor}, it also
 * (1) Merge body predicates
 * (2) Remove disconnected predicates
 * (3) Replace null literals with place holders
 * <br>
 * This visitor does not modify the original AST.
 */
public final class AggressiveSimplVisitor extends SimplificationVisitor {

    // Datalog expressions in the current rule head
    private Set<DatalogExpression> currHeadExprs;

    @Override
    protected DatalogRule simplifyRule(DatalogRule rule) {
        currHeadExprs = computeHeadExprs(rule);
        DatalogRule currRule = rule;
        int prevSize = -1;
        while (currRule.bodies.size() != prevSize) {
            prevSize = currRule.bodies.size();
            currRule = rewriteToPlaceHolders(currRule, computeFrequencyMap(currRule));
            currRule = removeUnnecessaryPredicates(currRule);
            currRule = removeSubsumedPredicates(currRule);
            currRule = removeUnnecessaryPredicates(currRule);
            currRule = removeDisconnectedPredicates(currRule);
            currRule = mergeBodyPredicates(currRule);
        }
        return currRule;
    }

    private static Set<DatalogExpression> computeHeadExprs(DatalogRule rule) {
        return rule.heads.stream()
                .map(relPred -> relPred.arguments.stream())
                .flatMap(Function.identity())
                .collect(Collectors.toSet());
    }

    private DatalogRule removeDisconnectedPredicates(DatalogRule rule) {
        ListMultiMap<Identifier, Integer> injVarToPredIndices = groupInjVarPredicates(rule);
        Set<Integer> deletedIndices = new HashSet<>();
        for (Identifier injVar : injVarToPredIndices.keySet()) {
            List<Integer> indices = injVarToPredIndices.get(injVar);
            if (onlyContainsConstants(rule, injVar, indices)) {
                deletedIndices.addAll(indices);
            }
        }
        // build the new rule
        List<DatalogPredicate> newBodies = new ArrayList<>();
        for (int i = 0; i < rule.bodies.size(); ++i) {
            if (!deletedIndices.contains(i)) {
                newBodies.add(rule.bodies.get(i));
            }
        }
        return new DatalogRule(rule.heads, newBodies);
    }

    private static boolean onlyContainsConstants(DatalogRule rule, Identifier injVar, List<Integer> indices) {
        for (int index : indices) {
            RelationPredicate relPred = (RelationPredicate) rule.bodies.get(index);
            for (DatalogExpression arg : relPred.arguments) {
                if (!arg.equals(PlaceHolder.getInstance()) && !arg.equals(injVar) && !isConstant(arg)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static ListMultiMap<Identifier, Integer> groupInjVarPredicates(DatalogRule rule) {
        ListMultiMap<Identifier, Integer> ret = new ListMultiMap<>();
        for (int i = 0; i < rule.bodies.size(); ++i) {
            DatalogPredicate pred = rule.bodies.get(i);
            if (!(pred instanceof RelationPredicate)) continue;
            RelationPredicate relPred = (RelationPredicate) pred;
            Identifier ident = null;
            boolean onlyOneInjVar = true;
            for (DatalogExpression arg : relPred.arguments) {
                if (isInjectiveVariable(arg)) {
                    if (ident == null) {
                        ident = (Identifier) arg;
                    } else {
                        onlyOneInjVar = false;
                        break;
                    }
                }
            }
            if (onlyOneInjVar && ident != null) {
                ret.put(ident, i);
            }
        }
        return ret;
    }

    private DatalogRule mergeBodyPredicates(DatalogRule rule) {
        DatalogRule currRule = rule;
        Set<ImmutablePair<Integer, Integer>> unmergableSet = new HashSet<>();
        ImmutablePair<Integer, Integer> indices = findTwoRelPredsToMerge(currRule, unmergableSet);
        while (indices != null) {
            ImmutablePair<DatalogRule, Substitution> result = merge(currRule, indices, unmergableSet);
            if (result == null) { // cannot merge these two predicates
                unmergableSet.add(indices);
            } else {
                DatalogRule mergedRule = result.first;
                Substitution substitution = result.second;
                currRule = propagate(mergedRule, substitution);
                unmergableSet.clear();
            }
            indices = findTwoRelPredsToMerge(currRule, unmergableSet);
        }
        return currRule;
    }

    /**
     * Find two relation predicates with the same relation name.
     *
     * @param rule the Datalog rule
     * @return {@code null} if two predicates cannot be found; or a pair of indices of the rule body
     */
    private ImmutablePair<Integer, Integer> findTwoRelPredsToMerge(
            DatalogRule rule,
            Set<ImmutablePair<Integer, Integer>> unmergableSet) {
        List<DatalogPredicate> bodyPreds = rule.bodies;
        for (int i = 0; i < bodyPreds.size(); ++i) {
            DatalogPredicate outerPred = bodyPreds.get(i);
            if (!(outerPred instanceof RelationPredicate)) continue;
            RelationPredicate outerRelPred = (RelationPredicate) outerPred;
            for (int j = i + 1; j < bodyPreds.size(); ++j) {
                DatalogPredicate innerPred = bodyPreds.get(j);
                if (!(innerPred instanceof RelationPredicate)) continue;
                RelationPredicate innerRelPred = (RelationPredicate) innerPred;
                if (innerRelPred.relation.equals(outerRelPred.relation)) {
                    ImmutablePair<Integer, Integer> pair = new ImmutablePair<>(i, j);
                    if (!unmergableSet.contains(pair)) {
                        return pair;
                    }
                }
            }
        }
        return null;
    }

    private ImmutablePair<DatalogRule, Substitution> merge(
            DatalogRule rule,
            ImmutablePair<Integer, Integer> indices,
            final Set<ImmutablePair<Integer, Integer>> unmergableSet) {
        RelationPredicate left = (RelationPredicate) rule.bodies.get(indices.first);
        RelationPredicate right = (RelationPredicate) rule.bodies.get(indices.second);
        ImmutablePair<RelationPredicate, Substitution> result = mergeRelPred(left, right);
        if (result == null) { // cannot merge left and right
            unmergableSet.add(indices);
            return null;
        }
        // can merge left and right; build the new rule
        List<DatalogPredicate> newBodies = new ArrayList<>();
        for (int i = 0; i < rule.bodies.size(); ++i) {
            if (i == indices.first) {
                newBodies.add(result.first);
            } else if (i == indices.second) {
                ; // skip
            } else {
                newBodies.add(rule.bodies.get(i));
            }
        }
        DatalogRule newRule = new DatalogRule(rule.heads, newBodies);
        return new ImmutablePair<>(newRule, result.second);
    }

    /**
     * Merge two relation predicates.
     *
     * @param left  the left relational predicate
     * @param right the right relational predicate
     * @return {@code null} if the specified predicates cannot be merged; or a pair with
     *         the first element being the merged predicate, and the second element being the substitution
     */
    private ImmutablePair<RelationPredicate, Substitution> mergeRelPred(
            RelationPredicate left, RelationPredicate right) {
        List<DatalogExpression> newArgs = new ArrayList<>();
        Map<DatalogExpression, DatalogExpression> exprMap = new HashMap<>();
        assert left.arity() == right.arity();
        for (int i = 0; i < left.arity(); ++i) {
            DatalogExpression leftArg = left.arguments.get(i);
            DatalogExpression rightArg = right.arguments.get(i);
            MergeType mergeType = getMergeType(leftArg, rightArg);
            if (mergeType == MergeType.LEFT) {
                newArgs.add(leftArg);
                if (!isPlaceHolder(rightArg)) {
                    boolean safe = MapUtils.safePut(exprMap, rightArg, leftArg);
                    assert safe : rightArg;
                }
            } else if (mergeType == MergeType.RIGHT) {
                newArgs.add(rightArg);
                if (!isPlaceHolder(leftArg)) {
                    boolean safe = MapUtils.safePut(exprMap, leftArg, rightArg);
                    assert safe : leftArg;
                }
            } else if (mergeType == MergeType.SAME) {
                if (leftArg.equals(rightArg)) {
                    newArgs.add(leftArg);
                } else {
                    return null;
                }
            } else {
                throw new IllegalStateException("Unknown merge type: " + mergeType.name());
            }
        }
        return new ImmutablePair<>(new RelationPredicate(left.relation, newArgs), new Substitution(exprMap));
    }

    private DatalogRule propagate(DatalogRule rule, Substitution substitution) {
        return (DatalogRule) rule.accept(new SubstitutionVisitor(substitution));
    }

    private enum MergeType {
        LEFT, // keep left
        RIGHT, // keep right
        SAME, // keep either if left and right are the same; otherwise unmergable
    }

    private boolean isHeadVariable(DatalogExpression expr) {
        if (!(expr instanceof Identifier)) return false;
        return currHeadExprs.contains(expr);
    }

    private static boolean isConstant(DatalogExpression expr) {
        return expr instanceof IntLiteral || expr instanceof StringLiteral;
    }

    private boolean isNonHeadVariable(DatalogExpression expr) {
        if (!(expr instanceof Identifier)) return false;
        return !currHeadExprs.contains(expr);
    }

    private int getExprType(DatalogExpression expr) {
        if (isHeadVariable(expr)) {
            return 0;
        } else if (isConstant(expr)) {
            return 1;
        } else if (isNonHeadVariable(expr)) {
            return 2;
        } else if (isPlaceHolder(expr)) {
            return 3;
        } else {
            throw new IllegalStateException("Unknown expression type: " + expr);
        }
    }

    private static final MergeType[][] MERGE_STRATEGY = new MergeType[][] {
            { MergeType.SAME, MergeType.SAME, MergeType.LEFT, MergeType.LEFT },
            { MergeType.SAME, MergeType.SAME, MergeType.LEFT, MergeType.LEFT },
            { MergeType.RIGHT, MergeType.RIGHT, MergeType.LEFT, MergeType.LEFT },
            { MergeType.RIGHT, MergeType.RIGHT, MergeType.RIGHT, MergeType.LEFT },
    };

    private MergeType getMergeType(DatalogExpression left, DatalogExpression right) {
        int leftIndex = getExprType(left);
        int rightIndex = getExprType(right);
        return MERGE_STRATEGY[leftIndex][rightIndex];
    }

    @Override
    public DatalogExpression visit(StringLiteral literal) {
        return literal.value.equals("null") ? PlaceHolder.getInstance() : literal;
    }

}

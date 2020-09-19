package dynamite.datalog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dynamite.datalog.ast.BinaryPredicate;
import dynamite.datalog.ast.ConcatFunction;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.ExpressionHole;
import dynamite.datalog.ast.IExpressionVisitor;
import dynamite.datalog.ast.IPredicateVisitor;
import dynamite.datalog.ast.IProgramVisitor;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.IntLiteral;
import dynamite.datalog.ast.PlaceHolder;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;

/**
 * A visitor that simplifies the Datalog program. Specifically,
 * (1) it replaces the variables that only occur once to place-holders;
 * (2) it removes unnecessary predicates (variables are all place-holders),
 * (3) it removed subsumed predicates (can be obtained by alpha-renaming),
 * (4) it keeps everything else the same.
 * <br>
 * This visitor does not modify the original AST.
 */
public class SimplificationVisitor implements
        IExpressionVisitor<DatalogExpression>,
        IPredicateVisitor<DatalogPredicate>,
        IProgramVisitor<DatalogProgram> {

    @Override
    public DatalogProgram visit(DatalogProgram program) {
        return new DatalogProgram(program.declarations, simplifyRules(program.rules), program.facts);
    }

    private List<DatalogRule> simplifyRules(List<DatalogRule> rules) {
        return rules.stream()
                .map(rule -> simplifyRule(rule))
                .collect(Collectors.toList());
    }

    protected DatalogRule simplifyRule(DatalogRule rule) {
        DatalogRule currRule = rule;
        int prevSize = -1;
        while (currRule.bodies.size() != prevSize) {
            prevSize = currRule.bodies.size();
            // rewrite the variables that occur once to place-holders
            currRule = rewriteToPlaceHolders(currRule, computeFrequencyMap(currRule));
            currRule = removeUnnecessaryPredicates(currRule);
            currRule = removeSubsumedPredicates(currRule);
            currRule = removeUnnecessaryPredicates(currRule);
        }
        return currRule;
    }

    protected static Map<Identifier, Long> computeFrequencyMap(DatalogRule rule) {
        return Stream.concat(rule.heads.stream(), rule.bodies.stream()) // Stream<DatalogPredicate>
                .filter(RelationPredicate.class::isInstance)
                .map(RelationPredicate.class::cast) // Stream<RelationPredicate>
                .map(relPred -> relPred.arguments.stream()) // Stream<Stream<DatalogExpression>
                .flatMap(Function.identity()) // Stream<DatalogExpression>
                .filter(Identifier.class::isInstance)
                .map(Identifier.class::cast) // Stream<Identifier>
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    protected DatalogRule rewriteToPlaceHolders(DatalogRule rule, Map<Identifier, Long> identToFreqMap) {
        currIdentToFreqMap = identToFreqMap;
        List<DatalogPredicate> newBodies = rule.bodies.stream()
                .map(pred -> pred.accept(this))
                .collect(Collectors.toList());
        return new DatalogRule(rule.heads, newBodies);
    }

    protected DatalogRule removeUnnecessaryPredicates(DatalogRule rule) {
        List<DatalogPredicate> newBodies = new ArrayList<>();
        for (DatalogPredicate pred : rule.bodies) {
            if (pred instanceof RelationPredicate) {
                RelationPredicate relPred = (RelationPredicate) pred;
                if (isUnnecessary(relPred)) continue; // remove unnecessary predicates
            }
            newBodies.add(pred);
        }
        return new DatalogRule(rule.heads, newBodies);
    }

    protected static boolean isInjectiveVariable(DatalogExpression expr) {
        if (!(expr instanceof Identifier)) return false;
        Identifier ident = (Identifier) expr;
        return ident.name.matches("v_\\d+");
    }

    protected static boolean isPlaceHolder(DatalogExpression expr) {
        return expr instanceof PlaceHolder;
    }

    private boolean isUnnecessary(RelationPredicate relPred) {
        DatalogExpression firstArg = relPred.arguments.get(0);
        if (!isInjectiveVariable(firstArg) && !isPlaceHolder(firstArg)) {
            return false;
        }
        for (int i = 1; i < relPred.arguments.size(); ++i) {
            if (!isPlaceHolder(relPred.arguments.get(i))) {
                return false;
            }
        }
        return true;
    }

    protected DatalogRule removeSubsumedPredicates(DatalogRule rule) {
        Set<Integer> deletedIndices = new HashSet<>();
        for (int i = 0; i < rule.bodies.size(); ++i) {
            if (deletedIndices.contains(i)) continue;
            DatalogPredicate relPred = rule.bodies.get(i);
            if (!(relPred instanceof RelationPredicate)) continue;
            for (int j = 0; j < rule.bodies.size(); ++j) {
                if (j == i || deletedIndices.contains(j)) continue;
                DatalogPredicate subPred = rule.bodies.get(j);
                if (!(subPred instanceof RelationPredicate)) continue;
                if (isSubsumed((RelationPredicate) relPred, (RelationPredicate) subPred)) {
                    deletedIndices.add(j);
                }
            }
        }
        List<DatalogPredicate> newBodies = new ArrayList<>();
        for (int i = 0; i < rule.bodies.size(); ++i) {
            if (!deletedIndices.contains(i)) {
                newBodies.add(rule.bodies.get(i));
            }
        }
        return new DatalogRule(rule.heads, newBodies);
    }

    private boolean isSubsumed(RelationPredicate relPred, RelationPredicate subPred) {
        if (!subPred.relation.equals(relPred.relation)) return false;
        List<DatalogExpression> relArgs = relPred.arguments;
        List<DatalogExpression> subArgs = subPred.arguments;
        assert relArgs.size() == subArgs.size();
        for (int i = 0; i < relArgs.size(); ++i) {
            DatalogExpression relArg = relArgs.get(i);
            DatalogExpression subArg = subArgs.get(i);
            if (!subArg.equals(relArg) && !subArg.equals(PlaceHolder.getInstance())) return false;
        }
        return true;
    }

    // current frequency map: identifier -> frequency
    private Map<Identifier, Long> currIdentToFreqMap = null;

    @Override
    public RelationPredicate visit(RelationPredicate predicate) {
        return new RelationPredicate(predicate.relation, predicate.arguments.stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.toList()));
    }

    @Override
    public BinaryPredicate visit(BinaryPredicate predicate) {
        return predicate;
    }

    @Override
    public DatalogExpression visit(Identifier identifier) {
        return currIdentToFreqMap.get(identifier) == 1
                ? PlaceHolder.getInstance()
                : identifier;
    }

    @Override
    public IntLiteral visit(IntLiteral literal) {
        return literal;
    }

    @Override
    public DatalogExpression visit(StringLiteral literal) {
        return literal;
    }

    @Override
    public ExpressionHole visit(ExpressionHole hole) {
        throw new IllegalStateException("Sketch holes cannot be simplified");
    }

    @Override
    public ConcatFunction visit(ConcatFunction concatFunc) {
        return concatFunc;
    }

    @Override
    public PlaceHolder visit(PlaceHolder placeHolder) {
        return placeHolder;
    }

}

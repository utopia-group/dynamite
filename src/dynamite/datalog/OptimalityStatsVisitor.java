package dynamite.datalog;

import java.util.Map;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.IProgramVisitor;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.util.DatalogAstUtils;

/**
 * A visitor that computes the optimality statistics of a Datalog program given a golden program.
 * <br>
 * This visitor does not modify the original AST.
 */
public final class OptimalityStatsVisitor implements
        IProgramVisitor<Void> {

    // name of head relations -> its corresponding Datalog rule
    private final Map<String, DatalogRule> headNameToRuleMap;
    // number of optimal rules
    private int optimalCount = 0;
    // total difference of body predicate count
    private int totalDifference = 0;

    public OptimalityStatsVisitor(DatalogProgram goldenProgram) {
        this.headNameToRuleMap = DatalogAstUtils.buildHeadNameToRuleMap(goldenProgram);
    }

    public int getOptimalRuleCount() {
        return optimalCount;
    }

    public int getOptimalityDifference() {
        return totalDifference;
    }

    @Override
    public Void visit(DatalogProgram program) {
        program.rules.forEach(rule -> visit(rule));
        return null;
    }

    public void visit(DatalogRule rule) {
        for (RelationPredicate head : rule.heads) {
            if (!headNameToRuleMap.containsKey(head.relation)) {
                throw new IllegalStateException("Cannot find rule with head name: " + head.relation);
            }
            DatalogRule goldenRule = headNameToRuleMap.get(head.relation);
            if (areSame(rule, goldenRule)) {
                ++optimalCount;
            } else {
                totalDifference += optimalityDifference(rule, goldenRule);
            }
        }
    }

    // NOTE: In our context, two rules are considered to be the same if they
    // have the same number of body predicates
    private boolean areSame(DatalogRule lhs, DatalogRule rhs) {
        return lhs.bodies.size() == rhs.bodies.size();
    }

    private int optimalityDifference(DatalogRule actual, DatalogRule golden) {
        if (actual.bodies.size() < golden.bodies.size()) {
            System.out.println("WARNING: Actual program is shorter than the golden program.");
            System.out.println("Actual:");
            System.out.println(actual.toSouffle());
            System.out.println("Golden:");
            System.out.println(golden.toSouffle());
            return 0;
        }
        return actual.bodies.size() - golden.bodies.size();
    }

}

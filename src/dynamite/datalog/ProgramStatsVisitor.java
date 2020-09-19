package dynamite.datalog;

import dynamite.datalog.ast.BinaryPredicate;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.IPredicateVisitor;
import dynamite.datalog.ast.IProgramVisitor;
import dynamite.datalog.ast.IStatementVisitor;
import dynamite.datalog.ast.InputDeclaration;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.TypeDeclaration;

/**
 * A visitor that computes the statistics of a Datalog program.
 * <br>
 * This visitor does not modify the original AST.
 */
public final class ProgramStatsVisitor implements
        IStatementVisitor<Void>,
        IPredicateVisitor<Void>,
        IProgramVisitor<Void> {

    // number of rules
    private int ruleCount;
    // number of predicates
    private int predCount;
    // number of predicates in head for the current rule
    private int currHeadSize;

    public ProgramStatsVisitor() {
        this.ruleCount = 0;
        this.predCount = 0;
    }

    /**
     * @return the number of rules in the Datalog program.
     *         If a rule has n predicate in the head, it is considered as n rules.
     */
    public int getRuleCount() {
        return ruleCount;
    }

    /**
     * @return the number of body predicates in the Datalog program.
     *         If a rule has n predicate in the head, all its body predicates are considered as n copies.
     */
    public int getPredCount() {
        return predCount;
    }

    @Override
    public Void visit(DatalogProgram program) {
        for (DatalogRule rule : program.rules) {
            rule.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(RelationPredicate predicate) {
        predCount += currHeadSize;
        return null;
    }

    @Override
    public Void visit(BinaryPredicate predicate) {
        return null;
    }

    @Override
    public Void visit(TypeDeclaration declaration) {
        return null;
    }

    @Override
    public Void visit(InputDeclaration declaration) {
        return null;
    }

    @Override
    public Void visit(OutputDeclaration declaration) {
        return null;
    }

    @Override
    public Void visit(RelationDeclaration declaration) {
        return null;
    }

    @Override
    public Void visit(DatalogRule rule) {
        currHeadSize = rule.heads.size();
        ruleCount += currHeadSize;
        for (DatalogPredicate pred : rule.bodies) {
            pred.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DatalogFact fact) {
        return null;
    }

}

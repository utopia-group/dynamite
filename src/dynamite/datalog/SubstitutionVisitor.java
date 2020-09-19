package dynamite.datalog;

import java.util.List;
import java.util.stream.Collectors;

import dynamite.datalog.ast.BinaryPredicate;
import dynamite.datalog.ast.ConcatFunction;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.ExpressionHole;
import dynamite.datalog.ast.IExpressionVisitor;
import dynamite.datalog.ast.IPredicateVisitor;
import dynamite.datalog.ast.IProgramVisitor;
import dynamite.datalog.ast.IStatementVisitor;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.InputDeclaration;
import dynamite.datalog.ast.IntLiteral;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.PlaceHolder;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.datalog.ast.TypeDeclaration;

/**
 * A visitor that performs substitution on Datalog programs.
 * <br>
 * This visitor does not modify the original AST.
 */
public final class SubstitutionVisitor implements
        IExpressionVisitor<DatalogExpression>,
        IStatementVisitor<DatalogStatement>,
        IPredicateVisitor<DatalogPredicate>,
        IProgramVisitor<DatalogProgram> {

    public final Substitution substitution;

    public SubstitutionVisitor(Substitution substitution) {
        this.substitution = substitution;
    }

    @Override
    public DatalogProgram visit(DatalogProgram program) {
        List<DatalogStatement> decls = program.declarations.stream()
                .map(decl -> decl.accept(this))
                .collect(Collectors.toList());
        List<DatalogRule> rules = program.rules.stream()
                .map(rule -> rule.accept(this))
                .map(DatalogRule.class::cast)
                .collect(Collectors.toList());
        List<DatalogFact> facts = program.rules.stream()
                .map(fact -> fact.accept(this))
                .map(DatalogFact.class::cast)
                .collect(Collectors.toList());
        return new DatalogProgram(decls, rules, facts);
    }

    @Override
    public TypeDeclaration visit(TypeDeclaration declaration) {
        return declaration;
    }

    @Override
    public InputDeclaration visit(InputDeclaration declaration) {
        return declaration;
    }

    @Override
    public OutputDeclaration visit(OutputDeclaration declaration) {
        return declaration;
    }

    @Override
    public RelationDeclaration visit(RelationDeclaration declaration) {
        return declaration;
    }

    @Override
    public DatalogRule visit(DatalogRule rule) {
        List<RelationPredicate> heads = rule.heads.stream()
                .map(head -> head.accept(this))
                .map(RelationPredicate.class::cast)
                .collect(Collectors.toList());
        List<DatalogPredicate> bodies = rule.bodies.stream()
                .map(body -> body.accept(this))
                .collect(Collectors.toList());
        return new DatalogRule(heads, bodies);
    }

    @Override
    public DatalogFact visit(DatalogFact fact) {
        return fact;
    }

    @Override
    public RelationPredicate visit(RelationPredicate predicate) {
        return new RelationPredicate(predicate.relation, predicate.arguments.stream()
                .map(argument -> argument.accept(this))
                .collect(Collectors.toList()));
    }

    @Override
    public BinaryPredicate visit(BinaryPredicate predicate) {
        return new BinaryPredicate(predicate.lhs.accept(this), predicate.op, predicate.rhs.accept(this));
    }

    @Override
    public DatalogExpression visit(Identifier identifier) {
        return substitution.containsKey(identifier) ? substitution.get(identifier) : identifier;
    }

    @Override
    public DatalogExpression visit(IntLiteral literal) {
        return substitution.containsKey(literal) ? substitution.get(literal) : literal;
    }

    @Override
    public DatalogExpression visit(StringLiteral literal) {
        return substitution.containsKey(literal) ? substitution.get(literal) : literal;
    }

    @Override
    public ExpressionHole visit(ExpressionHole hole) {
        List<DatalogExpression> domain = hole.domain.stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.toList());
        return new ExpressionHole(hole.name, domain);
    }

    @Override
    public ConcatFunction visit(ConcatFunction concatFunc) {
        return new ConcatFunction(concatFunc.lhs.accept(this), concatFunc.rhs.accept(this));
    }

    @Override
    public DatalogExpression visit(PlaceHolder placeHolder) {
        return substitution.containsKey(placeHolder) ? substitution.get(placeHolder) : placeHolder;
    }

}

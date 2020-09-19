package dynamite.datalog;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.datalog.ast.BinaryPredicate;
import dynamite.datalog.ast.ConcatFunction;
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
 * A visitor that collects all the identifiers occurred in the head of Datalog rules.
 * Note that the return values are lists, which may potentially contain duplicated identifiers.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class HeadVarExtractionVisitor implements
        IProgramVisitor<List<Identifier>>,
        IPredicateVisitor<List<Identifier>>,
        IExpressionVisitor<List<Identifier>> {

    @Override
    public List<Identifier> visit(Identifier identifier) {
        return Collections.singletonList(identifier);
    }

    @Override
    public List<Identifier> visit(IntLiteral literal) {
        return Collections.emptyList();
    }

    @Override
    public List<Identifier> visit(StringLiteral literal) {
        return Collections.emptyList();
    }

    @Override
    public List<Identifier> visit(ExpressionHole hole) {
        throw new IllegalStateException("Holes should not occur in heads");
    }

    @Override
    public List<Identifier> visit(ConcatFunction concatFunc) {
        throw new IllegalStateException("Concat functions should not occur in heads");
    }

    @Override
    public List<Identifier> visit(PlaceHolder placeHolder) {
        throw new IllegalStateException("Place holders should not occur in heads");
    }

    @Override
    public List<Identifier> visit(RelationPredicate predicate) {
        return predicate.arguments.stream()
                .flatMap(argument -> argument.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Identifier> visit(BinaryPredicate predicate) {
        throw new IllegalStateException("Binary predicates should not occur in heads");
    }

    public List<Identifier> visit(DatalogRule rule) {
        return rule.heads.stream()
                .flatMap(head -> head.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Identifier> visit(DatalogProgram program) {
        return program.rules.stream()
                .flatMap(rule -> visit(rule).stream())
                .collect(Collectors.toList());
    }

}

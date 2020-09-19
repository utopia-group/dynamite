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
 * A visitor that collects all the expression holes in the Datalog program sketch.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class HoleExtractionVisitor implements
        IProgramVisitor<List<ExpressionHole>>,
        IPredicateVisitor<List<ExpressionHole>>,
        IExpressionVisitor<List<ExpressionHole>> {

    @Override
    public List<ExpressionHole> visit(Identifier identifier) {
        return Collections.emptyList();
    }

    @Override
    public List<ExpressionHole> visit(IntLiteral literal) {
        return Collections.emptyList();
    }

    @Override
    public List<ExpressionHole> visit(StringLiteral literal) {
        return Collections.emptyList();
    }

    @Override
    public List<ExpressionHole> visit(ExpressionHole hole) {
        return Collections.singletonList(hole);
    }

    @Override
    public List<ExpressionHole> visit(ConcatFunction concatFunc) {
        List<ExpressionHole> list = concatFunc.lhs.accept(this);
        list.addAll(concatFunc.rhs.accept(this));
        return list;
    }

    @Override
    public List<ExpressionHole> visit(PlaceHolder placeHolder) {
        return Collections.emptyList();
    }

    @Override
    public List<ExpressionHole> visit(RelationPredicate predicate) {
        return predicate.arguments.stream()
                .flatMap(argument -> argument.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpressionHole> visit(BinaryPredicate predicate) {
        List<ExpressionHole> list = predicate.lhs.accept(this);
        list.addAll(predicate.rhs.accept(this));
        return list;
    }

    public List<ExpressionHole> visit(DatalogRule rule) {
        return rule.bodies.stream()
                .flatMap(body -> body.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpressionHole> visit(DatalogProgram program) {
        return program.rules.stream()
                .flatMap(rule -> visit(rule).stream())
                .collect(Collectors.toList());
    }

}

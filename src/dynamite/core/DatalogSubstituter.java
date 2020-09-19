package dynamite.core;

import java.util.List;
import java.util.stream.Collectors;

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
import dynamite.smt.SmtModel;

/**
 * A Datalog visitor that substitutes the holes in the Datalog program sketch
 * according to a given SMT model.
 * <br>
 * This visitor does not modify the AST.
 */
public final class DatalogSubstituter implements
        IExpressionVisitor<DatalogExpression>,
        IPredicateVisitor<DatalogPredicate>,
        IProgramVisitor<DatalogProgram> {

    private final SmtModel model;
    private final EncodingMap encodingMap;

    public DatalogSubstituter(SmtModel model, EncodingMap encodingMap) {
        this.model = model;
        this.encodingMap = encodingMap;
    }

    @Override
    public DatalogProgram visit(DatalogProgram program) {
        List<DatalogRule> rules = program.rules.stream()
                .map(rule -> visit(rule))
                .collect(Collectors.toList());
        return new DatalogProgram(program.declarations, rules, program.facts);
    }

    public DatalogRule visit(DatalogRule rule) {
        List<DatalogPredicate> bodies = rule.bodies.stream()
                .map(body -> body.accept(this))
                .collect(Collectors.toList());
        return new DatalogRule(rule.heads, bodies);
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
    public Identifier visit(Identifier identifier) {
        return identifier;
    }

    @Override
    public IntLiteral visit(IntLiteral literal) {
        return literal;
    }

    @Override
    public StringLiteral visit(StringLiteral literal) {
        return literal;
    }

    @Override
    public DatalogExpression visit(ExpressionHole hole) {
        int value = model.getValue(hole.name);
        return encodingMap.getExprByInteger(value);
    }

    @Override
    public ConcatFunction visit(ConcatFunction concatFunc) {
        return new ConcatFunction(concatFunc.lhs.accept(this), concatFunc.rhs.accept(this));
    }

    @Override
    public DatalogExpression visit(PlaceHolder placeHolder) {
        return placeHolder;
    }

}

package dynamite.datalog;

import java.math.BigInteger;

import dynamite.datalog.ast.BinaryPredicate;
import dynamite.datalog.ast.ConcatFunction;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
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
 * A visitor that computes the size of search space for sketches.
 * <br>
 * This visitor does not modify the original AST.
 */
public final class SearchSpaceSizeVisitor implements
        IExpressionVisitor<BigInteger>,
        IStatementVisitor<BigInteger>,
        IPredicateVisitor<BigInteger>,
        IProgramVisitor<BigInteger> {

    @Override
    public BigInteger visit(DatalogProgram program) {
        BigInteger size = BigInteger.ZERO;
        for (DatalogRule rule : program.rules) {
            size = size.add(rule.accept(this));
        }
        return size;
    }

    @Override
    public BigInteger visit(TypeDeclaration declaration) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(InputDeclaration declaration) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(OutputDeclaration declaration) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(RelationDeclaration declaration) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(DatalogRule rule) {
        BigInteger size = BigInteger.ONE;
        for (RelationPredicate head : rule.heads) {
            size = size.multiply(head.accept(this));
        }
        for (DatalogPredicate body : rule.bodies) {
            size = size.multiply(body.accept(this));
        }
        return size;
    }

    @Override
    public BigInteger visit(DatalogFact fact) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(RelationPredicate predicate) {
        BigInteger size = BigInteger.ONE;
        for (DatalogExpression expr : predicate.arguments) {
            size = size.multiply(expr.accept(this));
        }
        return size;
    }

    @Override
    public BigInteger visit(BinaryPredicate predicate) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(Identifier identifier) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(IntLiteral literal) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(StringLiteral literal) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(ExpressionHole hole) {
        return BigInteger.valueOf(hole.domain.size());
    }

    @Override
    public BigInteger visit(ConcatFunction concatFunc) {
        return BigInteger.ONE;
    }

    @Override
    public BigInteger visit(PlaceHolder placeHolder) {
        return BigInteger.ONE;
    }

}

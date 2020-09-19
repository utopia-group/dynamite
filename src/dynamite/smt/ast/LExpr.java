package dynamite.smt.ast;

/**
 * Abstract class for logical expressions.
 */
public abstract class LExpr {

    public abstract <T> T accept(ILExprVisitor<T> visitor);

}

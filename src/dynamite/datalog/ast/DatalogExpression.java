package dynamite.datalog.ast;

public abstract class DatalogExpression extends DatalogAstNode {

    public abstract <T> T accept(IExpressionVisitor<T> visitor);

}

package dynamite.datalog.ast;

public abstract class DatalogStatement extends DatalogAstNode {

    public abstract <T> T accept(IStatementVisitor<T> visitor);

}

package dynamite.datalog.ast;

public abstract class DatalogPredicate extends DatalogAstNode {

    public abstract <T> T accept(IPredicateVisitor<T> visitor);

}

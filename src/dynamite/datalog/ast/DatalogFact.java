package dynamite.datalog.ast;

public final class DatalogFact extends DatalogStatement {

    public final RelationPredicate relationPred;

    public DatalogFact(RelationPredicate relationPred) {
        this.relationPred = relationPred;
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.format("%s.", relationPred.toSouffle());
    }

    @Override
    public String toString() {
        return String.format("Fact(%s)", relationPred);
    }

}

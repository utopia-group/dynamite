package dynamite.reldb.ast;

public abstract class RIValue {

    public abstract <T> T accept(IRIValueVisitor<T> visitor);

}

package dynamite.graphdb.ast;

public abstract class GIValue {

    public abstract <T> T accept(IGIValueVisitor<T> visitor);

}

package dynamite.graphdb.ast;

public abstract class GSAstNode {

    public abstract <T> T accept(IGSAstVisitor<T> visitor);

}

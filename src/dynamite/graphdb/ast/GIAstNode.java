package dynamite.graphdb.ast;

public abstract class GIAstNode {

    public abstract <T> T accept(IGIAstVisitor<T> visitor);

}

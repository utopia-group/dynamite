package dynamite.docdb.ast;

/**
 * AST node for document instances.
 */
public abstract class DIAstNode {

    public abstract <T> T accept(IDIAstVisitor<T> visitor);

}

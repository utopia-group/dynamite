package dynamite.docdb.ast;

/**
 * AST node for document schemas.
 */
public abstract class DSAstNode {

    public abstract <T> T accept(IDSAstVisitor<T> visitor);

}

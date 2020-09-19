package dynamite.docdb.ast;

/**
 * Document schema AST node for attributes.
 */
public abstract class DSAttribute extends DSAstNode {

    /**
     * Get the canonical name of this attribute.
     *
     * @return the canonical name
     */
    public abstract String getCanonicalName();

    /**
     * Get the simple name of this attribute.
     *
     * @return the simple name
     */
    public abstract String getName();

}

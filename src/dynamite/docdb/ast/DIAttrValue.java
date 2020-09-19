package dynamite.docdb.ast;

/**
 * Document instance AST node for attribute-value pairs.
 */
public abstract class DIAttrValue extends DIAstNode {

    /**
     * Get the canonical name of this attribute.
     *
     * @return the canonical name
     */
    public abstract String getCanonicalName();

    /**
     * Get the simple name of this attribute.
     *
     * @return the simple name of this attribute
     */
    public abstract String getAttributeName();

}

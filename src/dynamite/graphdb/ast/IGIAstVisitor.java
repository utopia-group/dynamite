package dynamite.graphdb.ast;

/**
 * Interface for graph instance AST node visitors.
 *
 * @param <T> return type
 */
public interface IGIAstVisitor<T> {

    /**
     * Visit an AST node for vertices.
     *
     * @param vertex the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GIVertex vertex);

    /**
     * Visit an AST node for edges.
     *
     * @param edge the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GIEdge edge);

    /**
     * Visit an AST node for collections.
     *
     * @param collection the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GICollection collection);

    /**
     * Visit an AST node for property lists.
     *
     * @param propList the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GIPropList propList);

    /**
     * Visit an AST node for atomic property-values.
     *
     * @param atomicProp the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GIAtomicProp atomicProp);

    /**
     * Visit an AST node for collection property-values.
     *
     * @param collectionProp the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GICollectionProp collectionProp);

}

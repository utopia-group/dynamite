package dynamite.graphdb.ast;

/**
 * Interface for graph schema AST node visitors.
 *
 * @param <T> return type
 */
public interface IGSAstVisitor<T> {

    /**
     * Visit a graph schema AST node for vertices.
     *
     * @param vertex the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GSVertex vertex);

    /**
     * Visit a graph schema AST node for edges.
     *
     * @param edge the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GSEdge edge);

    /**
     * Visit a graph schema AST node for collections.
     *
     * @param collection the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GSCollection collection);

    /**
     * Visit a graph schema AST node for property lists.
     *
     * @param propList the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GSPropList propList);

    /**
     * Visit a graph schema AST node for atomic properties.
     *
     * @param atomicProp the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GSAtomicProp atomicProp);

    /**
     * Visit a graph schema AST node for collection attributes.
     *
     * @param collectionProp the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GSCollectionProp collectionProp);

}

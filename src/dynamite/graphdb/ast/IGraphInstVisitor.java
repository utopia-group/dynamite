package dynamite.graphdb.ast;

/**
 * Interface for graph instance visitors.
 *
 * @param <T> return type
 */
public interface IGraphInstVisitor<T> {

    /**
     * Visit an AST node for graph instances.
     *
     * @param instance the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GraphInstance instance);

}

package dynamite.graphdb.ast;

/**
 * Interface for graph schema visitors.
 *
 * @param <T> return type
 */
public interface IGraphSchemaVisitor<T> {

    /**
     * Visit the AST node for graph schemas.
     *
     * @param schema the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GraphSchema schema);

}

package dynamite.graphdb.ast;

/**
 * Interface for graph instance value visitors.
 *
 * @param <T> return type
 */
public interface IGIValueVisitor<T> {

    /**
     * Visit an AST node for integer values.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GIIntValue value);

    /**
     * Visit an AST node for string values.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GIStrValue value);

    /**
     * Visit an AST node for floating point values.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GIFloatValue value);

    /**
     * Visit an AST node for null values.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(GINullValue value);

}

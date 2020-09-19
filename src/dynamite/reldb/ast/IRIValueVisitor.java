package dynamite.reldb.ast;

/**
 * Interface for relational instance value visitors.
 *
 * @param <T> return type
 */
public interface IRIValueVisitor<T> {

    /**
     * Visit an integer value in relational instances.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(RIIntValue value);

    /**
     * Visit a string value in relational instances.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(RIStrValue value);

    /**
     * Visit a floating-point value in relational instances.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(RIFloatValue value);

    /**
     * Visit a null value in relational instances.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(RINullValue value);

}

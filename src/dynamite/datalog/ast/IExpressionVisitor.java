package dynamite.datalog.ast;

/**
 * Interface for datalog expression visitors.
 *
 * @param <T> return type
 */
public interface IExpressionVisitor<T> {

    /**
     * Visit an AST node of identifiers.
     *
     * @param identifier the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(Identifier identifier);

    /**
     * Visit an AST node of integer literals.
     *
     * @param literal the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(IntLiteral literal);

    /**
     * Visit an AST node of string literals.
     *
     * @param literal the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(StringLiteral literal);

    /**
     * Visit an AST node of expression holes.
     *
     * @param hole the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(ExpressionHole hole);

    /**
     * Visit an AST node of concatenation functions.
     *
     * @param concatFunc the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(ConcatFunction concatFunc);

    /**
     * Visit an AST node of place holders.
     *
     * @param placeHolder the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(PlaceHolder placeHolder);

}

package dynamite.docdb.ast;

/**
 * Interface for document instance value visitors.
 *
 * @param <T> return type
 */
public interface IDIValueVisitor<T> {

    /**
     * Visit an integer value in document instances.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DIIntValue value);

    /**
     * Visit a string value in document instances.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DIStrValue value);

    /**
     * Visit a floating point value in document instances.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DIFloatValue value);

    /**
     * Visit a null value in document instances.
     *
     * @param value the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DINullValue value);

}

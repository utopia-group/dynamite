package dynamite.reldb.ast;

/**
 * Interface for relational instance AST node visitors.
 *
 * @param <T> return type
 */
public interface IRIAstVisitor<T> {

    /**
     * Visit a relational instance AST node for tables.
     *
     * @param table the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(RITable table);

    /**
     * Visit a relational instance AST node for rows.
     *
     * @param row the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(RIRow row);

}

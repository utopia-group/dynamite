package dynamite.smt.ast;

/**
 * Interface for logical expression visitors.
 *
 * @param <T> return type
 */
public interface ILExprVisitor<T> {

    /**
     * Visit an AST node for constants (integer variables).
     *
     * @param lconst the constant to visit
     * @return a value obtained from the visiting
     */
    public T visit(LConst lconst);

    /**
     * Visit an AST node for integer literals.
     *
     * @param lit the integer literal to visit
     * @return a value obtained from the visiting
     */
    public T visit(LIntLiteral lit);

}

package dynamite.smt.ast;

/**
 * Interface for constraint visitors.
 *
 * @param <T> return type
 */
public interface IConstraintVisitor<T> {

    /**
     * Visit an SMT constraint.
     *
     * @param constraint the constraint to visit
     * @return a value obtained from the visiting
     */
    public T visit(Constraint constraint);

}

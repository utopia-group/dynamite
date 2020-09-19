package dynamite.datalog.ast;

/**
 * Interface for datalog predicate visitors.
 *
 * @param <T> return type
 */
public interface IPredicateVisitor<T> {

    /**
     * Visit an AST node of relation predicates.
     *
     * @param predicate the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(RelationPredicate predicate);

    /**
     * Visit an AST node of binary predicates.
     *
     * @param predicate the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(BinaryPredicate predicate);

}

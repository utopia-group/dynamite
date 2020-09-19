package dynamite.smt.ast;

/**
 * Interface for formula visitors.
 *
 * @param <T> return type
 */
public interface ILFormulaVisitor<T> {

    /**
     * Visit an AST node for binary predicates.
     *
     * @param pred the binary predicate to visit
     * @return a value obtained from the visiting
     */
    public T visit(LBinPred pred);

    /**
     * Visit an AST node for range predicates.
     *
     * @param pred the range predicate to visit
     * @return a value obtained from the visiting
     */
    public T visit(LRangePred pred);

    /**
     * Visit an AST node for boolean functions.
     *
     * @param pred the boolean function to visit
     * @return a value obtained from the visiting
     */
    public T visit(LBoolFunc pred);

    /**
     * Visit an AST node for conjunctions.
     *
     * @param land the conjunction to visit
     * @return a value obtained from the visiting
     */
    public T visit(LAnd land);

    /**
     * Visit an AST node for disjunctions.
     *
     * @param lor the disjunction to visit
     * @return a value obtained from the visiting
     */
    public T visit(LOr lor);

    /**
     * Visit an AST node for negations.
     *
     * @param lnot the negation to visit
     * @return a value obtained from the visiting
     */
    public T visit(LNot lnot);

}

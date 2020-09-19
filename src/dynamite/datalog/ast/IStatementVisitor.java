package dynamite.datalog.ast;

/**
 * Interface for datalog statement visitors.
 *
 * @param <T> return type
 */
public interface IStatementVisitor<T> {

    /**
     * Visit an AST node of type declarations
     *
     * @param declaration the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(TypeDeclaration declaration);

    /**
     * Visit an AST node of input declarations
     *
     * @param declaration the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(InputDeclaration declaration);

    /**
     * Visit an AST node of output declarations
     *
     * @param declaration the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(OutputDeclaration declaration);

    /**
     * Visit an AST node of relation declarations
     *
     * @param declaration the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(RelationDeclaration declaration);

    /**
     * Visit an AST node of datalog rules
     *
     * @param rule the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(DatalogRule rule);

    /**
     * Visit an AST node of datalog facts
     *
     * @param fact the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(DatalogFact fact);

}

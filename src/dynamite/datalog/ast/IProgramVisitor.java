package dynamite.datalog.ast;

/**
 * Interface for datalog program visitors.
 *
 * @param <T> return type
 */
public interface IProgramVisitor<T> {

    /**
     * Visit an AST node of datalog programs.
     *
     * @param program the AST node to visit
     * @return a value obtained by visit
     */
    public T visit(DatalogProgram program);

}

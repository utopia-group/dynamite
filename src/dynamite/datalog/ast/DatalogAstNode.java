package dynamite.datalog.ast;

public abstract class DatalogAstNode {

    /**
     * Convert the AST node to Souffle program.
     *
     * @return the Souffle program text
     */
    public abstract String toSouffle();
}

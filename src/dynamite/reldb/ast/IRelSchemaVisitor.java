package dynamite.reldb.ast;

/**
 * Interface for relational schema visitors.
 *
 * @param <T> return type
 */
public interface IRelSchemaVisitor<T> {

    /**
     * Visit a relational schema node.
     *
     * @param schema the relational schema to visit
     * @return a value obtained from the visiting
     */
    public T visit(RelationalSchema schema);

}

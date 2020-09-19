package dynamite.reldb.ast;

/**
 * Interface for relational instance visitors.
 *
 * @param <T> return type
 */
public interface IRelInstVisitor<T> {

    /**
     * Visit a relational instance node.
     *
     * @param instance the relational instance to visit
     * @return a value obtained from the visiting
     */
    public T visit(RelationalInstance instance);

}

package dynamite.docdb.ast;

/**
 * Interface for document instance visitors.
 *
 * @param <T> return type
 */
public interface IDocInstVisitor<T> {

    /**
     * Visit a document instance node.
     *
     * @param instance the document instance to visit
     * @return a value obtained from the visiting
     */
    public T visit(DocumentInstance instance);

}

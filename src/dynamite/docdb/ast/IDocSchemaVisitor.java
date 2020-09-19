package dynamite.docdb.ast;

/**
 * Interface for document schema visitors.
 *
 * @param <T> return type
 */
public interface IDocSchemaVisitor<T> {

    /**
     * Visit a document schema node.
     *
     * @param schema the document schema to visit
     * @return a value obtained from the visiting
     */
    public T visit(DocumentSchema schema);

}

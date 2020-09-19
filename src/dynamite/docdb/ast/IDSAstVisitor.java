package dynamite.docdb.ast;

/**
 * Interface for document schema AST node visitors.
 *
 * @param <T> return type
 */
public interface IDSAstVisitor<T> {

    /**
     * Visit a document schema AST node for collections.
     *
     * @param collection the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DSCollection collection);

    /**
     * Visit a document schema AST node for documents.
     *
     * @param document the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DSDocument document);

    /**
     * Visit a document schema AST node for atomic attributes.
     *
     * @param atomicAttr the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DSAtomicAttr atomicAttr);

    /**
     * Visit a document schema AST node for collection attributes.
     *
     * @param collectionAttr the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DSCollectionAttr collectionAttr);

}

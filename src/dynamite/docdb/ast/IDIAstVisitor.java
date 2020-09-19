package dynamite.docdb.ast;

/**
 * Interface for document instance AST node visitors.
 *
 * @param <T> return type
 */
public interface IDIAstVisitor<T> {

    /**
     * Visit a document instance AST node for collections.
     *
     * @param collection the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DICollection collection);

    /**
     * Visit a document instance AST node for documents.
     *
     * @param document the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DIDocument document);

    /**
     * Visit a document instance AST node for atomic attribute-value pairs.
     *
     * @param atomicAttrValue the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DIAtomicAttrValue atomicAttrValue);

    /**
     * Visit a document instance AST node for collection attribute-value pairs.
     *
     * @param collectionAttrValue the AST node to visit
     * @return a value obtained from the visiting
     */
    public T visit(DICollectionAttrValue collectionAttrValue);

}

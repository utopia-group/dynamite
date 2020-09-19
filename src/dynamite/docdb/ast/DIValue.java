package dynamite.docdb.ast;

/**
 * Document instance AST node for values.
 */
public abstract class DIValue {

    public abstract <T> T accept(IDIValueVisitor<T> visitor);

}

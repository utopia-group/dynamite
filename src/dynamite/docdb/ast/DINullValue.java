package dynamite.docdb.ast;

/**
 * Document instance AST node for null values.
 */
public final class DINullValue extends DIValue {

    private static DINullValue instance = null;

    private DINullValue() {
        if (instance != null) {
            throw new RuntimeException("Cannot access private constructor");
        }
    }

    /**
     * @return the singleton instance
     */
    public synchronized static DINullValue getInstance() {
        if (instance == null) {
            instance = new DINullValue();
        }
        return instance;
    }

    @Override
    public <T> T accept(IDIValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "null";
    }

}

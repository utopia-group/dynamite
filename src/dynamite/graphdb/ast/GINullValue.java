package dynamite.graphdb.ast;

public final class GINullValue extends GIValue {

    private static GINullValue instance = null;

    private GINullValue() {
        if (instance != null) {
            throw new RuntimeException("Cannot access private constructor");
        }
    }

    /**
     * @return the singleton instance
     */
    public synchronized static GINullValue getInstance() {
        if (instance == null) {
            instance = new GINullValue();
        }
        return instance;
    }

    @Override
    public <T> T accept(IGIValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "null";
    }

}

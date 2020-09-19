package dynamite.reldb.ast;

public final class RINullValue extends RIValue {

    private static RINullValue instance = null;

    private RINullValue() {
        if (instance != null) {
            throw new RuntimeException("Cannot access private constructor");
        }
    }

    /**
     * @return the singleton instance
     */
    public synchronized static RINullValue getInstance() {
        if (instance == null) {
            instance = new RINullValue();
        }
        return instance;
    }

    @Override
    public <T> T accept(IRIValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "null";
    }

}

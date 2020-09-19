package dynamite.datalog.ast;

public final class PlaceHolder extends DatalogExpression {

    private static PlaceHolder instance = null;

    private PlaceHolder() {
        if (instance != null) {
            throw new RuntimeException("Cannot access private constructor");
        }
    }

    /**
     * @return the singleton instance
     */
    public synchronized static PlaceHolder getInstance() {
        if (instance == null) {
            instance = new PlaceHolder();
        }
        return instance;
    }

    @Override
    public <T> T accept(IExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return "_";
    }

}

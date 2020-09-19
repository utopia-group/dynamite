package dynamite.datalog;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import dynamite.datalog.ast.DatalogExpression;

public final class Substitution {

    private final Map<DatalogExpression, DatalogExpression> oldToNewExprs;

    public Substitution(Map<DatalogExpression, DatalogExpression> identMap) {
        Objects.requireNonNull(identMap);
        this.oldToNewExprs = Collections.unmodifiableMap(identMap);
    }

    public Set<DatalogExpression> keySet() {
        return oldToNewExprs.keySet();
    }

    public boolean containsKey(DatalogExpression key) {
        return oldToNewExprs.containsKey(key);
    }

    public DatalogExpression get(DatalogExpression ident) {
        assert oldToNewExprs.containsKey(ident) : ident;
        return oldToNewExprs.get(ident);
    }

    @Override
    public String toString() {
        return oldToNewExprs.toString();
    }

}

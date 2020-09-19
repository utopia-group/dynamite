package dynamite.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.IntLiteral;
import dynamite.datalog.ast.StringLiteral;
import dynamite.util.MapUtils;

public final class EncodingMap {

    // Datalog expression -> integer value
    private final Map<DatalogExpression, Integer> exprToValueMap;
    // integer value -> Datalog identifier
    private final Map<Integer, DatalogExpression> valueToExprMap;
    // integer values for string or integer literals
    private final Set<Integer> literalIntegerValues;

    public EncodingMap(final Map<DatalogExpression, Integer> exprToValueMap) {
        Objects.requireNonNull(exprToValueMap);
        this.exprToValueMap = exprToValueMap;
        this.valueToExprMap = MapUtils.invertInjectiveMapToMap(exprToValueMap);
        this.literalIntegerValues = computeLiteralIntegerValues(exprToValueMap);
    }

    /**
     * Get the corresponding integer of the provided Datalog expression.
     *
     * @param expr the provided expression
     * @return the corresponding integer in the encoding map
     */
    public int getIntegerByExpr(DatalogExpression expr) {
        return exprToValueMap.get(expr);
    }

    /**
     * Check if the encoding map contains the provided integer.
     *
     * @param value the provided integer
     * @return {@code true} iff the encoding map contains the integer
     */
    public boolean containsInteger(int value) {
        return valueToExprMap.containsKey(value);
    }

    /**
     * Get the corresponding Datalog expression of the provided integer.
     *
     * @param value the provided integer in the co-domain of this encoding map
     * @return the Datalog expression that was encoded to the provided integer
     */
    public DatalogExpression getExprByInteger(int value) {
        return valueToExprMap.get(value);
    }

    /**
     * Check if an integer in the co-domain corresponds to a Datalog variable.
     *
     * @param value the provided integer
     * @return {@true} iff the integer corresponds to a Datalog variable
     */
    public boolean isVariableInteger(int value) {
        return !literalIntegerValues.contains(value);
    }

    /**
     * Check if an integer in the co-domain corresponds to a Datalog literal.
     *
     * @param value the provided integer
     * @return {@true} iff the integer corresponds to a Datalog literal
     */
    public boolean isLiteralInteger(int value) {
        return literalIntegerValues.contains(value);
    }

    private static Set<Integer> computeLiteralIntegerValues(Map<DatalogExpression, Integer> exprToValueMap) {
        Set<Integer> ret = new HashSet<>();
        for (DatalogExpression expr : exprToValueMap.keySet()) {
            if (expr instanceof StringLiteral || expr instanceof IntLiteral) {
                ret.add(exprToValueMap.get(expr));
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return valueToExprMap.keySet().stream()
                .sorted()
                .map(key -> String.format("%s -> %s", key, valueToExprMap.get(key).toSouffle()))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valueToExprMap);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof EncodingMap)) return false;
        EncodingMap o = (EncodingMap) obj;
        return Objects.equals(valueToExprMap, o.valueToExprMap);
    }

}

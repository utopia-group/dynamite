package dynamite.smt;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.util.ListMultiMap;
import dynamite.util.MapUtils;

/**
 * Data structure for SMT models.
 */
public final class SmtModel {

    // symbol -> value
    private final Map<String, Integer> symbolToValue;
    // value -> list of symbols
    private final ListMultiMap<Integer, String> valueToSymbolList;

    public SmtModel(final Map<String, Integer> symbolToValue) {
        Objects.requireNonNull(symbolToValue);
        this.symbolToValue = symbolToValue;
        this.valueToSymbolList = MapUtils.invertMapToListMultiMap(symbolToValue);
    }

    public int size() {
        return symbolToValue.size();
    }

    public Set<String> keySet() {
        return symbolToValue.keySet();
    }

    public boolean containsKey(String key) {
        return symbolToValue.containsKey(key);
    }

    public int getValue(String symbol) {
        return symbolToValue.get(symbol);
    }

    public boolean containsValue(int value) {
        return valueToSymbolList.containsKey(value);
    }

    public List<String> getKeysByValue(int value) {
        return valueToSymbolList.get(value);
    }

    @Override
    public String toString() {
        return symbolToValue.keySet().stream()
                .sorted()
                .map(symbol -> String.format("%s -> %s", symbol, symbolToValue.get(symbol)))
                .collect(Collectors.joining("\n"));
    }

}

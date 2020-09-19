package dynamite.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ListMultiMap<K, V> implements MultiMap<K, V> {

    public Map<K, List<V>> map = new LinkedHashMap<>();

    @Override
    public boolean put(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        return list.add(value);
    }

    @Override
    public boolean putAll(K key, Collection<V> values) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        return list.addAll(values);
    }

    @Override
    public List<V> get(K key) {
        return map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public boolean contains(K key, V value) {
        List<V> list = map.get(key);
        return list != null && list.contains(value);
    }

    @Override
    public int size() {
        int ret = 0;
        for (K key : map.keySet()) {
            ret += map.get(key).size();
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append(map.keySet().stream()
                .map(key -> String.format("%s -> %s", key, map.get(key)))
                .collect(Collectors.joining("\n")));
        builder.append("\n}");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ListMultiMap)) return false;
        @SuppressWarnings("unchecked")
        ListMultiMap<K, V> o = (ListMultiMap<K, V>) obj;
        return Objects.equals(map, o.map);
    }

}

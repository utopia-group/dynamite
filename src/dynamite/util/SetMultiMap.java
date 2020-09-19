package dynamite.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SetMultiMap<K, V> implements MultiMap<K, V> {

    public Map<K, Set<V>> map;

    public SetMultiMap() {
        this.map = new HashMap<>();
    }

    public SetMultiMap(SetMultiMap<K, V> other) {
        this.map = new HashMap<>();
        for (K key : other.keySet()) {
            Collection<V> set = other.get(key);
            Set<V> set1 = new HashSet<>(set);
            this.map.put(key, set1);
        }
    }

    @Override
    public boolean put(K key, V value) {
        Set<V> set = map.get(key);
        if (set == null) {
            set = new HashSet<>();
            map.put(key, set);
        }
        return set.add(value);
    }

    @Override
    public boolean putAll(K key, Collection<V> values) {
        Set<V> set = map.get(key);
        if (set == null) {
            set = new HashSet<>();
            map.put(key, set);
        }
        return set.addAll(values);
    }

    @Override
    public Set<V> get(K key) {
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
        Set<V> set = map.get(key);
        return set != null && set.contains(value);
    }

    @Override
    public int size() {
        int ret = 0;
        for (K key : map.keySet()) {
            ret += map.get(key).size();
        }
        return ret;
    }

    public static <K, V> SetMultiMap<K, V> unionSetMultiMaps(SetMultiMap<K, V> map1, SetMultiMap<K, V> map2) {
        SetMultiMap<K, V> ret = new SetMultiMap<K, V>(map1);
        for (K key : map2.keySet()) {
            ret.putAll(key, map2.get(key));
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
        if (!(obj instanceof SetMultiMap)) return false;
        @SuppressWarnings("unchecked")
        SetMultiMap<K, V> o = (SetMultiMap<K, V>) obj;
        return Objects.equals(map, o.map);
    }

}

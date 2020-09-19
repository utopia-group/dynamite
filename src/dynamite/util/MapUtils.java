package dynamite.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapUtils {

    /**
     * Invert a map to a list multimap.
     *
     * @param <K> key type of the original map
     * @param <V> value type of the original map
     * @param map the original map
     * @return the inverted multimap
     * @throws NullPointerException if the original map is {@code null}
     */
    public static <K, V> ListMultiMap<V, K> invertMapToListMultiMap(Map<K, V> map) {
        Objects.requireNonNull(map);
        ListMultiMap<V, K> ret = new ListMultiMap<>();
        for (K key : map.keySet()) {
            ret.put(map.get(key), key);
        }
        return ret;
    }

    /**
     * Invert an injective map to a map.
     *
     * @param <K> key type of the original map
     * @param <V> value type of the original map
     * @param map the original map
     * @return the inverted map
     * @throws IllegalArgumentException if the original map is not injective
     * @throws NullPointerException     if the original map is {@code null}
     */
    public static <K, V> Map<V, K> invertInjectiveMapToMap(Map<K, V> map) {
        Objects.requireNonNull(map);
        Map<V, K> ret = new HashMap<>();
        for (K key : map.keySet()) {
            V value = map.get(key);
            if (ret.containsKey(value)) {
                throw new IllegalArgumentException("Duplicated value: " + value);
            }
            ret.put(value, key);
        }
        assert ret.size() == map.size();
        return ret;
    }

    /**
     * Invert a multimap to a list multimap.
     *
     * @param <K>      key type of the original multimap
     * @param <V>      value type of the original multimap
     * @param multimap the original multimap
     * @return the inverted list multimap
     * @throws NullPointerException if the original multimap is {@code null}
     */
    public static <K, V> ListMultiMap<V, K> invertMultiMapToListMultiMap(MultiMap<K, V> multimap) {
        Objects.requireNonNull(multimap);
        ListMultiMap<V, K> ret = new ListMultiMap<>();
        for (K key : multimap.keySet()) {
            for (V value : multimap.get(key)) {
                ret.put(value, key);
            }
        }
        return ret;
    }

    /**
     * Safely put key-value pair to a map.
     *
     * @param <K>   key type
     * @param <V>   value type
     * @param map   the specified map
     * @param key   the specified key
     * @param value the specified value
     * @return {@code true} if the value is not replaced; {@code false} otherwise
     */
    public static <K, V> boolean safePut(Map<K, V> map, K key, V value) {
        boolean replaced = false;
        if (map.containsKey(key) && !map.get(key).equals(value)) {
            replaced = true;
        }
        map.put(key, value);
        return !replaced;
    }

}

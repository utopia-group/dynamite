package dynamite.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListUtils {

    /**
     * Build a new list where elements occur in the minuend but do not occur in the subtrahend.
     *
     * @param <T>        the element type
     * @param minuend    the list to be subtracted from
     * @param subtrahend the collection being subtracted
     * @return the difference list
     */
    public static <T> List<T> subtract(List<T> minuend, Collection<T> subtrahend) {
        List<T> ret = new ArrayList<>(minuend);
        ret.removeAll(subtrahend);
        return ret;
    }

    /**
     * Build a new list where elements occur exactly once in the provided list. The order of elements
     * in the returned list is the same as the provided list.
     *
     * @param <T>  the element type
     * @param list the provided list
     * @return a new list where elements occur exactly once in the provided list
     * @throws NullPointerException if the the provided list is {@code null}
     */
    public static <T> List<T> occurExactlyOnce(List<T> list) {
        Objects.requireNonNull(list);
        Map<T, Integer> elementToCount = new HashMap<>();
        for (T elem : list) {
            if (elementToCount.containsKey(elem)) {
                elementToCount.put(elem, elementToCount.get(elem) + 1);
            } else {
                elementToCount.put(elem, 1);
            }
        }
        List<T> ret = new ArrayList<>();
        for (T elem : list) {
            if (elementToCount.get(elem) == 1) {
                ret.add(elem);
            }
        }
        return ret;
    }

}

package dynamite.core;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dynamite.util.ListMultiMap;
import dynamite.util.MapUtils;

/**
 * Data structure for value correspondences.
 */
public final class ValueCorr {

    // target attribute name -> list of source attribute names
    private final ListMultiMap<String, String> tgtToSrc;
    // source attribute name -> list of target attribute names
    private final ListMultiMap<String, String> srcToTgt;
    // source attribute name -> list of source attribute names
    private final ListMultiMap<String, String> srcToSrc;
    // source attribute name -> list of constants
    private final ListMultiMap<String, String> srcToConsts;

    public ValueCorr(
            ListMultiMap<String, String> tgtToSrc,
            ListMultiMap<String, String> srcToSrc,
            ListMultiMap<String, String> srcToConsts) {
        Objects.requireNonNull(tgtToSrc);
        Objects.requireNonNull(srcToSrc);
        Objects.requireNonNull(srcToConsts);
        this.tgtToSrc = tgtToSrc;
        this.srcToTgt = MapUtils.invertMultiMapToListMultiMap(tgtToSrc);
        this.srcToSrc = srcToSrc;
        this.srcToConsts = srcToConsts;
    }

    public List<String> getSourceAttrsFromTarget(String tgtAttr) {
        assert tgtToSrc.containsKey(tgtAttr) : tgtAttr;
        return tgtToSrc.get(tgtAttr);
    }

    public boolean containsSrcToTgtKey(String srcAttr) {
        return srcToTgt.containsKey(srcAttr);
    }

    public List<String> getTargetAttrsFromSource(String srcAttr) {
        assert srcToTgt.containsKey(srcAttr) : srcAttr;
        return srcToTgt.get(srcAttr);
    }

    public boolean containsSrcToSrcKey(String key) {
        return srcToSrc.containsKey(key);
    }

    public List<String> getSourceAttrsFromSource(String srcAttr) {
        assert srcToSrc.containsKey(srcAttr) : srcAttr;
        return srcToSrc.get(srcAttr);
    }

    public boolean containsSrcToConstsKey(String key) {
        return srcToConsts.containsKey(key);
    }

    public List<String> getConstsFromSource(String srcAttr) {
        assert srcToConsts.containsKey(srcAttr) : srcAttr;
        return srcToConsts.get(srcAttr);
    }

    @Override
    public String toString() {
        Stream<String> stStr = tgtToSrc.keySet().stream()
                .sorted()
                .map(key -> String.format("%s <- %s", key, tgtToSrc.get(key)));
        Stream<String> ssStr = srcToSrc.keySet().stream()
                .sorted()
                .map(key -> String.format("%s <-> %s", key, srcToSrc.get(key)));
        Stream<String> scStr = srcToConsts.keySet().stream()
                .sorted()
                .map(key -> String.format("%s -> %s", key, srcToConsts.get(key)));
        return Stream.of(stStr, ssStr, scStr)
                .flatMap(Function.identity())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return Objects.hash(tgtToSrc, srcToSrc, srcToConsts);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ValueCorr)) return false;
        ValueCorr o = (ValueCorr) obj;
        return Objects.equals(tgtToSrc, o.tgtToSrc)
                && Objects.equals(srcToSrc, o.srcToSrc)
                && Objects.equals(srcToConsts, o.srcToConsts);
    }

}

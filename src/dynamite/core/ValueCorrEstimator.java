package dynamite.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dynamite.docdb.ValueCollectingVisitor;
import dynamite.docdb.ast.DICollection;
import dynamite.util.ListMultiMap;
import dynamite.util.SetMultiMap;

public final class ValueCorrEstimator {

    /**
     * Infer the value correspondence between a source schema and a target document
     * given their corresponding examples.
     *
     * @param srcInst       example of the source instance
     * @param tgtCollection example of the target document collection
     * @return the value correspondence
     */
    public static ValueCorr infer(IInstance srcInst, DICollection tgtCollection) {
        SetMultiMap<String, Object> srcAttrValues = srcInst.collectValuesByAttr();
        SetMultiMap<String, Object> tgtAttrValues = tgtCollection.accept(new ValueCollectingVisitor());
        ListMultiMap<String, String> tgtToSrc = inferTgtToSrcCorr(srcAttrValues, tgtAttrValues);
        ListMultiMap<String, String> srcToSrc = inferSrcToSrcCorr(srcAttrValues);
        ListMultiMap<String, String> srcToConsts = inferSrcToConsts(srcAttrValues);
        return new ValueCorr(tgtToSrc, srcToSrc, srcToConsts);
    }

    private static ListMultiMap<String, String> inferTgtToSrcCorr(
            SetMultiMap<String, Object> srcAttrValues,
            SetMultiMap<String, Object> tgtAttrValues) {
        ListMultiMap<String, String> ret = new ListMultiMap<>();
        for (String tgtAttr : tgtAttrValues.keySet()) {
            Set<Object> tgtValues = tgtAttrValues.get(tgtAttr);
            for (String srcAttr : srcAttrValues.keySet()) {
                Set<Object> srcValues = srcAttrValues.get(srcAttr);
                if (srcValues.containsAll(tgtValues)) {
                    ret.put(tgtAttr, srcAttr);
                }
            }
        }
        return ret;
    }

    private static ListMultiMap<String, String> inferSrcToSrcCorr(SetMultiMap<String, Object> srcAttrValues) {
        ListMultiMap<String, String> ret = new ListMultiMap<>();
        List<String> srcAttrs = new ArrayList<>(srcAttrValues.keySet());
        for (int i = 0; i < srcAttrs.size(); ++i) {
            String outerAttr = srcAttrs.get(i);
            Set<Object> outerValues = srcAttrValues.get(outerAttr);
            for (int j = i + 1; j < srcAttrs.size(); ++j) {
                String innerAttr = srcAttrs.get(j);
                Set<Object> innerValues = srcAttrValues.get(innerAttr);
                // NOTE: the aliasing relation is not symmetric
                if (outerValues.containsAll(innerValues)) {
                    ret.put(outerAttr, innerAttr);
                }
                if (innerValues.containsAll(outerValues)) {
                    ret.put(innerAttr, outerAttr);
                }
            }
        }
        return ret;
    }

    private static ListMultiMap<String, String> inferSrcToConsts(SetMultiMap<String, Object> srcAttrValues) {
        ListMultiMap<String, String> ret = new ListMultiMap<>();
        for (String srcAttr : srcAttrValues.keySet()) {
            for (Object value : srcAttrValues.get(srcAttr)) {
                if (keepsValue(value)) {
                    ret.put(srcAttr, valueToString(value));
                }
            }
        }
        return ret;
    }

    // TODO: Make constant list configurable from files
    private static boolean keepsValue(Object value) {
        if (!(value instanceof String)) return false;
        String str = (String) value;
        Set<String> dictionary = new HashSet<>(Arrays.asList(new String[] {
                "actor",
                "actress",
                "director",
                "producer",
                "writer",
        }));
        return dictionary.contains(str);
    }

    private static String valueToString(Object value) {
        if (!(value instanceof String || value instanceof Integer)) {
            throw new IllegalArgumentException("Unknown value type: " + value);
        }
        return value.toString();
    }

}

package dynamite.core;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.ExpressionHole;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.docdb.DocDbTestUtils;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.util.ListMultiMap;

public class SketchBodyGeneratorTest {

    private static String toSouffleString(List<? extends DatalogPredicate> list) {
        return list.stream()
                .map(pred -> pred.toSouffle())
                .collect(Collectors.joining("\n"));
    }

    private static ValueCorr buildValueCorr1() {
        ListMultiMap<String, String> tgtToSrc = new ListMultiMap<>();
        tgtToSrc.put("T?i", "Computers?cid");
        tgtToSrc.put("T?n", "Computers?name");
        tgtToSrc.put("T?m", "Computers?manufacturer");
        tgtToSrc.put("T?c", "Computers?catalog");
        tgtToSrc.put("T?p", "Computers?parts?value");
        ListMultiMap<String, String> srcToSrc = new ListMultiMap<>();
        ListMultiMap<String, String> srcToConsts = new ListMultiMap<>();
        return new ValueCorr(tgtToSrc, srcToSrc, srcToConsts);
    }

    @Test
    public void test1() {
        ValueCorr valueCorr = buildValueCorr1();
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        List<DSAtomicAttr> tgtAttrs = Arrays.asList(new DSAtomicAttr[] {
                new DSAtomicAttr("i", AttrType.INT),
                new DSAtomicAttr("n", AttrType.STRING),
                new DSAtomicAttr("m", AttrType.STRING),
                new DSAtomicAttr("c", AttrType.STRING),
                new DSAtomicAttr("p", AttrType.INT),
        });
        tgtAttrs.get(0).setCanonicalName("T?i");
        tgtAttrs.get(1).setCanonicalName("T?n");
        tgtAttrs.get(2).setCanonicalName("T?m");
        tgtAttrs.get(3).setCanonicalName("T?p");
        tgtAttrs.get(4).setCanonicalName("T?c");
        SketchBodyGenerator generator = new SketchBodyGenerator(valueCorr, schema, tgtAttrs, Integer.MAX_VALUE);
        List<DatalogPredicate> bodies = generator.genBodies();
        Assert.assertEquals(6, bodies.size());
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(0)).relation);
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(1)).relation);
        Assert.assertEquals("Computers?parts", ((RelationPredicate) bodies.get(2)).relation);
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(3)).relation);
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(4)).relation);
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(5)).relation);
    }

    private static ValueCorr buildValueCorr2() {
        ListMultiMap<String, String> tgtToSrc = new ListMultiMap<>();
        tgtToSrc.put("T?i", "Computers?cid");
        tgtToSrc.put("T?n", "Computers?name");
        tgtToSrc.put("T?n", "Computers?catalog");
        tgtToSrc.put("T?m", "Computers?manufacturer");
        tgtToSrc.put("T?c", "Computers?catalog");
        tgtToSrc.put("T?p", "Computers?parts?value");
        ListMultiMap<String, String> srcToSrc = new ListMultiMap<>();
        ListMultiMap<String, String> srcToConsts = new ListMultiMap<>();
        return new ValueCorr(tgtToSrc, srcToSrc, srcToConsts);
    }

    @Test
    public void test2() {
        ValueCorr valueCorr = buildValueCorr2();
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        List<DSAtomicAttr> tgtAttrs = Arrays.asList(new DSAtomicAttr[] {
                new DSAtomicAttr("i", AttrType.INT),
                new DSAtomicAttr("n", AttrType.STRING),
                new DSAtomicAttr("m", AttrType.STRING),
                new DSAtomicAttr("c", AttrType.STRING),
                new DSAtomicAttr("p", AttrType.INT),
        });
        tgtAttrs.get(0).setCanonicalName("T?i");
        tgtAttrs.get(1).setCanonicalName("T?n");
        tgtAttrs.get(2).setCanonicalName("T?m");
        tgtAttrs.get(3).setCanonicalName("T?p");
        tgtAttrs.get(4).setCanonicalName("T?c");
        SketchBodyGenerator generator = new SketchBodyGenerator(valueCorr, schema, tgtAttrs, Integer.MAX_VALUE);
        List<DatalogPredicate> bodies = generator.genBodies();
        Assert.assertEquals(7, bodies.size());
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(0)).relation);
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(1)).relation);
        Assert.assertEquals("Computers?parts", ((RelationPredicate) bodies.get(2)).relation);
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(3)).relation);
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(4)).relation);
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(5)).relation);
        Assert.assertEquals("Computers", ((RelationPredicate) bodies.get(6)).relation);
    }

    private static ValueCorr buildValueCorr3() {
        ListMultiMap<String, String> tgtToSrc = new ListMultiMap<>();
        tgtToSrc.put("T?aa1", "A?a1");
        tgtToSrc.put("T?bb1", "A?B?b1");
        tgtToSrc.put("T?cc1", "A?B?C?c1");
        tgtToSrc.put("T?cc2", "A?B?C?c2");
        ListMultiMap<String, String> srcToSrc = new ListMultiMap<>();
        srcToSrc.put("A?B?C?c1", "A?B?C?c2");
        srcToSrc.put("A?B?C?c2", "A?B?C?c1");
        ListMultiMap<String, String> srcToConsts = new ListMultiMap<>();
        return new ValueCorr(tgtToSrc, srcToSrc, srcToConsts);
    }

    @Test
    public void test3() {
        ValueCorr valueCorr = buildValueCorr3();
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        List<DSAtomicAttr> tgtAttrs = Arrays.asList(new DSAtomicAttr[] {
                new DSAtomicAttr("aa1", AttrType.INT),
                new DSAtomicAttr("bb1", AttrType.INT),
                new DSAtomicAttr("cc1", AttrType.INT),
                new DSAtomicAttr("cc2", AttrType.INT),
        });
        tgtAttrs.get(0).setCanonicalName("T?aa1");
        tgtAttrs.get(1).setCanonicalName("T?bb1");
        tgtAttrs.get(2).setCanonicalName("T?cc1");
        tgtAttrs.get(3).setCanonicalName("T?cc2");
        SketchBodyGenerator generator = new SketchBodyGenerator(valueCorr, schema, tgtAttrs, Integer.MAX_VALUE);
        List<DatalogPredicate> bodies = generator.genBodies();
        Assert.assertEquals(9, bodies.size());
        Assert.assertEquals("A", ((RelationPredicate) bodies.get(0)).relation);
        Assert.assertEquals("A?B", ((RelationPredicate) bodies.get(1)).relation);
        Assert.assertEquals("A?B?C", ((RelationPredicate) bodies.get(2)).relation);
        Assert.assertEquals("A", ((RelationPredicate) bodies.get(3)).relation);
        Assert.assertEquals("A?B", ((RelationPredicate) bodies.get(4)).relation);
        Assert.assertEquals("A?B?C", ((RelationPredicate) bodies.get(5)).relation);
        Assert.assertEquals("A", ((RelationPredicate) bodies.get(6)).relation);
        Assert.assertEquals("A?B", ((RelationPredicate) bodies.get(7)).relation);
        Assert.assertEquals("A", ((RelationPredicate) bodies.get(8)).relation);
        ExpressionHole h0 = (ExpressionHole) ((RelationPredicate) bodies.get(0)).arguments.get(0);
        ExpressionHole h1 = (ExpressionHole) ((RelationPredicate) bodies.get(1)).arguments.get(1);
        ExpressionHole h2 = (ExpressionHole) ((RelationPredicate) bodies.get(2)).arguments.get(1);
        ExpressionHole h3 = (ExpressionHole) ((RelationPredicate) bodies.get(2)).arguments.get(2);
        Assert.assertEquals("?h0 {v_A?a1_0, v_A?a1_1, v_A?a1_2, v_A?a1_3, v_T?aa1}", h0.toSouffle());
        Assert.assertEquals("?h1 {v_A?B?b1_0, v_A?B?b1_1, v_A?B?b1_2, v_T?bb1}", h1.toSouffle());
        Assert.assertEquals("?h2 {v_A?B?C?c1_0, v_A?B?C?c1_1, v_A?B?C?c2_0, v_A?B?C?c2_1, v_T?cc1}", h2.toSouffle());
        Assert.assertEquals("?h3 {v_A?B?C?c2_0, v_A?B?C?c2_1, v_A?B?C?c1_0, v_A?B?C?c1_1, v_T?cc2}", h3.toSouffle());
    }

    private static ValueCorr buildValueCorr4() {
        ListMultiMap<String, String> tgtToSrc = new ListMultiMap<>();
        tgtToSrc.put("T?c1", "A?a1");
        tgtToSrc.put("T?c2", "A?a2");
        tgtToSrc.put("T?c3", "B?b1");
        tgtToSrc.put("T?c4", "B?b2");
        ListMultiMap<String, String> srcToSrc = new ListMultiMap<>();
        ListMultiMap<String, String> srcToConsts = new ListMultiMap<>();
        return new ValueCorr(tgtToSrc, srcToSrc, srcToConsts);
    }

    @Test
    public void test4() {
        ValueCorr valueCorr = buildValueCorr4();
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        List<DSAtomicAttr> tgtAttrs = Arrays.asList(new DSAtomicAttr[] {
                new DSAtomicAttr("c1", AttrType.INT),
                new DSAtomicAttr("c2", AttrType.INT),
                new DSAtomicAttr("c3", AttrType.INT),
                new DSAtomicAttr("c4", AttrType.INT),
        });
        tgtAttrs.get(0).setCanonicalName("T?c1");
        tgtAttrs.get(1).setCanonicalName("T?c2");
        tgtAttrs.get(2).setCanonicalName("T?c3");
        tgtAttrs.get(3).setCanonicalName("T?c4");
        SketchBodyGenerator generator = new SketchBodyGenerator(valueCorr, schema, tgtAttrs, Integer.MAX_VALUE);
        String expected = ""
                + "B(?h0 {v_B?b1_0, v_B?b1_1, v_T?c3}, ?h1 {v_B?b2_0, v_B?b2_1, v_T?c4})\n"
                + "B(?h2 {v_B?b1_0, v_B?b1_1, v_T?c3}, ?h3 {v_B?b2_0, v_B?b2_1, v_T?c4})\n"
                + "A(?h4 {v_A?a1_0, v_A?a1_1, v_T?c1}, ?h5 {v_A?a2_0, v_A?a2_1, v_T?c2})\n"
                + "A(?h6 {v_A?a1_0, v_A?a1_1, v_T?c1}, ?h7 {v_A?a2_0, v_A?a2_1, v_T?c2})";
        Assert.assertEquals(expected, toSouffleString(generator.genBodies()));
    }

}

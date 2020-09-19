package dynamite.core;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogPredicate;
import dynamite.docdb.DocDbTestUtils;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DocumentSchema;

public class SketchHeadGeneratorTest {

    private static String toSouffleString(List<? extends DatalogPredicate> list) {
        return list.stream()
                .map(pred -> pred.toSouffle())
                .collect(Collectors.joining("\n"));
    }

    @Test
    public void test1() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DSCollection computer = schema.collections.get(0);
        SketchHeadGenerator generator = new SketchHeadGenerator();
        computer.accept(generator);
        String expectedHeadStr = ""
                + "Computers?parts(_v_0, v_Computers?parts?value)\n"
                + "Computers(v_Computers?cid, v_Computers?name, v_Computers?manufacturer, v_Computers?catalog, _v_0)";
        Assert.assertEquals(expectedHeadStr, toSouffleString(generator.getHeads()));
        String expectedPredStr = "_v_0 = cat(\"#\", cat(v_Computers?cid, cat(\"#\", cat(v_Computers?name, cat(\"#\", cat(v_Computers?manufacturer, cat(\"#\", cat(v_Computers?catalog, \"#\"))))))))";
        Assert.assertEquals(expectedPredStr, toSouffleString(generator.getInjectivePredicates()));
    }

    @Test
    public void test2() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        DSCollection computer = schema.collections.get(0);
        SketchHeadGenerator generator = new SketchHeadGenerator();
        computer.accept(generator);
        String expectedHeadStr = ""
                + "Computers?parts(_v_0, v_Computers?parts?value)\n"
                + "Computers(v_Computers?cid, v_Computers?name, _v_0, v_Computers?catalog, v_Computers?manufacturer)";
        Assert.assertEquals(expectedHeadStr, toSouffleString(generator.getHeads()));
        String expectedPredStr = "_v_0 = cat(\"#\", cat(v_Computers?cid, cat(\"#\", cat(v_Computers?name, cat(\"#\", cat(v_Computers?catalog, cat(\"#\", cat(v_Computers?manufacturer, \"#\"))))))))";
        Assert.assertEquals(expectedPredStr, toSouffleString(generator.getInjectivePredicates()));
    }

    @Test
    public void test3() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DSCollection collectionA = schema.collections.get(0);
        SketchHeadGenerator generator = new SketchHeadGenerator();
        collectionA.accept(generator);
        String expectedHeadStr = ""
                + "A?B?C(_v_1, v_A?B?C?c1, v_A?B?C?c2)\n"
                + "A?B(_v_0, v_A?B?b1, _v_1)\n"
                + "A(v_A?a1, _v_0)";
        Assert.assertEquals(expectedHeadStr, toSouffleString(generator.getHeads()));
        String expectedPredStr = ""
                + "_v_0 = cat(\"#\", cat(v_A?a1, \"#\"))\n"
                + "_v_1 = cat(\"#\", cat(_v_0, cat(\"#\", cat(v_A?B?b1, \"#\"))))";
        Assert.assertEquals(expectedPredStr, toSouffleString(generator.getInjectivePredicates()));
    }

    @Test
    public void test4() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        DSCollection collectionA = schema.collections.get(0);
        {
            SketchHeadGenerator generator = new SketchHeadGenerator();
            collectionA.accept(generator);
            String expectedHeadStr = "A(v_A?a1, v_A?a2)";
            Assert.assertEquals(expectedHeadStr, toSouffleString(generator.getHeads()));
            String expectedPredStr = "";
            Assert.assertEquals(expectedPredStr, toSouffleString(generator.getInjectivePredicates()));
        }
        DSCollection collectionB = schema.collections.get(1);
        {
            SketchHeadGenerator generator = new SketchHeadGenerator();
            collectionB.accept(generator);
            String expectedHeadStr = "B(v_B?b1, v_B?b2)";
            Assert.assertEquals(expectedHeadStr, toSouffleString(generator.getHeads()));
            String expectedPredStr = "";
            Assert.assertEquals(expectedPredStr, toSouffleString(generator.getInjectivePredicates()));
        }
    }

}

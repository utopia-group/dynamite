package dynamite.core;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DocumentSchema;

public class SketchGeneratorTest {

    @Test()
    public void test1() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema1();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema1();
        DSCollection tgtCollection = tgtSchema.collections.get(0);
        ValueCorr valueCorr = CoreTestUtils.buildValueCorr1();
        InstanceExample example = CoreTestUtils.buildExample1();
        Sketch sketch = SketchGenerator.generate(valueCorr, example.sourceInstance, srcSchema, tgtCollection);
        String expected = ""
                + ".type IntAttr\n"
                + ".type StrAttr\n"
                + ".type Rel\n"
                + ".decl S(a: StrAttr, b: StrAttr)\n"
                + ".decl T(a: StrAttr, b: StrAttr)\n"
                + ".output T(delimiter=\"\\t\")\n"
                + "\n"
                + "T(v_T?a, v_T?b) :-\n"
                + "    S(?h0 {v_S?a_0, v_S?a_1, v_S?a_2, v_T?b}, ?h1 {v_S?b_0, v_S?b_1, v_S?b_2, v_S?a_0, v_S?a_1, v_S?a_2, v_T?a, v_T?b}),\n"
                + "    S(?h2 {v_S?a_0, v_S?a_1, v_S?a_2, v_T?b}, ?h3 {v_S?b_0, v_S?b_1, v_S?b_2, v_S?a_0, v_S?a_1, v_S?a_2, v_T?a, v_T?b}),\n"
                + "    S(?h4 {v_S?a_0, v_S?a_1, v_S?a_2, v_T?b}, ?h5 {v_S?b_0, v_S?b_1, v_S?b_2, v_S?a_0, v_S?a_1, v_S?a_2, v_T?a, v_T?b}).\n"
                + "\n"
                + "S(\"1\", \"1\").\n"
                + "S(\"1\", \"2\").\n"
                + "S(\"1\", \"3\").\n";
        Assert.assertEquals(expected, sketch.toString());
    }

}

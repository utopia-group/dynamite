package dynamite.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.smt.SmtModel;

public class DatalogSubstituterTest {

    /**
     * h0 -> 2 (v_T?b)
     * h1 -> 3 (v_T?a)
     *
     * @return the model
     */
    private static SmtModel buildModel01() {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put("h0", 2);
        map.put("h1", 3);
        return new SmtModel(map);
    }

    @Test
    public void testSketch01() {
        Sketch sketch = CoreTestUtils.buildSketch01();
        String originalSketchStr = sketch.toString();
        SmtModel model = buildModel01();
        EncodingMap encodingMap = CoreTestUtils.buildEncodingMap01();
        DatalogProgram substituted = sketch.program.accept(new DatalogSubstituter(model, encodingMap));
        String expectedProgram = ""
                + ".type IntAttr\n"
                + ".type StrAttr\n"
                + ".type Rel\n"
                + ".decl S(a: StrAttr, b: StrAttr)\n"
                + ".decl T(a: StrAttr, b: StrAttr)\n"
                + ".output T(delimiter=\"\\t\")\n"
                + "\n"
                + "T(v_T?a, v_T?b) :-\n"
                + "    S(v_T?b, v_T?a).\n"
                + "\n"
                + "S(\"1\", \"1\").\n"
                + "S(\"1\", \"2\").\n"
                + "S(\"1\", \"3\").\n";
        Assert.assertEquals(expectedProgram, substituted.toSouffle());
        // check that the sketch itself is not modified
        Assert.assertEquals(originalSketchStr, sketch.toString());
    }

}

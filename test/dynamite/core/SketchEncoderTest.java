package dynamite.core;

import org.junit.Assert;
import org.junit.Test;

import dynamite.smt.ast.Constraint;

public class SketchEncoderTest {

    @Test
    public void test1() {
        Sketch sketch = CoreTestUtils.buildSketch01();
        SketchEncoder encoder = new SketchEncoder(sketch);
        EncodingMap encodingMap = encoder.getEncodingMap();
        EncodingMap expectedEncodingMap = CoreTestUtils.buildEncodingMap01();
        Assert.assertEquals(expectedEncodingMap, encodingMap);
        Constraint cstr = encoder.encode();
        String expected = ""
                + "or(h0 = 0, h0 = 1, h0 = 2)\n"
                + "or(h1 = 1, h1 = 0, h1 = 3)\n"
                + "or(not(isVar(h0)), h0 = 0, h0 = 1, h0 = 2)\n"
                + "or(isVar(h0))\n"
                + "or(not(isVar(h1)), h1 = 1, h1 = 0, h1 = 3)\n"
                + "or(isVar(h1))\n"
                + "or(h1 = 3)\n"
                + "or(h0 = 2)";
        Assert.assertEquals(expected, cstr.toString());
    }

    @Test
    public void test0() {
        Sketch sketch = CoreTestUtils.buildSketch0();
        SketchEncoder encoder = new SketchEncoder(sketch);
        Constraint cstr = encoder.encode();
        String expected = ""
                + "or(h1 = 0, h1 = 1, h1 = 2, h1 = 3)\n"
                + "or(h2 = 4, h2 = 5, h2 = 6, h2 = 7)\n"
                + "or(h3 = 0, h3 = 1, h3 = 2, h3 = 3)\n"
                + "or(h4 = 8, h4 = 9, h4 = 10)\n"
                + "or(h5 = 0, h5 = 1, h5 = 2, h5 = 3)\n"
                + "or(h6 = 4, h6 = 5, h6 = 6, h6 = 7)\n"
                + "or(h7 = 0, h7 = 1, h7 = 2, h7 = 3)\n"
                + "or(h8 = 8, h8 = 9, h8 = 10)\n"
                + "or(not(isVar(h1)), h1 = 0, h1 = 1, h1 = 2, h1 = 3)\n"
                + "or(isVar(h1))\n"
                + "or(not(isVar(h2)), h2 = 4, h2 = 5, h2 = 6, h2 = 7)\n"
                + "or(isVar(h2))\n"
                + "or(not(isVar(h3)), h3 = 0, h3 = 1, h3 = 2, h3 = 3)\n"
                + "or(isVar(h3))\n"
                + "or(not(isVar(h4)), h4 = 8, h4 = 9, h4 = 10)\n"
                + "or(isVar(h4))\n"
                + "or(not(isVar(h5)), h5 = 0, h5 = 1, h5 = 2, h5 = 3)\n"
                + "or(isVar(h5))\n"
                + "or(not(isVar(h6)), h6 = 4, h6 = 5, h6 = 6, h6 = 7)\n"
                + "or(isVar(h6))\n"
                + "or(not(isVar(h7)), h7 = 0, h7 = 1, h7 = 2, h7 = 3)\n"
                + "or(isVar(h7))\n"
                + "or(not(isVar(h8)), h8 = 8, h8 = 9, h8 = 10)\n"
                + "or(isVar(h8))\n"
                + "or(h2 = 4, h6 = 4)\n"
                + "or(h2 = 5, h6 = 5)\n"
                + "or(h4 = 8, h8 = 8)";
        Assert.assertEquals(expected, cstr.toString());
    }

}

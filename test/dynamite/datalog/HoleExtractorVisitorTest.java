package dynamite.datalog;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.core.CoreTestUtils;
import dynamite.core.Sketch;
import dynamite.datalog.ast.ExpressionHole;

public class HoleExtractorVisitorTest {

    @Test
    public void test1() {
        Sketch sketch = CoreTestUtils.buildSketch01();
        List<ExpressionHole> holes = sketch.program.accept(new HoleExtractionVisitor());
        Assert.assertEquals(2, holes.size());
        Assert.assertEquals("?h0 {v_S?a_0, v_S?b_0, v_T?b}", holes.get(0).toSouffle());
        Assert.assertEquals("?h1 {v_S?b_0, v_S?a_0, v_T?a}", holes.get(1).toSouffle());
    }

    @Test
    public void test0() {
        Sketch sketch = CoreTestUtils.buildSketch0();
        List<ExpressionHole> holes = sketch.program.accept(new HoleExtractionVisitor());
        Assert.assertEquals(8, holes.size());
        Assert.assertEquals("?h1 {id1, id2, fid1, fid2}", holes.get(0).toSouffle());
        Assert.assertEquals("?h2 {n1, n2, name1, name2}", holes.get(1).toSouffle());
        Assert.assertEquals("?h3 {id1, id2, fid1, fid2}", holes.get(2).toSouffle());
        Assert.assertEquals("?h4 {y, years1, years2}", holes.get(3).toSouffle());
        Assert.assertEquals("?h5 {id1, id2, fid1, fid2}", holes.get(4).toSouffle());
        Assert.assertEquals("?h6 {n1, n2, name1, name2}", holes.get(5).toSouffle());
        Assert.assertEquals("?h7 {id1, id2, fid1, fid2}", holes.get(6).toSouffle());
        Assert.assertEquals("?h8 {y, years1, years2}", holes.get(7).toSouffle());
    }

}

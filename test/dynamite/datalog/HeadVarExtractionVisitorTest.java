package dynamite.datalog;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.Identifier;

public class HeadVarExtractionVisitorTest {

    @Test
    public void test1() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram1();
        List<Identifier> headVars = program.accept(new HeadVarExtractionVisitor());
        Assert.assertEquals(2, headVars.size());
        Assert.assertEquals("x", headVars.get(0).toSouffle());
        Assert.assertEquals("y", headVars.get(1).toSouffle());
    }

    @Test
    public void test2() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram2();
        List<Identifier> headVars = program.accept(new HeadVarExtractionVisitor());
        Assert.assertEquals(2, headVars.size());
        Assert.assertEquals("x", headVars.get(0).toSouffle());
        Assert.assertEquals("z", headVars.get(1).toSouffle());
    }

}

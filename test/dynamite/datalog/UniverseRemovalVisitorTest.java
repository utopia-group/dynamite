package dynamite.datalog;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogProgram;

public class UniverseRemovalVisitorTest {

    @Test
    public void test1() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram1();
        DatalogProgram progWithUniv = program.accept(new UniverseGenVisitor());
        DatalogProgram progWithoutUniv = progWithUniv.accept(new UniverseRemovalVisitor());
        Assert.assertEquals(program.toSouffle(), progWithoutUniv.toSouffle());
    }

    @Test
    public void test2() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram2();
        DatalogProgram progWithUniv = program.accept(new UniverseGenVisitor());
        DatalogProgram progWithoutUniv = progWithUniv.accept(new UniverseRemovalVisitor());
        Assert.assertEquals(program.toSouffle(), progWithoutUniv.toSouffle());
    }

    @Test
    public void test3() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram3();
        DatalogProgram progWithUniv = program.accept(new UniverseGenVisitor());
        DatalogProgram progWithoutUniv = progWithUniv.accept(new UniverseRemovalVisitor());
        Assert.assertEquals(program.toSouffle(), progWithoutUniv.toSouffle());
    }

}

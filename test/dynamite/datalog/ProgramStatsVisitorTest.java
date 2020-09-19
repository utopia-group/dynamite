package dynamite.datalog;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogProgram;

public class ProgramStatsVisitorTest {

    @Test
    public void test1() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram1();
        ProgramStatsVisitor visitor = new ProgramStatsVisitor();
        program.accept(visitor);
        Assert.assertEquals(1, visitor.getRuleCount());
        Assert.assertEquals(1, visitor.getPredCount());
    }

    @Test
    public void test2() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram2();
        ProgramStatsVisitor visitor = new ProgramStatsVisitor();
        program.accept(visitor);
        Assert.assertEquals(1, visitor.getRuleCount());
        Assert.assertEquals(2, visitor.getPredCount());
    }

    @Test
    public void test3() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram3();
        ProgramStatsVisitor visitor = new ProgramStatsVisitor();
        program.accept(visitor);
        Assert.assertEquals(2, visitor.getRuleCount());
        Assert.assertEquals(2, visitor.getPredCount());
    }

}

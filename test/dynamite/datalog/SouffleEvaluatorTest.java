package dynamite.datalog;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogProgram;

public class SouffleEvaluatorTest {

    @Test
    public void testProgram1() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram1();
        SouffleEvaluator souffle = new SouffleEvaluator();
        DatalogOutput actual = souffle.evaluate(program);
        DatalogOutput expected = DatalogTestUtils.buildDatalogOutput1();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testProgram2() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram2();
        SouffleEvaluator souffle = new SouffleEvaluator();
        DatalogOutput actual = souffle.evaluate(program);
        DatalogOutput expected = DatalogTestUtils.buildDatalogOutput2();
        Assert.assertEquals(expected, actual);
    }
}

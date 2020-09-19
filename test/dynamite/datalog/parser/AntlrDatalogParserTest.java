package dynamite.datalog.parser;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.DatalogTestUtils;
import dynamite.datalog.ast.DatalogProgram;

public class AntlrDatalogParserTest {

    @Test
    public void test1() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram1();
        String progText = program.toSouffle();
        DatalogProgram programFromParser = AntlrDatalogParser.parse(progText);
        String progTextFromParser = programFromParser.toSouffle();
        Assert.assertEquals(progText, progTextFromParser);
    }

    @Test
    public void test2() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram2();
        String progText = program.toSouffle();
        DatalogProgram programFromParser = AntlrDatalogParser.parse(progText);
        String progTextFromParser = programFromParser.toSouffle();
        Assert.assertEquals(progText, progTextFromParser);
    }

    @Test
    public void test3() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram3();
        String progText = program.toSouffle();
        DatalogProgram programFromParser = AntlrDatalogParser.parse(progText);
        String progTextFromParser = programFromParser.toSouffle();
        Assert.assertEquals(progText, progTextFromParser);
    }

    @Test
    public void test4() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram4();
        String progText = program.toSouffle();
        DatalogProgram programFromParser = AntlrDatalogParser.parse(progText);
        String progTextFromParser = programFromParser.toSouffle();
        Assert.assertEquals(progText, progTextFromParser);
    }
}

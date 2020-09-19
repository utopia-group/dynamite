package dynamite.reldb;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogFact;
import dynamite.reldb.ast.RelationalInstance;

public class RelFactGenVisitorTest {

    private static String toSouffle(List<DatalogFact> facts) {
        return facts.stream()
                .map(DatalogFact::toSouffle)
                .collect(Collectors.joining("\n"));
    }

    @Test
    public void test1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstance1();
        List<DatalogFact> actual = instance.accept(new RelFactGenVisitor());
        String expected = ""
                + "R(\"1\", \"2\", \"s1\", \"s2\").\n"
                + "R(\"3\", \"4\", \"s3\", \"s4\").";
        Assert.assertEquals(expected, toSouffle(actual));
    }

    @Test
    public void test2() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstance2();
        List<DatalogFact> actual = instance.accept(new RelFactGenVisitor());
        String expected = ""
                + "A(\"1\", \"2\").\n"
                + "A(\"3\", \"4\").\n"
                + "B(\"5\", \"6\").\n"
                + "B(\"7\", \"8\").";
        Assert.assertEquals(expected, toSouffle(actual));
    }

    @Test
    public void testFloat1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstanceFloat1();
        List<DatalogFact> actual = instance.accept(new RelFactGenVisitor());
        String expected = "A(\"100.5\").";
        Assert.assertEquals(expected, toSouffle(actual));
    }

    @Test
    public void testNull1() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstanceNull1();
        List<DatalogFact> actual = instance.accept(new RelFactGenVisitor());
        String expected = "A(\"1\", \"null\").";
        Assert.assertEquals(expected, toSouffle(actual));
    }

}

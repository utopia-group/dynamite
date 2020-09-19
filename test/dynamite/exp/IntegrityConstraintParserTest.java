package dynamite.exp;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

public class IntegrityConstraintParserTest {

    @Test
    public void test1() {
        String text = "a -> b";
        IntegrityConstraint constraint = IntegrityConstraintParser.parse(text);
        Assert.assertEquals(text, constraint.toString());
    }

    @Test
    public void test2() {
        String text = Stream.of(
                "a -> c",
                "a?b -> d")
                .collect(Collectors.joining("\n"));
        IntegrityConstraint constraint = IntegrityConstraintParser.parse(text);
        Assert.assertEquals(text, constraint.toString());
    }

}

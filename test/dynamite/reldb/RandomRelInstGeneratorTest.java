package dynamite.reldb;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import dynamite.exp.IntegrityConstraint;
import dynamite.reldb.ast.RelationalInstance;
import dynamite.reldb.ast.RelationalSchema;

public class RandomRelInstGeneratorTest {

    public static final long RANDOM_SEED = 1;

    @Test
    public void test1() {
        int size = 1;
        RelationalSchema schema = RelDbTestUtils.buildRelationalSchema1();
        IntegrityConstraint constraint = new IntegrityConstraint(Collections.emptyList());
        RandomRelInstGenVisitor visitor = new RandomRelInstGenVisitor(size, constraint, RANDOM_SEED);
        RelationalInstance actual = schema.accept(visitor);
        RelationalInstance expected = RelInstanceParser.parse(""
                + "R(a: Int, b: Int, c: String, d: String)\n"
                + "985, 588, \"s847\", \"s313\"\n");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        int size = 3;
        RelationalSchema schema = RelDbTestUtils.buildRelationalSchema2();
        IntegrityConstraint constraint = new IntegrityConstraint(Collections.emptyList());
        RandomRelInstGenVisitor visitor = new RandomRelInstGenVisitor(size, constraint, RANDOM_SEED);
        RelationalInstance actual = schema.accept(visitor);
        System.out.println(actual.toInstanceString());
        RelationalInstance expected = RelInstanceParser.parse(""
                + "A(a1: Int, a2: Int)\n"
                + "985, 588\n"
                + "847, 313\n"
                + "254, 904\n"
                + "B(b1: Int, b2: Int)\n"
                + "434, 606\n"
                + "978, 748\n"
                + "569, 473\n");
        Assert.assertEquals(expected, actual);
    }

}

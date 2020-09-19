package dynamite.docdb;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.exp.IntegrityConstraint;

public class RandomDocInstGenVisitorTest {

    public static final long RANDOM_SEED = 1;

    @Test
    public void test1() {
        int size = 1;
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        IntegrityConstraint constraint = new IntegrityConstraint(Collections.emptyList());
        RandomDocInstGenVisitor visitor = new RandomDocInstGenVisitor(size, constraint, RANDOM_SEED);
        DocumentInstance actual = schema.accept(visitor);
        String expectedText = ""
                + "{"
                + "    \"Computers\": ["
                + "        {"
                + "            \"catalog\": \"s313\","
                + "            \"cid\": 985,"
                + "            \"manufacturer\": \"s847\","
                + "            \"name\": \"s588\","
                + "            \"parts\": ["
                + "                {"
                + "                    \"value\": 254"
                + "                }"
                + "            ]"
                + "        }"
                + "    ]"
                + "}";
        DocumentInstance expected = DocJsonInstanceParser.parse(expectedText, schema);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
        int size = 2;
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        IntegrityConstraint constraint = new IntegrityConstraint(Collections.emptyList());
        RandomDocInstGenVisitor visitor = new RandomDocInstGenVisitor(size, constraint, RANDOM_SEED);
        DocumentInstance actual = schema.accept(visitor);
        String expectedText = "{\"A\":[{\"a1\":985,\"B\":[{\"b1\":588,\"C\":[{\"c1\":847,\"c2\":313}]}]},"
                + "{\"a1\":254,\"B\":[{\"b1\":904,\"C\":[{\"c1\":434,\"c2\":606}]}]}]}\n";
        DocumentInstance expected = DocJsonInstanceParser.parse(expectedText, schema);
        Assert.assertEquals(expected, actual);
    }

}

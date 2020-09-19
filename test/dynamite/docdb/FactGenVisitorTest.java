package dynamite.docdb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.docdb.ast.DocumentInstance;

public class FactGenVisitorTest {

    private static String toSouffleString(List<DatalogFact> facts) {
        return facts.stream()
                .map(DatalogFact::toSouffle)
                .collect(Collectors.joining("\n"));
    }

    @Test
    public void testInstance1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance1();
        List<DatalogFact> actual = instance.accept(new FactGenVisitor());
        List<DatalogFact> expected = Arrays.asList(new DatalogFact[] {
                new DatalogFact(new RelationPredicate("Computers?parts", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("#100#C1#M1#C2#"), new StringLiteral("101")
                }))),
                new DatalogFact(new RelationPredicate("Computers?parts", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("#100#C1#M1#C2#"), new StringLiteral("102")
                }))),
                new DatalogFact(new RelationPredicate("Computers", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("100"), new StringLiteral("C1"), new StringLiteral("M1"),
                        new StringLiteral("C2"), new StringLiteral("#100#C1#M1#C2#")
                }))),
        });
        Assert.assertEquals(toSouffleString(expected), toSouffleString(actual));
    }

    @Test
    public void testInstance2() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance2();
        List<DatalogFact> actual = instance.accept(new FactGenVisitor());
        List<DatalogFact> expected = Arrays.asList(new DatalogFact[] {
                new DatalogFact(new RelationPredicate("Computers?parts", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("#100#C1#C2#M1#"), new StringLiteral("101")
                }))),
                new DatalogFact(new RelationPredicate("Computers?parts", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("#100#C1#C2#M1#"), new StringLiteral("102")
                }))),
                new DatalogFact(new RelationPredicate("Computers", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("100"), new StringLiteral("C1"), new StringLiteral("#100#C1#C2#M1#"),
                        new StringLiteral("C2"), new StringLiteral("M1")
                }))),
        });
        Assert.assertEquals(toSouffleString(expected), toSouffleString(actual));
    }

    @Test
    public void testInstance3() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance3();
        List<DatalogFact> actual = instance.accept(new FactGenVisitor());
        List<DatalogFact> expected = Arrays.asList(new DatalogFact[] {
                new DatalogFact(new RelationPredicate("A?B?C", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("##100##200#"), new StringLiteral("300"), new StringLiteral("400")
                }))),
                new DatalogFact(new RelationPredicate("A?B", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("#100#"), new StringLiteral("200"), new StringLiteral("##100##200#")
                }))),
                new DatalogFact(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("100"), new StringLiteral("#100#")
                }))),
        });
        Assert.assertEquals(toSouffleString(expected), toSouffleString(actual));
    }

    @Test
    public void testInstance4() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance4();
        List<DatalogFact> actual = instance.accept(new FactGenVisitor());
        List<DatalogFact> expected = Arrays.asList(new DatalogFact[] {
                new DatalogFact(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("1000"), new StringLiteral("2000")
                }))),
                new DatalogFact(new RelationPredicate("B", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("3000"), new StringLiteral("4000")
                }))),
        });
        Assert.assertEquals(toSouffleString(expected), toSouffleString(actual));
    }

    /**
     * FIXME: Probably needs a better way to handle this case.
     */
    @Test
    public void testInstance5() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance5();
        List<DatalogFact> actual = instance.accept(new FactGenVisitor());
        List<DatalogFact> expected = Arrays.asList(new DatalogFact[] {
                new DatalogFact(new RelationPredicate("A?B?C", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("##100##200#"), new StringLiteral("300"), new StringLiteral("400")
                }))),
                new DatalogFact(new RelationPredicate("A?B", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("#100#"), new StringLiteral("200"), new StringLiteral("##100##200#")
                }))),
                new DatalogFact(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("100"), new StringLiteral("#100#")
                }))),
                new DatalogFact(new RelationPredicate("A?B?C", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("##100##200#"), new StringLiteral("300"), new StringLiteral("500")
                }))),
                new DatalogFact(new RelationPredicate("A?B", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("#100#"), new StringLiteral("200"), new StringLiteral("##100##200#")
                }))),
                new DatalogFact(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("100"), new StringLiteral("#100#")
                }))),
        });
        Assert.assertEquals(toSouffleString(expected), toSouffleString(actual));
    }

    @Test
    public void testInstanceFloat1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstanceFloat1();
        List<DatalogFact> actual = instance.accept(new FactGenVisitor());
        List<DatalogFact> expected = Arrays.asList(new DatalogFact[] {
                new DatalogFact(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("100.5")
                }))),
        });
        Assert.assertEquals(toSouffleString(expected), toSouffleString(actual));
    }

    @Test
    public void testInstanceNull1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstanceNull1();
        List<DatalogFact> actual = instance.accept(new FactGenVisitor());
        List<DatalogFact> expected = Arrays.asList(new DatalogFact[] {
                new DatalogFact(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                        new StringLiteral("1"), new StringLiteral("null")
                }))),
        });
        Assert.assertEquals(toSouffleString(expected), toSouffleString(actual));
    }

}

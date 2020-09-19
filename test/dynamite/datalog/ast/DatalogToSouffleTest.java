package dynamite.datalog.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.DatalogTestUtils;
import dynamite.datalog.ast.BinaryPredicate.Operator;
import dynamite.util.DatalogAstUtils;

public class DatalogToSouffleTest {

    @Test
    public void testTypeDecl() {
        TypeDeclaration typeDecl = new TypeDeclaration("Attr");
        Assert.assertEquals(".type Attr", typeDecl.toSouffle());
    }

    @Test
    public void testInputDecl1() {
        InputDeclaration inputDecl = new InputDeclaration("R1");
        Assert.assertEquals(".input R1", inputDecl.toSouffle());
    }

    @Test
    public void testInputDecl2() {
        Map<String, String> parameterMap = new TreeMap<>();
        parameterMap.put("delimiter", "\"\\t\"");
        InputDeclaration inputDecl = new InputDeclaration("R1", parameterMap);
        Assert.assertEquals(".input R1(delimiter=\"\\t\")", inputDecl.toSouffle());
    }

    @Test
    public void testOutputDecl1() {
        OutputDeclaration outputDecl = new OutputDeclaration("R1");
        Assert.assertEquals(".output R1", outputDecl.toSouffle());
    }

    @Test
    public void testOutputDecl2() {
        OutputDeclaration outputDecl = new OutputDeclaration("R1", DatalogAstUtils.buildDelimiterParamMap());
        Assert.assertEquals(".output R1(delimiter=\"\\t\")", outputDecl.toSouffle());
    }

    @Test
    public void testTypedAttr() {
        TypedAttribute typedAttr = new TypedAttribute("id", "Attr");
        Assert.assertEquals("id: Attr", typedAttr.toSouffle());
    }

    @Test
    public void testRelationDecl() {
        List<TypedAttribute> attributes = Arrays.asList(new TypedAttribute[] {
                new TypedAttribute("id1", "Attr1"), new TypedAttribute("id2", "Attr2")
        });
        RelationDeclaration relDecl = new RelationDeclaration("R", attributes);
        Assert.assertEquals(".decl R(id1: Attr1, id2: Attr2)", relDecl.toSouffle());
    }

    @Test
    public void testIdentifier() {
        Identifier id = new Identifier("id");
        Assert.assertEquals("id", id.toSouffle());
    }

    @Test
    public void testStrLiteral() {
        StringLiteral literal = new StringLiteral("name");
        Assert.assertEquals("\"name\"", literal.toSouffle());
    }

    @Test
    public void testIntLiteral() {
        IntLiteral literal = new IntLiteral(99);
        Assert.assertEquals("99", literal.toSouffle());
    }

    @Test
    public void testConcatFunc() {
        ConcatFunction concat = new ConcatFunction(new Identifier("pid"), new Identifier("pname"));
        Assert.assertEquals("cat(pid, pname)", concat.toSouffle());
    }

    @Test
    public void testExpressionHole() {
        List<DatalogExpression> domain = Arrays.asList(new DatalogExpression[] {
                new Identifier("pid"), new Identifier("cid")
        });
        ExpressionHole hole = new ExpressionHole("x1", domain);
        Assert.assertEquals("?x1 {pid, cid}", hole.toSouffle());
    }

    @Test
    public void testRelationPred() {
        List<DatalogExpression> arguments = Arrays.asList(new DatalogExpression[] {
                new Identifier("pid"), new Identifier("pname")
        });
        RelationPredicate relPred = new RelationPredicate("Parts", arguments);
        Assert.assertEquals("Parts(pid, pname)", relPred.toSouffle());
    }

    @Test
    public void testBinaryPred1() {
        Identifier lhs = new Identifier("lhs");
        Identifier rhs = new Identifier("rhs");
        BinaryPredicate eqPred = new BinaryPredicate(lhs, Operator.EQ, rhs);
        Assert.assertEquals("lhs = rhs", eqPred.toSouffle());
    }

    @Test
    public void testBinaryPred2() {
        Identifier lhs = new Identifier("id");
        ConcatFunction rhs = new ConcatFunction(new Identifier("pid"), new Identifier("pname"));
        BinaryPredicate pred = new BinaryPredicate(lhs, Operator.EQ, rhs);
        Assert.assertEquals("id = cat(pid, pname)", pred.toSouffle());
    }

    @Test
    public void testRule() {
        RelationPredicate relA = new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                new Identifier("x"), new Identifier("y"), new Identifier("z")
        }));
        RelationPredicate relB = new RelationPredicate("B", Arrays.asList(new DatalogExpression[] {
                new Identifier("x"), new Identifier("y")
        }));
        RelationPredicate relC = new RelationPredicate("C", Arrays.asList(new DatalogExpression[] {
                new Identifier("y"), new Identifier("z")
        }));
        List<RelationPredicate> heads = Collections.singletonList(relA);
        List<DatalogPredicate> bodies = Arrays.asList(new RelationPredicate[] { relB, relC });
        DatalogRule rule = new DatalogRule(heads, bodies);
        Assert.assertEquals("A(x, y, z) :-"
                + "\n    B(x, y),"
                + "\n    C(y, z).",
                rule.toSouffle());
    }

    @Test
    public void testFact() {
        RelationPredicate relA = new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                new IntLiteral(99), new StringLiteral("name")
        }));
        DatalogFact fact = new DatalogFact(relA);
        Assert.assertEquals("A(99, \"name\").", fact.toSouffle());
    }

    @Test
    public void testProgram1() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram1();
        Assert.assertEquals(".decl R(a: symbol, b: symbol)\n" +
                ".output R(delimiter=\"\\t\")\n" +
                "\n" +
                "R(x, y) :-\n" +
                "    R(y, x).\n" +
                "\n" +
                "R(\"1\", \"1\").\n" +
                "R(\"1\", \"2\").\n" +
                "R(\"1\", \"3\").\n", program.toSouffle());
    }

    @Test
    public void testProgram2() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram2();
        Assert.assertEquals(".decl A?B(a: symbol, b: symbol)\n" +
                ".output A?B(delimiter=\"\\t\")\n" +
                "\n" +
                "A?B(x, z) :-\n" +
                "    A?B(x, y),\n" +
                "    A?B(y, z).\n" +
                "\n" +
                "A?B(\"1\", \"2\").\n" +
                "A?B(\"2\", \"3\").\n", program.toSouffle());
    }
}

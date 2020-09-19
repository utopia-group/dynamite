package dynamite.interact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import dynamite.core.IInstance;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.TypedAttribute;
import dynamite.reldb.RelInstanceParser;
import dynamite.reldb.ast.RelationalInstance;
import dynamite.util.DatalogAstUtils;

public class DatalogDifferentiatorTest {

    private static RelationalInstance buildRelInstance() {
        return RelInstanceParser.parse(Stream.of(
                "A(a1: Int, a2: Int)",
                "1, 1",
                "2, 2",
                "3, 4",
                "B(b1: Int, b2: Int)",
                "5, 6",
                "7, 8")
                .collect(Collectors.joining("\n")));
    }

    private static RelationalInstance buildExpectedRelInstance() {
        return RelInstanceParser.parse(Stream.of(
                "A(a1: Int, a2: Int)",
                "3, 4",
                "B(b1: Int, b2: Int)")
                .collect(Collectors.joining("\n")));
    }

    /**
     * @return the Datalog program
     *
     *         <pre>
     * .decl A(a: symbol, b: symbol)
     * .decl B(a: symbol, b: symbol)
     * .decl R(a: symbol, b: symbol)
     * .output R(delimiter="\\t")
     * R(x, y) :- A(y, x).
     *         </pre>
     *
     */
    private static DatalogProgram buildDatalogProgram1() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(new RelationDeclaration("A", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new RelationDeclaration("B", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new RelationDeclaration("R", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new OutputDeclaration("R", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("x"), new Identifier("y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                    new Identifier("y"), new Identifier("x")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        return new DatalogProgram(decls, rules, Collections.emptyList());
    }

    /**
     * @return the Datalog program
     *
     *         <pre>
     * .decl A(a: symbol, b: symbol)
     * .decl B(a: symbol, b: symbol)
     * .decl R(a: symbol, b: symbol)
     * .output R(delimiter="\\t")
     * R(x, y) :- A(x, y).
     *         </pre>
     *
     */
    private static DatalogProgram buildDatalogProgram2() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(new RelationDeclaration("A", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new RelationDeclaration("B", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new RelationDeclaration("R", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new OutputDeclaration("R", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("x"), new Identifier("y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                    new Identifier("x"), new Identifier("y")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        return new DatalogProgram(decls, rules, Collections.emptyList());
    }

    @Test
    public void testIntToBitSet() {
        BitSet actual1 = DatalogDifferentiator.intToBitSet(1);
        Assert.assertTrue(actual1.get(0));
        Assert.assertFalse(actual1.get(1));
        BitSet actual2 = DatalogDifferentiator.intToBitSet(2);
        Assert.assertFalse(actual2.get(0));
        Assert.assertTrue(actual2.get(1));
        BitSet actual3 = DatalogDifferentiator.intToBitSet(3);
        Assert.assertTrue(actual3.get(0));
        Assert.assertTrue(actual3.get(1));
    }

    @Test
    public void testCheckDifference() {
        DatalogProgram prog1 = buildDatalogProgram1();
        DatalogProgram prog2 = buildDatalogProgram2();
        IInstance instance = buildRelInstance();
        IInstance actual = DatalogDifferentiator.checkDifference(prog1, prog2, instance);
        IInstance expected = buildExpectedRelInstance();
        Assert.assertEquals(expected, actual);
    }

}

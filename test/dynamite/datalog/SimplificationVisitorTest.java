package dynamite.datalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.PlaceHolder;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.TypedAttribute;

public class SimplificationVisitorTest {

    /**
     * @return the following declarations
     *
     *         <pre>
     * .decl S1(a: symbol, b: symbol)
     * .decl S2(a: symbol, b: symbol)
     * .decl S3(a: symbol, b: symbol, c: symbol)
     * .decl R(a: symbol, b: symbol)
     * .output R
     *         </pre>
     */
    private static List<DatalogStatement> buildDeclarations() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(new RelationDeclaration("S1", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new RelationDeclaration("S2", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new RelationDeclaration("S3", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol"), new TypedAttribute("c", "symbol")
            })));
            decls.add(new RelationDeclaration("R", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new OutputDeclaration("R"));
        }
        return decls;
    }

    private static String declarationText() {
        return ".decl S1(a: symbol, b: symbol)\n"
                + ".decl S2(a: symbol, b: symbol)\n"
                + ".decl S3(a: symbol, b: symbol, c: symbol)\n"
                + ".decl R(a: symbol, b: symbol)\n"
                + ".output R\n"
                + "\n";
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * R(v_R?x, v_R?y) :- S1(v_R?y, v_R?x), S2(v_S2?a_0, v_S2?b_0).
     *         </pre>
     */
    private static DatalogProgram buildProgram1() {
        List<DatalogStatement> decls = buildDeclarations();
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("S1", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?y"), new Identifier("v_R?x")
            })));
            bodies.add(new RelationPredicate("S2", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_S2?a_0"), new Identifier("v_S2?b_0")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = Collections.emptyList();
        return new DatalogProgram(decls, rules, facts);
    }

    @Test
    public void test1() {
        DatalogProgram program = buildProgram1();
        DatalogProgram simplified = program.accept(new SimplificationVisitor());
        String expected = declarationText()
                + "R(v_R?x, v_R?y) :-\n"
                + "    S1(v_R?y, v_R?x).\n"
                + "\n";
        Assert.assertEquals(expected, simplified.toSouffle());
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * R(v_R?x, v_R?y) :- S1(v_R?y, v_S1?b_0), S2(v_R?x, v_S2?b_0).
     *         </pre>
     */
    private static DatalogProgram buildProgram2() {
        List<DatalogStatement> decls = buildDeclarations();
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("S1", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?y"), new Identifier("v_S1?b_0")
            })));
            bodies.add(new RelationPredicate("S2", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?x"), new Identifier("v_S2?b_0")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = Collections.emptyList();
        return new DatalogProgram(decls, rules, facts);
    }

    @Test
    public void test2() {
        DatalogProgram program = buildProgram2();
        DatalogProgram simplified = program.accept(new SimplificationVisitor());
        String expected = declarationText()
                + "R(v_R?x, v_R?y) :-\n"
                + "    S1(v_R?y, _),\n"
                + "    S2(v_R?x, _).\n"
                + "\n";
        Assert.assertEquals(expected, simplified.toSouffle());
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * R(v_R?x, v_R?y) :- S1(v_R?y, v_S1?b_0), S2(v_R?x, v_0), S3(v_0, v_S3?b_0, v_S3?c_0).
     *         </pre>
     */
    private static DatalogProgram buildProgram3() {
        List<DatalogStatement> decls = buildDeclarations();
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("S1", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?y"), new Identifier("v_S1?b_0")
            })));
            bodies.add(new RelationPredicate("S2", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?x"), new Identifier("v_0")
            })));
            bodies.add(new RelationPredicate("S3", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_0"), new Identifier("v_S3?b_0"), new Identifier("v_S3?c_0")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = Collections.emptyList();
        return new DatalogProgram(decls, rules, facts);
    }

    @Test
    public void test3() {
        DatalogProgram program = buildProgram3();
        DatalogProgram simplified = program.accept(new SimplificationVisitor());
        String expected = declarationText()
                + "R(v_R?x, v_R?y) :-\n"
                + "    S1(v_R?y, _),\n"
                + "    S2(v_R?x, _).\n"
                + "\n";
        Assert.assertEquals(expected, simplified.toSouffle());
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * R(v_R?x, v_R?y) :- S1(v_R?y, v_0), S2(v_0, v_1), S3(v_1, v_R?x, v_S3?c_0).
     *         </pre>
     */
    private static DatalogProgram buildProgram4() {
        List<DatalogStatement> decls = buildDeclarations();
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("S1", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?y"), new Identifier("v_0")
            })));
            bodies.add(new RelationPredicate("S2", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_0"), new Identifier("v_1")
            })));
            bodies.add(new RelationPredicate("S3", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_1"), new Identifier("v_R?x"), new Identifier("v_S3?c_0")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = Collections.emptyList();
        return new DatalogProgram(decls, rules, facts);
    }

    @Test
    public void test4() {
        DatalogProgram program = buildProgram4();
        DatalogProgram simplified = program.accept(new SimplificationVisitor());
        String expected = declarationText()
                + "R(v_R?x, v_R?y) :-\n"
                + "    S1(v_R?y, v_0),\n"
                + "    S2(v_0, v_1),\n"
                + "    S3(v_1, v_R?x, _).\n"
                + "\n";
        Assert.assertEquals(expected, simplified.toSouffle());
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * R(v_R?x, v_R?y) :- S1(v_R?y, v_0), S2(v_0, v_S2?b_0), S3(v_0, v_R?x, v_S3?c_0).
     *         </pre>
     */
    private static DatalogProgram buildProgram5() {
        List<DatalogStatement> decls = buildDeclarations();
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("S1", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?y"), new Identifier("v_0")
            })));
            bodies.add(new RelationPredicate("S2", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_0"), new Identifier("v_S2?b_0")
            })));
            bodies.add(new RelationPredicate("S3", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_0"), new Identifier("v_R?x"), new Identifier("v_S3?c_0")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = Collections.emptyList();
        return new DatalogProgram(decls, rules, facts);
    }

    @Test
    public void test5() {
        DatalogProgram program = buildProgram5();
        DatalogProgram simplified = program.accept(new SimplificationVisitor());
        String expected = declarationText()
                + "R(v_R?x, v_R?y) :-\n"
                + "    S1(v_R?y, v_0),\n"
                + "    S3(v_0, v_R?x, _).\n"
                + "\n";
        Assert.assertEquals(expected, simplified.toSouffle());
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * R(v_R?x, v_R?y) :- S3(v_S3?a_0, v_R?x, v_R?y), S3(v_S3?a_0, v_R?x, v_R?y).
     *         </pre>
     */
    private static DatalogProgram buildProgram6() {
        List<DatalogStatement> decls = buildDeclarations();
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("S3", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_S3?a_0"), new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            bodies.add(new RelationPredicate("S3", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_S3?a_0"), new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = Collections.emptyList();
        return new DatalogProgram(decls, rules, facts);
    }

    @Test
    public void test6() {
        DatalogProgram program = buildProgram6();
        DatalogProgram simplified = program.accept(new SimplificationVisitor());
        String expected = declarationText()
                + "R(v_R?x, v_R?y) :-\n"
                + "    S3(_, v_R?x, v_R?y).\n"
                + "\n";
        Assert.assertEquals(expected, simplified.toSouffle());
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * R(v_R?x, v_R?y) :- S3(v_S3?a_0, v_R?x, v_R?y), S3(v_S3?a_0, _, v_R?y).
     *         </pre>
     */
    private static DatalogProgram buildProgram7() {
        List<DatalogStatement> decls = buildDeclarations();
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("S3", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_S3?a_0"), new Identifier("v_R?x"), new Identifier("v_R?y")
            })));
            bodies.add(new RelationPredicate("S3", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_S3?a_0"), PlaceHolder.getInstance(), new Identifier("v_R?y")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = Collections.emptyList();
        return new DatalogProgram(decls, rules, facts);
    }

    @Test
    public void test7() {
        DatalogProgram program = buildProgram7();
        DatalogProgram simplified = program.accept(new SimplificationVisitor());
        String expected = declarationText()
                + "R(v_R?x, v_R?y) :-\n"
                + "    S3(_, v_R?x, v_R?y).\n"
                + "\n";
        Assert.assertEquals(expected, simplified.toSouffle());
    }

}

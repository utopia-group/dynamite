package dynamite.datalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dynamite.datalog.ast.BinaryPredicate;
import dynamite.datalog.ast.BinaryPredicate.Operator;
import dynamite.datalog.ast.ConcatFunction;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.datalog.ast.TypeDeclaration;
import dynamite.datalog.ast.TypedAttribute;
import dynamite.util.DatalogAstUtils;

public class DatalogTestUtils {

    /**
     * <pre>
     * .decl R(a: symbol, b: symbol)
     * .output R(delimiter="\\t")
     * R(x, y) :- R(y, x).
     * R("1", "1").
     * R("1", "2").
     * R("1", "3").
     * </pre>
     *
     * @return the Datalog program
     */
    public static DatalogProgram buildDatalogProgram1() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
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
            bodies.add(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new Identifier("y"), new Identifier("x")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("1")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("R", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("3")
            }))));
        }
        DatalogProgram program = new DatalogProgram(decls, rules, facts);
        return program;
    }

    /**
     * <pre>
     * .decl A?B(a: symbol, b: symbol)
     * .output A?B(delimiter="\\t")
     * A?B(x, z) :- A?B(x, y), A?B(y, z).
     * A?B("1", "2").
     * A?B("2", "3").
     * </pre>
     *
     * @return the Datalog program
     */
    public static DatalogProgram buildDatalogProgram2() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(new RelationDeclaration("A?B", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "symbol"), new TypedAttribute("b", "symbol")
            })));
            decls.add(new OutputDeclaration("A?B", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("A?B", Arrays.asList(new DatalogExpression[] {
                    new Identifier("x"), new Identifier("z")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("A?B", Arrays.asList(new DatalogExpression[] {
                    new Identifier("x"), new Identifier("y")
            })));
            bodies.add(new RelationPredicate("A?B", Arrays.asList(new DatalogExpression[] {
                    new Identifier("y"), new Identifier("z")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("A?B", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("A?B", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2"), new StringLiteral("3")
            }))));
        }
        DatalogProgram program = new DatalogProgram(decls, rules, facts);
        return program;
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * .type IntAttr
     * .type StrAttr
     * .type Rel
     * .decl C(c1: StrAttr, c2: StrAttr, c3: StrAttr, c4: StrAttr)
     * .decl A?B(__id: Rel, b1: StrAttr, b2: StrAttr)
     * .decl A(a1: StrAttr, a2: StrAttr, B: Rel)
     * .output A?B(delimiter="\\t")
     * .output A(delimiter="\\t")
     *
     * A?B(_v_0, v_A?B?b1, v_A?B?b2), A(v_A?a1, v_A?a2, _v_0) :-
     *     C(v_A?a1, v_A?a2, v_A?B?b1, v_A?B?b2),
     *     _v_0 = cat("#", v_A?a1, cat("#", cat(v_A?a2, "#"))).
     *
     * C("1", "2", "3", "4").
     *         </pre>
     */
    public static DatalogProgram buildDatalogProgram3() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(TypeDeclaration.intAttrTypeDecl());
            decls.add(TypeDeclaration.strAttrTypeDecl());
            decls.add(TypeDeclaration.relTypeDecl());
            decls.add(new RelationDeclaration("C", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("c1", "StrAttr"), new TypedAttribute("c2", "StrAttr"),
                    new TypedAttribute("c3", "StrAttr"), new TypedAttribute("c4", "StrAttr"),
            })));
            decls.add(new RelationDeclaration("A?B", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("__id", "Rel"), new TypedAttribute("b1", "StrAttr"), new TypedAttribute("b2", "StrAttr")
            })));
            decls.add(new RelationDeclaration("A", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a1", "StrAttr"), new TypedAttribute("a2", "StrAttr"), new TypedAttribute("B", "Rel")
            })));
            decls.add(new OutputDeclaration("A?B", DatalogAstUtils.buildDelimiterParamMap()));
            decls.add(new OutputDeclaration("A", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("A?B", Arrays.asList(new DatalogExpression[] {
                    new Identifier("_v_0"), new Identifier("v_A?B?b1"), new Identifier("v_A?B?b2")
            })));
            heads.add(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_A?a1"), new Identifier("v_A?a2"), new Identifier("_v_0")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("C", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_A?a1"), new Identifier("v_A?a2"),
                    new Identifier("v_A?B?b1"), new Identifier("v_A?B?b2")
            })));
            bodies.add(new BinaryPredicate(new Identifier("_v_0"), Operator.EQ, new ConcatFunction(
                    new StringLiteral("#"), new ConcatFunction(new Identifier("v_A?a1"), new ConcatFunction(
                            new StringLiteral("#"), new ConcatFunction(new Identifier("v_A?a2"), new StringLiteral("#")))))));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("C", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("2"), new StringLiteral("3"), new StringLiteral("4")
            }))));
        }
        DatalogProgram program = new DatalogProgram(decls, rules, facts);
        return program;
    }

    /**
     * @return the following Datalog program
     * <p>
     * <pre>
     * .decl UserId(_id: StrAttr, user_name: StrAttr, user_screen_name: StrAttr)
     * .output UserId(delimiter="\t")
     * .decl Location(_id: StrAttr, lat: StrAttr, lon: StrAttr)
     * .output Location(delimiter="\t")
     * .decl LIVES_IN(_start: StrAttr, _end: StrAttr)
     * .output LIVES_IN(delimiter="\t")
     *         </pre>
     */
    public static DatalogProgram buildDatalogProgram4() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(new RelationDeclaration("UserId", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("_id", "StrAttr"), new TypedAttribute("user_name", "StrAttr"), new TypedAttribute("user_screen_name", "StrAttr")
            })));
            decls.add(new RelationDeclaration("Location", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("_id", "StrAttr"), new TypedAttribute("lat", "StrAttr"), new TypedAttribute("lon", "StrAttr")
            })));
            decls.add(new RelationDeclaration("LIVES_IN", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("_start", "StrAttr"), new TypedAttribute("_end", "StrAttr")
            })));
            decls.add(new OutputDeclaration("UserId", DatalogAstUtils.buildDelimiterParamMap()));
            decls.add(new OutputDeclaration("Location", DatalogAstUtils.buildDelimiterParamMap()));
            decls.add(new OutputDeclaration("LIVES_IN", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        List<DatalogFact> facts = new ArrayList<>();
        return new DatalogProgram(decls, rules, facts);
    }


    /**
     * <pre>
     * R:
     * [1, 1]
     * [1, 2]
     * [1, 3]
     * [2, 1]
     * [3, 1]
     * </pre>
     *
     * @return the Datalog output
     */
    public static DatalogOutput buildDatalogOutput1() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList(new String[] { "1", "1" }));
        data.add(Arrays.asList(new String[] { "1", "2" }));
        data.add(Arrays.asList(new String[] { "1", "3" }));
        data.add(Arrays.asList(new String[] { "2", "1" }));
        data.add(Arrays.asList(new String[] { "3", "1" }));
        RelationOutput r = new RelationOutput("R", data);
        return new DatalogOutput(Collections.singletonList(r));
    }

    /**
     * <pre>
     * A?B:
     * [1, 2]
     * [2, 3]
     * [1, 3]
     * </pre>
     *
     * @return the Datalog output
     */
    public static DatalogOutput buildDatalogOutput2() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList(new String[] { "1", "2" }));
        data.add(Arrays.asList(new String[] { "2", "3" }));
        data.add(Arrays.asList(new String[] { "1", "3" }));
        RelationOutput aB = new RelationOutput("A?B", data);
        return new DatalogOutput(Collections.singletonList(aB));
    }

}

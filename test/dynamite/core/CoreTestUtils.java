package dynamite.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dynamite.datalog.ast.BinaryPredicate;
import dynamite.datalog.ast.BinaryPredicate.Operator;
import dynamite.datalog.ast.ConcatFunction;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.ExpressionHole;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.IntLiteral;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.datalog.ast.TypeDeclaration;
import dynamite.datalog.ast.TypedAttribute;
import dynamite.docdb.InstCanonicalNameVisitor;
import dynamite.docdb.SchemaCanonicalNameVisitor;
import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.reldb.ast.RIIntValue;
import dynamite.reldb.ast.RIRow;
import dynamite.reldb.ast.RIStrValue;
import dynamite.reldb.ast.RITable;
import dynamite.reldb.ast.RIValue;
import dynamite.reldb.ast.RSColumn;
import dynamite.reldb.ast.RSColumn.ColumnType;
import dynamite.reldb.ast.RSTable;
import dynamite.reldb.ast.RelationalInstance;
import dynamite.reldb.ast.RelationalSchema;
import dynamite.util.DatalogAstUtils;
import dynamite.util.ListMultiMap;

public class CoreTestUtils {

    // ------------ Micro Benchmark 1 ------------
    /**
     * S: [{ a: string, b: string }]
     */
    public static DocumentSchema buildDocSrcSchema1() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> sAttrs = new ArrayList<>();
            {
                sAttrs.add(new DSAtomicAttr("a", AttrType.STRING));
                sAttrs.add(new DSAtomicAttr("b", AttrType.STRING));
            }
            DSCollection s = new DSCollection("S", new DSDocument(sAttrs));
            collections.add(s);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * T: [{ a: string , b: string }]
     */
    public static DocumentSchema buildDocTgtSchema1() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> tAttrs = new ArrayList<>();
            {
                tAttrs.add(new DSAtomicAttr("a", AttrType.STRING));
                tAttrs.add(new DSAtomicAttr("b", AttrType.STRING));
            }
            DSCollection t = new DSCollection("T", new DSDocument(tAttrs));
            collections.add(t);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * <pre>
     * T?a <- [S?b]
     * T?b <- [S?a]
     * S?b <-> S?a
     * </pre>
     */
    public static ValueCorr buildValueCorr1() {
        ListMultiMap<String, String> srcToTgt = new ListMultiMap<>();
        srcToTgt.put("T?a", "S?b");
        srcToTgt.put("T?b", "S?a");
        srcToTgt.put("T?b", "S?b");
        ListMultiMap<String, String> srcToSrc = new ListMultiMap<>();
        srcToSrc.put("S?b", "S?a");
        ListMultiMap<String, String> srcToConsts = new ListMultiMap<>();
        return new ValueCorr(srcToTgt, srcToSrc, srcToConsts);
    }

    /**
     * S: [{a: "1", b: "1"}, { a: "1", b: "2"}, {a: "1", b: "3" }]
     */
    private static DocumentInstance buildDocSrcInstance1() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("a", new DIStrValue("1")));
                    attributes.add(new DIAtomicAttrValue("b", new DIStrValue("1")));
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("a", new DIStrValue("1")));
                    attributes.add(new DIAtomicAttrValue("b", new DIStrValue("2")));
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("a", new DIStrValue("1")));
                    attributes.add(new DIAtomicAttrValue("b", new DIStrValue("3")));
                }
                documents.add(new DIDocument(attributes));
            }
            DICollection s = new DICollection("S", documents);
            collections.add(s);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * T: [{a: "1", b: "1"}, { a: "2", b: "1"}, {a: "3", b: "1" }]
     */
    private static DocumentInstance buildDocTgtInstance1() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("a", new DIStrValue("1")));
                    attributes.add(new DIAtomicAttrValue("b", new DIStrValue("1")));
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("a", new DIStrValue("2")));
                    attributes.add(new DIAtomicAttrValue("b", new DIStrValue("1")));
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("a", new DIStrValue("3")));
                    attributes.add(new DIAtomicAttrValue("b", new DIStrValue("1")));
                }
                documents.add(new DIDocument(attributes));
            }
            DICollection t = new DICollection("T", documents);
            collections.add(t);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * @see {@link #buildDocSrcInstance1()} and {@link #buildDocTgtInstance1()}
     */
    public static InstanceExample buildExample1() {
        DocumentInstance srcInst = buildDocSrcInstance1();
        DocumentInstance tgtInst = buildDocTgtInstance1();
        return new InstanceExample(srcInst, tgtInst);
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * .type IntAttr
     * .type StrAttr
     * .type Rel
     * .decl S(a: StrAttr, b: StrAttr)
     * .decl T(a: StrAttr, b: StrAttr)
     * .output T(delimiter="\", \"")
     * T(v_T?a, v_T?b) :-
     *     S(v_T?b, v_T?a).
     * S("1", "1").
     * S("1", "2").
     * S("1", "3").
     *         </pre>
     */
    public static DatalogProgram buildDatalogProgram1() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(new TypeDeclaration("IntAttr"));
            decls.add(new TypeDeclaration("StrAttr"));
            decls.add(new TypeDeclaration("Rel"));
            decls.add(new RelationDeclaration("S", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "StrAttr"), new TypedAttribute("b", "StrAttr")
            })));
            decls.add(new RelationDeclaration("T", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "StrAttr"), new TypedAttribute("b", "StrAttr")
            })));
            decls.add(new OutputDeclaration("T", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("T", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_T?a"), new Identifier("v_T?b")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("S", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_T?b"), new Identifier("v_T?a")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("S", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("1")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("S", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("S", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("3")
            }))));
        }
        return new DatalogProgram(decls, rules, facts);
    }

    // ------------ Micro Benchmark 2 ------------
    /**
     * Person: [{ id: int, name: string, friendship: [{ fid: int, years: int }] }]
     */
    public static DocumentSchema buildDocSrcSchema2() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> personAttrs = new ArrayList<>();
            {
                personAttrs.add(new DSAtomicAttr("id", AttrType.INT));
                personAttrs.add(new DSAtomicAttr("name", AttrType.STRING));
                { // friendship
                    List<DSAttribute> friendshipAttrs = new ArrayList<>();
                    {
                        friendshipAttrs.add(new DSAtomicAttr("fid", AttrType.INT));
                        friendshipAttrs.add(new DSAtomicAttr("years", AttrType.INT));
                    }
                    DSCollection friendship = new DSCollection("friendship", new DSDocument(friendshipAttrs));
                    personAttrs.add(new DSCollectionAttr(friendship));
                }
            }
            DSCollection person = new DSCollection("Person", new DSDocument(personAttrs));
            collections.add(person);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * Friend: [{ n1: string, n2: string, y: int }]
     */
    public static DocumentSchema buildDocTgtSchema2() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> friendAttrs = new ArrayList<>();
            {
                friendAttrs.add(new DSAtomicAttr("n1", AttrType.STRING));
                friendAttrs.add(new DSAtomicAttr("n2", AttrType.STRING));
                friendAttrs.add(new DSAtomicAttr("y", AttrType.INT));
            }
            DSCollection friend = new DSCollection("Friend", new DSDocument(friendAttrs));
            collections.add(friend);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * Friend(n1: String, n2: String, y: Int)
     */
    public static RelationalSchema buildRelTgtSchema2() {
        List<RSTable> tables = new ArrayList<>();
        {
            List<RSColumn> columns = new ArrayList<>();
            columns.add(new RSColumn("n1", ColumnType.STRING));
            columns.add(new RSColumn("n2", ColumnType.STRING));
            columns.add(new RSColumn("y", ColumnType.INT));
            tables.add(new RSTable("Friend", columns));
        }
        return new RelationalSchema(tables);
    }

    /**
     * @return the following document instance
     *
     *         <pre>
     * Person: [{ id: 1, name: "Alice", friendship: [
     *             {fid: 1, years: 20}, {fid: 2, years: 3}, {fid: 4, years: 4}]},
     *          { id: 2, name: "Bob", friendship: [
     *             {fid: 2, years: 19}, {fid: 3, years: 2}, {fid: 1, years: 3}]},
     *          { id: 3, name: "Clair", friendship: [
     *             {fid: 3, years: 20}, {fid: 2, years: 2}]},
     *          { id: 4, name: "David", friendship: [
     *             {fid: 4, years: 19}, {fid: 1, years: 4}]}]
     *         </pre>
     */
    private static DocumentInstance buildDocSrcInstance2() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(1)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("Alice")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(1)),
                                    new DIAtomicAttrValue("years", new DIIntValue(20)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(2)),
                                    new DIAtomicAttrValue("years", new DIIntValue(3)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(4)),
                                    new DIAtomicAttrValue("years", new DIIntValue(4)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(2)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("Bob")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(2)),
                                    new DIAtomicAttrValue("years", new DIIntValue(19)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(3)),
                                    new DIAtomicAttrValue("years", new DIIntValue(2)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(1)),
                                    new DIAtomicAttrValue("years", new DIIntValue(3)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(3)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("Clair")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(3)),
                                    new DIAtomicAttrValue("years", new DIIntValue(20)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(2)),
                                    new DIAtomicAttrValue("years", new DIIntValue(2)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(4)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("David")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(4)),
                                    new DIAtomicAttrValue("years", new DIIntValue(19)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(1)),
                                    new DIAtomicAttrValue("years", new DIIntValue(4)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            DICollection person = new DICollection("Person", documents);
            collections.add(person);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    private static DIDocument buildDocTgtInstanceRow2(String n1, String n2, int y) {
        List<DIAttrValue> attributes = new ArrayList<>();
        attributes.add(new DIAtomicAttrValue("n1", new DIStrValue(n1)));
        attributes.add(new DIAtomicAttrValue("n2", new DIStrValue(n2)));
        attributes.add(new DIAtomicAttrValue("y", new DIIntValue(y)));
        return new DIDocument(attributes);
    }

    /**
     * @return the following document instance
     *
     *         <pre>
     * Friend: [{n1: "Alice", n2: "Alice", y: 20},
     *          {n1: "Alice", n2: "Bob", y: 3},
     *          {n1: "Alice", n2: "David", y: 4},
     *          {n1: "Bob", n2: "Bob", y: 19},
     *          {n1: "Bob", n2: "Clair", y: 2},
     *          {n1: "Bob", n2: "Alice", y: 3},
     *          {n1: "Clair", n2: "Clair", y: 20},
     *          {n1: "Clair", n2: "Bob", y: 2},
     *          {n1: "David", n2: "David", y: 19},
     *          {n1: "David", n2: "Alice", y: 4}]
     *         </pre>
     */
    private static DocumentInstance buildDocTgtInstance2() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                documents.add(buildDocTgtInstanceRow2("Alice", "Alice", 20));
                documents.add(buildDocTgtInstanceRow2("Alice", "Bob", 3));
                documents.add(buildDocTgtInstanceRow2("Alice", "David", 4));
                documents.add(buildDocTgtInstanceRow2("Bob", "Bob", 19));
                documents.add(buildDocTgtInstanceRow2("Bob", "Clair", 2));
                documents.add(buildDocTgtInstanceRow2("Bob", "Alice", 3));
                documents.add(buildDocTgtInstanceRow2("Clair", "Clair", 20));
                documents.add(buildDocTgtInstanceRow2("Clair", "Bob", 2));
                documents.add(buildDocTgtInstanceRow2("David", "David", 19));
                documents.add(buildDocTgtInstanceRow2("David", "Alice", 4));
            }
            DICollection friend = new DICollection("Friend", documents);
            collections.add(friend);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * @see {@link #buildDocSrcInstance2()} and {@link #buildDocTgtInstance2()}
     */
    public static InstanceExample buildExample2() {
        DocumentInstance srcInst = buildDocSrcInstance2();
        DocumentInstance tgtInst = buildDocTgtInstance2();
        return new InstanceExample(srcInst, tgtInst);
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * .type IntAttr
     * .type StrAttr
     * .type Rel
     * .decl Person?friendship(__id: Rel, fid: IntAttr, years: IntAttr)
     * .decl Person(id: IntAttr, name: StrAttr, friendship: Rel)
     * .decl Friend(n1: StrAttr, n2: StrAttr, y: IntAttr)
     * .output Friend(delimiter="\", \"")
     * Friend(v_Friend?n1, v_Friend?n2, v_Friend?y) :-
     *     Person?friendship(v_0, v_Person?friendship?fid_0, v_Friend?y),
     *     Person(v_Person?id_1, v_Friend?n2, v_0),
     *     Person?friendship(v_1, v_Person?id_1, v_Friend?y),
     *     Person(v_Person?id_0, v_Friend?n1, v_1).
     * Person?friendship("#1#Alice#", "1", "20").
     * Person?friendship("#1#Alice#", "2", "3").
     * Person?friendship("#1#Alice#", "4", "4").
     * Person("1", "Alice", "#1#Alice#").
     * Person?friendship("#2#Bob#", "2", "19").
     * Person?friendship("#2#Bob#", "3", "2").
     * Person?friendship("#2#Bob#", "1", "3").
     * Person("2", "Bob", "#2#Bob#").
     * Person?friendship("#3#Clair#", "3", "20").
     * Person?friendship("#3#Clair#", "2", "2").
     * Person("3", "Clair", "#3#Clair#").
     * Person?friendship("#4#David#", "4", "19").
     * Person?friendship("#4#David#", "1", "4").
     * Person("4", "David", "#4#David#").
     *         </pre>
     */
    public static DatalogProgram buildDatalogProgram2() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(TypeDeclaration.intAttrTypeDecl());
            decls.add(TypeDeclaration.strAttrTypeDecl());
            decls.add(TypeDeclaration.relTypeDecl());
            decls.add(new RelationDeclaration("Person?friendship", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("__id", "Rel"), new TypedAttribute("fid", "IntAttr"), new TypedAttribute("years", "IntAttr")
            })));
            decls.add(new RelationDeclaration("Person", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("id", "IntAttr"), new TypedAttribute("name", "StrAttr"), new TypedAttribute("friendship", "Rel")
            })));
            decls.add(new RelationDeclaration("Friend", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("n1", "StrAttr"), new TypedAttribute("n2", "StrAttr"), new TypedAttribute("y", "IntAttr")
            })));
            decls.add(new OutputDeclaration("Friend", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_Friend?n1"), new Identifier("v_Friend?n2"), new Identifier("v_Friend?y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_0"), new Identifier("v_Person?friendship?fid_0"), new Identifier("v_Friend?y")
            })));
            bodies.add(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_Person?id_1"), new Identifier("v_Friend?n2"), new Identifier("v_0")
            })));
            bodies.add(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_1"), new Identifier("v_Person?id_1"), new Identifier("v_Friend?y")
            })));
            bodies.add(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_Person?id_0"), new Identifier("v_Friend?n1"), new Identifier("v_1")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#1#Alice#"), new StringLiteral("1"), new StringLiteral("20")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#1#Alice#"), new StringLiteral("2"), new StringLiteral("3")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#1#Alice#"), new StringLiteral("4"), new StringLiteral("4")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("Alice"), new StringLiteral("#1#Alice#")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#2#Bob#"), new StringLiteral("2"), new StringLiteral("19")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#2#Bob#"), new StringLiteral("3"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#2#Bob#"), new StringLiteral("1"), new StringLiteral("3")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2"), new StringLiteral("Bob"), new StringLiteral("#2#Bob#")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#3#Clair#"), new StringLiteral("3"), new StringLiteral("20")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#3#Clair#"), new StringLiteral("2"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("3"), new StringLiteral("Clair"), new StringLiteral("#3#Clair#")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#4#David#"), new StringLiteral("4"), new StringLiteral("19")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#4#David#"), new StringLiteral("1"), new StringLiteral("4")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("4"), new StringLiteral("David"), new StringLiteral("#4#David#")
            }))));
        }
        return new DatalogProgram(decls, rules, facts);
    }

    // ------------ Micro Benchmark 2a ------------
    /**
     * @return the following document instance
     *
     *         <pre>
     * Person: [{ id: 1, name: "Alice", friendship: [
     *             {fid: 2, years: 3}, {fid: 4, years: 4}]},
     *          { id: 2, name: "Bob", friendship: [
     *             {fid: 3, years: 2}, {fid: 1, years: 3}]},
     *          { id: 3, name: "Clair", friendship: [
     *             {fid: 2, years: 2}]},
     *          { id: 4, name: "David", friendship: [
     *             {fid: 1, years: 4}]}]
     *         </pre>
     */
    private static DocumentInstance buildDocSrcInstance2a() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(1)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("Alice")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(2)),
                                    new DIAtomicAttrValue("years", new DIIntValue(3)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(4)),
                                    new DIAtomicAttrValue("years", new DIIntValue(4)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(2)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("Bob")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(3)),
                                    new DIAtomicAttrValue("years", new DIIntValue(2)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(1)),
                                    new DIAtomicAttrValue("years", new DIIntValue(3)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(3)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("Clair")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(2)),
                                    new DIAtomicAttrValue("years", new DIIntValue(2)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(4)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("David")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(1)),
                                    new DIAtomicAttrValue("years", new DIIntValue(4)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            DICollection person = new DICollection("Person", documents);
            collections.add(person);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * @return the following document instance
     *
     *         <pre>
     * Friend: [{n1: "Alice", n2: "Bob", y: 3},
     *          {n1: "Alice", n2: "David", y: 4},
     *          {n1: "Bob", n2: "Clair", y: 2},
     *          {n1: "Bob", n2: "Alice", y: 3},
     *          {n1: "Clair", n2: "Bob", y: 2},
     *          {n1: "David", n2: "Alice", y: 4}]
     *         </pre>
     */
    private static DocumentInstance buildDocTgtInstance2a() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                documents.add(buildDocTgtInstanceRow2("Alice", "Bob", 3));
                documents.add(buildDocTgtInstanceRow2("Alice", "David", 4));
                documents.add(buildDocTgtInstanceRow2("Bob", "Clair", 2));
                documents.add(buildDocTgtInstanceRow2("Bob", "Alice", 3));
                documents.add(buildDocTgtInstanceRow2("Clair", "Bob", 2));
                documents.add(buildDocTgtInstanceRow2("David", "Alice", 4));
            }
            DICollection friend = new DICollection("Friend", documents);
            collections.add(friend);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * @see {@link #buildDocSrcInstance2a()} and {@link #buildDocTgtInstance2a()}
     */
    public static InstanceExample buildExample2a() {
        DocumentInstance srcInst = buildDocSrcInstance2a();
        DocumentInstance tgtInst = buildDocTgtInstance2a();
        return new InstanceExample(srcInst, tgtInst);
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * .type IntAttr
     * .type StrAttr
     * .type Rel
     * .decl Person?friendship(__id: Rel, fid: IntAttr, years: IntAttr)
     * .decl Person(id: IntAttr, name: StrAttr, friendship: Rel)
     * .decl Friend(n1: StrAttr, n2: StrAttr, y: IntAttr)
     * .output Friend
     * Friend(v_Friend?n1, v_Friend?n2, v_Friend?y) :-
     *     Person?friendship(v_0, v_Person?friendship?fid_0, v_Friend?y),
     *     Person(v_Person?id_1, v_Friend?n2, v_0),
     *     Person?friendship(v_1, v_Person?id_1, v_Friend?y),
     *     Person(v_Person?id_0, v_Friend?n1, v_1).
     * Person?friendship("#1#Alice#", "2", "3").
     * Person?friendship("#1#Alice#", "4", "4").
     * Person("1", "Alice", "#1#Alice#").
     * Person?friendship("#2#Bob#", "3", "2").
     * Person?friendship("#2#Bob#", "1", "3").
     * Person("2", "Bob", "#2#Bob#").
     * Person?friendship("#3#Clair#", "2", "2").
     * Person("3", "Clair", "#3#Clair#").
     * Person?friendship("#4#David#", "1", "4").
     * Person("4", "David", "#4#David#").
     *         </pre>
     */
    public static DatalogProgram buildDatalogProgram2a() {
        DatalogProgram program2 = buildDatalogProgram2();
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#1#Alice#"), new StringLiteral("2"), new StringLiteral("3")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#1#Alice#"), new StringLiteral("4"), new StringLiteral("4")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("Alice"), new StringLiteral("#1#Alice#")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#2#Bob#"), new StringLiteral("3"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#2#Bob#"), new StringLiteral("1"), new StringLiteral("3")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2"), new StringLiteral("Bob"), new StringLiteral("#2#Bob#")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#3#Clair#"), new StringLiteral("2"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("3"), new StringLiteral("Clair"), new StringLiteral("#3#Clair#")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#4#David#"), new StringLiteral("1"), new StringLiteral("4")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("4"), new StringLiteral("David"), new StringLiteral("#4#David#")
            }))));
        }
        return new DatalogProgram(program2.declarations, program2.rules, facts);
    }

    // ------------ Micro Benchmark 2b ------------
    /**
     * @return the following document instance
     *
     *         <pre>
     * Person: [{ id: 1, name: "Alice", friendship: [
     *             {fid: 2, years: 3}, {fid: 4, years: 4}]},
     *          { id: 2, name: "Bob", friendship: [
     *             {fid: 3, years: 5}, {fid: 1, years: 3}]},
     *          { id: 3, name: "Clair", friendship: [
     *             {fid: 2, years: 5}]},
     *          { id: 4, name: "David", friendship: [
     *             {fid: 1, years: 4}]}]
     *         </pre>
     */
    private static DocumentInstance buildDocSrcInstance2b() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(1)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("Alice")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(2)),
                                    new DIAtomicAttrValue("years", new DIIntValue(3)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(4)),
                                    new DIAtomicAttrValue("years", new DIIntValue(4)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(2)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("Bob")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(3)),
                                    new DIAtomicAttrValue("years", new DIIntValue(5)),
                            })));
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(1)),
                                    new DIAtomicAttrValue("years", new DIIntValue(3)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(3)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("Clair")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(2)),
                                    new DIAtomicAttrValue("years", new DIIntValue(5)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("id", new DIIntValue(4)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("David")));
                    { // friendship
                        List<DIDocument> friendshipDocs = new ArrayList<>();
                        {
                            friendshipDocs.add(new DIDocument(Arrays.asList(new DIAttrValue[] {
                                    new DIAtomicAttrValue("fid", new DIIntValue(1)),
                                    new DIAtomicAttrValue("years", new DIIntValue(4)),
                            })));
                        }
                        DICollection friendship = new DICollection("friendship", friendshipDocs);
                        attributes.add(new DICollectionAttrValue(friendship));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            DICollection person = new DICollection("Person", documents);
            collections.add(person);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * @return the following document instance
     *
     *         <pre>
     * Friend: [{n1: "Alice", n2: "Bob", y: 3},
     *          {n1: "Alice", n2: "David", y: 4},
     *          {n1: "Bob", n2: "Clair", y: 5},
     *          {n1: "Bob", n2: "Alice", y: 3},
     *          {n1: "Clair", n2: "Bob", y: 5},
     *          {n1: "David", n2: "Alice", y: 4}]
     *         </pre>
     */
    private static DocumentInstance buildDocTgtInstance2b() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                documents.add(buildDocTgtInstanceRow2("Alice", "Bob", 3));
                documents.add(buildDocTgtInstanceRow2("Alice", "David", 4));
                documents.add(buildDocTgtInstanceRow2("Bob", "Clair", 5));
                documents.add(buildDocTgtInstanceRow2("Bob", "Alice", 3));
                documents.add(buildDocTgtInstanceRow2("Clair", "Bob", 5));
                documents.add(buildDocTgtInstanceRow2("David", "Alice", 4));
            }
            DICollection friend = new DICollection("Friend", documents);
            collections.add(friend);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * @see {@link #buildDocSrcInstance2b()} and {@link #buildDocTgtInstance2b()}
     */
    public static InstanceExample buildExample2b() {
        DocumentInstance srcInst = buildDocSrcInstance2b();
        DocumentInstance tgtInst = buildDocTgtInstance2b();
        return new InstanceExample(srcInst, tgtInst);
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * .type IntAttr
     * .type StrAttr
     * .type Rel
     * .decl Person?friendship(__id: Rel, fid: IntAttr, years: IntAttr)
     * .decl Person(id: IntAttr, name: StrAttr, friendship: Rel)
     * .decl Friend(n1: StrAttr, n2: StrAttr, y: IntAttr)
     * .output Friend
     * Friend(v_Friend?n1, v_Friend?n2, v_Friend?y) :-
     *     Person?friendship(v_0, v_Person?friendship?fid_0, v_Friend?y),
     *     Person(v_Person?id_1, v_Friend?n2, v_0),
     *     Person?friendship(v_1, v_Person?id_1, v_Friend?y),
     *     Person(v_Person?id_0, v_Friend?n1, v_1).
     * Person?friendship("#1#Alice#", "2", "3").
     * Person?friendship("#1#Alice#", "4", "4").
     * Person("1", "Alice", "#1#Alice#").
     * Person?friendship("#2#Bob#", "3", "5").
     * Person?friendship("#2#Bob#", "1", "3").
     * Person("2", "Bob", "#2#Bob#").
     * Person?friendship("#3#Clair#", "2", "5").
     * Person("3", "Clair", "#3#Clair#").
     * Person?friendship("#4#David#", "1", "4").
     * Person("4", "David", "#4#David#").
     *         </pre>
     */
    public static DatalogProgram buildDatalogProgram2b() {
        DatalogProgram program2 = buildDatalogProgram2();
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#1#Alice#"), new StringLiteral("2"), new StringLiteral("3")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#1#Alice#"), new StringLiteral("4"), new StringLiteral("4")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("Alice"), new StringLiteral("#1#Alice#")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#2#Bob#"), new StringLiteral("3"), new StringLiteral("5")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#2#Bob#"), new StringLiteral("1"), new StringLiteral("3")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2"), new StringLiteral("Bob"), new StringLiteral("#2#Bob#")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#3#Clair#"), new StringLiteral("2"), new StringLiteral("5")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("3"), new StringLiteral("Clair"), new StringLiteral("#3#Clair#")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("#4#David#"), new StringLiteral("1"), new StringLiteral("4")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("4"), new StringLiteral("David"), new StringLiteral("#4#David#")
            }))));
        }
        return new DatalogProgram(program2.declarations, program2.rules, facts);
    }

    // ------------ Micro Benchmark 2c ------------
    /**
     * @see {@link #buildDocSrcInstance2()}
     */
    private static DocumentInstance buildDocSrcInstance2c() {
        return buildDocSrcInstance2();
    }

    /**
     * @return the following relational instance
     *
     *         <pre>
     * Friend(n1: String, n2: String, y: Int)
     * "Alice", "Alice", 20
     * "Alice", "Bob", 3
     * "Alice", "David", 4
     * "Bob", "Bob", 19
     * "Bob", "Clair", 2
     * "Bob", "Alice", 3
     * "Clair", "Clair", 20
     * "Clair", "Bob", 2
     * "David", "David", 19
     * "David", "Alice", 4
     *         </pre>
     */
    private static RelationalInstance buildRelTgtInstance2c() {
        RelationalSchema schema = buildRelTgtSchema2();
        RSTable tableSchema = schema.tables.get(0);
        List<RIRow> rows = new ArrayList<>();
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Alice"), new RIStrValue("Alice"), new RIIntValue(20)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Alice"), new RIStrValue("Bob"), new RIIntValue(3)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Alice"), new RIStrValue("David"), new RIIntValue(4)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Bob"), new RIStrValue("Bob"), new RIIntValue(19)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Bob"), new RIStrValue("Clair"), new RIIntValue(2)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Bob"), new RIStrValue("Alice"), new RIIntValue(3)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Clair"), new RIStrValue("Clair"), new RIIntValue(20)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Clair"), new RIStrValue("Bob"), new RIIntValue(2)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("David"), new RIStrValue("David"), new RIIntValue(19)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("David"), new RIStrValue("Alice"), new RIIntValue(4)
        })));
        RITable table = new RITable(tableSchema, rows);
        return new RelationalInstance(Collections.singletonList(table));
    }

    /**
     * @see {@link #buildDocSrcInstance2c()} and {@link #buildRelTgtInstance2c()}
     */
    public static InstanceExample buildExample2c() {
        DocumentInstance srcInst = buildDocSrcInstance2c();
        RelationalInstance tgtInst = buildRelTgtInstance2c();
        return new InstanceExample(srcInst, tgtInst);
    }

    /**
     * @see {@link #buildDatalogProgram2()}
     */
    public static DatalogProgram buildDatalogProgram2c() {
        return buildDatalogProgram2();
    }

    // ------------ Micro Benchmark 2d ------------
    /**
     * @see {@link #buildDocSrcInstance2b()}
     */
    private static DocumentInstance buildDocSrcInstance2d() {
        return buildDocSrcInstance2b();
    }

    /**
     * @return the following relational instance
     *
     *         <pre>
     * Friend(n1: String, n2: String, y: Int)
     * "Alice", "Bob", 3
     * "Alice", "David", 4
     * "Bob", "Clair", 5
     * "Bob", "Alice", 3
     * "Clair", "Bob", 5
     * "David", "Alice", 4
     *         </pre>
     */
    private static RelationalInstance buildRelTgtInstance2d() {
        RelationalSchema schema = buildRelTgtSchema2();
        RSTable tableSchema = schema.tables.get(0);
        List<RIRow> rows = new ArrayList<>();
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Alice"), new RIStrValue("Bob"), new RIIntValue(3)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Alice"), new RIStrValue("David"), new RIIntValue(4)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Bob"), new RIStrValue("Clair"), new RIIntValue(5)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Bob"), new RIStrValue("Alice"), new RIIntValue(3)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("Clair"), new RIStrValue("Bob"), new RIIntValue(5)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIStrValue("David"), new RIStrValue("Alice"), new RIIntValue(4)
        })));
        RITable table = new RITable(tableSchema, rows);
        return new RelationalInstance(Collections.singletonList(table));
    }

    /**
     * @see {@link #buildDocSrcInstance2d()} and {@link #buildRelTgtInstance2d()}
     */
    public static InstanceExample buildExample2d() {
        DocumentInstance srcInst = buildDocSrcInstance2d();
        RelationalInstance tgtInst = buildRelTgtInstance2d();
        return new InstanceExample(srcInst, tgtInst);
    }

    /**
     * @see {@link #buildDatalogProgram2b()}
     */
    public static DatalogProgram buildDatalogProgram2d() {
        return buildDatalogProgram2b();
    }

    // ------------ Micro Benchmark 3 ------------
    /**
     * Friend: [{ i: int, n1: string, n2: string, y: int }]
     */
    public static DocumentSchema buildDocSrcSchema3() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> friendAttrs = new ArrayList<>();
            {
                friendAttrs.add(new DSAtomicAttr("i", AttrType.INT));
                friendAttrs.add(new DSAtomicAttr("n1", AttrType.STRING));
                friendAttrs.add(new DSAtomicAttr("n2", AttrType.STRING));
                friendAttrs.add(new DSAtomicAttr("y", AttrType.INT));
            }
            DSCollection friend = new DSCollection("Friend", new DSDocument(friendAttrs));
            collections.add(friend);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * Friend(i: Int, n1: String, n2: String, y: Int)
     */
    public static RelationalSchema buildRelSrcSchema3() {
        List<RSTable> tables = new ArrayList<>();
        {
            List<RSColumn> columns = new ArrayList<>();
            columns.add(new RSColumn("i", ColumnType.INT));
            columns.add(new RSColumn("n1", ColumnType.STRING));
            columns.add(new RSColumn("n2", ColumnType.STRING));
            columns.add(new RSColumn("y", ColumnType.INT));
            tables.add(new RSTable("Friend", columns));
        }
        return new RelationalSchema(tables);
    }

    /**
     * Person: [{ id: int, name: string, friendship: [{ fid: int, years: int }] }]
     */
    public static DocumentSchema buildDocTgtSchema3() {
        return buildDocSrcSchema2();
    }

    private static DIDocument buildDocSrcInstanceRow3(int i, String n1, String n2, int y) {
        List<DIAttrValue> attributes = new ArrayList<>();
        attributes.add(new DIAtomicAttrValue("i", new DIIntValue(i)));
        attributes.add(new DIAtomicAttrValue("n1", new DIStrValue(n1)));
        attributes.add(new DIAtomicAttrValue("n2", new DIStrValue(n2)));
        attributes.add(new DIAtomicAttrValue("y", new DIIntValue(y)));
        return new DIDocument(attributes);
    }

    /**
     * @return the following document instance
     *
     *         <pre>
     * Friend: [{i: 1, n1: "Alice", n2: "Alice", y: 20},
     *          {i: 1, n1: "Alice", n2: "Bob", y: 3},
     *          {i: 1, n1: "Alice", n2: "David", y: 4},
     *          {i: 2, n1: "Bob", n2: "Bob", y: 19},
     *          {i: 2, n1: "Bob", n2: "Clair", y: 2},
     *          {i: 2, n1: "Bob", n2: "Alice", y: 3},
     *          {i: 3, n1: "Clair", n2: "Clair", y: 20},
     *          {i: 3, n1: "Clair", n2: "Bob", y: 2},
     *          {i: 4, n1: "David", n2: "David", y: 19},
     *          {i: 4, n1: "David", n2: "Alice", y: 4}]
     *         </pre>
     */
    private static DocumentInstance buildDocSrcInstance3() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                documents.add(buildDocSrcInstanceRow3(1, "Alice", "Alice", 20));
                documents.add(buildDocSrcInstanceRow3(1, "Alice", "Bob", 3));
                documents.add(buildDocSrcInstanceRow3(1, "Alice", "David", 4));
                documents.add(buildDocSrcInstanceRow3(2, "Bob", "Bob", 19));
                documents.add(buildDocSrcInstanceRow3(2, "Bob", "Clair", 2));
                documents.add(buildDocSrcInstanceRow3(2, "Bob", "Alice", 3));
                documents.add(buildDocSrcInstanceRow3(3, "Clair", "Clair", 20));
                documents.add(buildDocSrcInstanceRow3(3, "Clair", "Bob", 2));
                documents.add(buildDocSrcInstanceRow3(4, "David", "David", 19));
                documents.add(buildDocSrcInstanceRow3(4, "David", "Alice", 4));
            }
            DICollection friend = new DICollection("Friend", documents);
            collections.add(friend);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * @return the following document instance
     *
     *         <pre>
     * Person: [{ id: 1, name: "Alice", friendship: [
     *             {fid: 1, years: 20}, {fid: 2, years: 3}, {fid: 4, years: 4}]},
     *          { id: 2, name: "Bob", friendship: [
     *             {fid: 2, years: 19}, {fid: 3, years: 2}, {fid: 1, years: 3}]},
     *          { id: 3, name: "Clair", friendship: [
     *             {fid: 3, years: 20}, {fid: 2, years: 2}]},
     *          { id: 4, name: "David", friendship: [
     *             {fid: 4, years: 19}, {fid: 1, years: 4}]}]
     *         </pre>
     */
    private static DocumentInstance buildDocTgtInstance3() {
        return buildDocSrcInstance2();
    }

    /**
     * @see {@link #buildDocSrcInstance3()} and {@link #buildDocTgtInstance3()}
     */
    public static InstanceExample buildExample3() {
        DocumentInstance srcInst = buildDocSrcInstance3();
        DocumentInstance tgtInst = buildDocTgtInstance3();
        return new InstanceExample(srcInst, tgtInst);
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * .type IntAttr
     * .type StrAttr
     * .type Rel
     * .decl Friend(i: IntAttr, n1: StrAttr, n2: StrAttr, y: IntAttr)
     * .decl Person?friendship(__id: Rel, fid: IntAttr, years: IntAttr)
     * .decl Person(id: IntAttr, name: StrAttr, friendship: Rel)
     * .output Person?friendship(delimiter="\", \"")
     * .output Person(delimiter="\", \"")
     *
     * Person?friendship(_v_0, v_Person?friendship?fid, v_Person?friendship?years), Person(v_Person?id, v_Person?name, _v_0) :-
     *     Friend(v_Person?id, v_Person?name, v_Friend?n2_0, v_Person?friendship?years),
     *     Friend(v_Person?friendship?fid, v_Friend?n2_0, v_Person?name, v_Person?friendship?years),
     *     _v_0 = cat("#", cat(v_Person?id, cat("#", cat(v_Person?name, "#")))).
     *
     * Friend("1", "Alice", "Alice", "20").
     * Friend("1", "Alice", "Bob", "3").
     * Friend("1", "Alice", "David", "4").
     * Friend("2", "Bob", "Bob", "19").
     * Friend("2", "Bob", "Clair", "2").
     * Friend("2", "Bob", "Alice", "3").
     * Friend("3", "Clair", "Clair", "20").
     * Friend("3", "Clair", "Bob", "2").
     * Friend("4", "David", "David", "19").
     * Friend("4", "David", "Alice", "4").
     *         </pre>
     */
    public static DatalogProgram buildDatalogProgram3() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(TypeDeclaration.intAttrTypeDecl());
            decls.add(TypeDeclaration.strAttrTypeDecl());
            decls.add(TypeDeclaration.relTypeDecl());
            decls.add(new RelationDeclaration("Friend", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("i", "IntAttr"), new TypedAttribute("n1", "StrAttr"),
                    new TypedAttribute("n2", "StrAttr"), new TypedAttribute("y", "IntAttr")
            })));
            decls.add(new RelationDeclaration("Person?friendship", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("__id", "Rel"), new TypedAttribute("fid", "IntAttr"), new TypedAttribute("years", "IntAttr")
            })));
            decls.add(new RelationDeclaration("Person", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("id", "IntAttr"), new TypedAttribute("name", "StrAttr"), new TypedAttribute("friendship", "Rel")
            })));
            decls.add(new OutputDeclaration("Person?friendship", DatalogAstUtils.buildDelimiterParamMap()));
            decls.add(new OutputDeclaration("Person", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new Identifier("_v_0"), new Identifier("v_Person?friendship?fid"), new Identifier("v_Person?friendship?years")
            })));
            heads.add(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_Person?id"), new Identifier("v_Person?name"), new Identifier("_v_0")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_Person?id"), new Identifier("v_Person?name"),
                    new Identifier("v_Friend?n2_0"), new Identifier("v_Person?friendship?years")
            })));
            bodies.add(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_Person?friendship?fid"), new Identifier("v_Friend?n2_0"),
                    new Identifier("v_Person?name"), new Identifier("v_Person?friendship?years")
            })));
            bodies.add(new BinaryPredicate(new Identifier("_v_0"), Operator.EQ, new ConcatFunction(
                    new StringLiteral("#"), new ConcatFunction(new Identifier("v_Person?id"), new ConcatFunction(
                            new StringLiteral("#"), new ConcatFunction(new Identifier("v_Person?name"), new StringLiteral("#")))))));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("Alice"), new StringLiteral("Alice"), new StringLiteral("20")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("Alice"), new StringLiteral("Bob"), new StringLiteral("3")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("Alice"), new StringLiteral("David"), new StringLiteral("4")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2"), new StringLiteral("Bob"), new StringLiteral("Bob"), new StringLiteral("19")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2"), new StringLiteral("Bob"), new StringLiteral("Clair"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2"), new StringLiteral("Bob"), new StringLiteral("Alice"), new StringLiteral("3")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("3"), new StringLiteral("Clair"), new StringLiteral("Clair"), new StringLiteral("20")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("3"), new StringLiteral("Clair"), new StringLiteral("Bob"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("4"), new StringLiteral("David"), new StringLiteral("David"), new StringLiteral("19")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("4"), new StringLiteral("David"), new StringLiteral("Alice"), new StringLiteral("4")
            }))));
        }
        return new DatalogProgram(decls, rules, facts);
    }

    // ------------ Micro Benchmark 3a ------------
    /**
     * @return the following relational instance
     *
     *         <pre>
     * Friend(i: Int, n1: String, n2: String, y: Int)
     * 1, "Alice", "Alice", 20
     * 1, "Alice", "Bob", 3
     * 1, "Alice", "David", 4
     * 2, "Bob", "Bob", 19
     * 2, "Bob", "Clair", 2
     * 2, "Bob", "Alice", 3
     * 3, "Clair", "Clair", 20
     * 3, "Clair", "Bob", 2
     * 4, "David", "David", 19
     * 4, "David", "Alice", 4
     *         </pre>
     */
    private static RelationalInstance buildRelSrcInstance3a() {
        RelationalSchema schema = buildRelSrcSchema3();
        RSTable tableSchema = schema.tables.get(0);
        List<RIRow> rows = new ArrayList<>();
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(1), new RIStrValue("Alice"), new RIStrValue("Alice"), new RIIntValue(20)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(1), new RIStrValue("Alice"), new RIStrValue("Bob"), new RIIntValue(3)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(1), new RIStrValue("Alice"), new RIStrValue("David"), new RIIntValue(4)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(2), new RIStrValue("Bob"), new RIStrValue("Bob"), new RIIntValue(19)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(2), new RIStrValue("Bob"), new RIStrValue("Clair"), new RIIntValue(2)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(2), new RIStrValue("Bob"), new RIStrValue("Alice"), new RIIntValue(3)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(3), new RIStrValue("Clair"), new RIStrValue("Clair"), new RIIntValue(20)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(3), new RIStrValue("Clair"), new RIStrValue("Bob"), new RIIntValue(2)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(4), new RIStrValue("David"), new RIStrValue("David"), new RIIntValue(19)
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(4), new RIStrValue("David"), new RIStrValue("Alice"), new RIIntValue(4)
        })));
        RITable table = new RITable(tableSchema, rows);
        return new RelationalInstance(Collections.singletonList(table));
    }

    /**
     * @see {@link #buildDocTgtInstance3()}
     */
    private static DocumentInstance buildDocTgtInstance3a() {
        return buildDocTgtInstance3();
    }

    /**
     * @see {@link #buildRelSrcInstance3a()} and {@link #buildDocTgtInstance3a()}
     */
    public static InstanceExample buildExample3a() {
        RelationalInstance srcInst = buildRelSrcInstance3a();
        DocumentInstance tgtInst = buildDocTgtInstance3a();
        return new InstanceExample(srcInst, tgtInst);
    }

    /**
     * @see {@link #buildDatalogProgram3()}
     */
    public static DatalogProgram buildDatalogProgram3a() {
        return buildDatalogProgram3();
    }

    // ------------ Micro Benchmark 4 ------------
    /**
     * @return the following relational schema
     *
     *         <pre>
     * A(a1: int, a2: int)
     * B(b1: int, b2: int)
     *         </pre>
     */
    public static RelationalSchema buildRelSrcSchema4() {
        List<RSTable> tables = new ArrayList<>();
        {
            List<RSColumn> columns = new ArrayList<>();
            columns.add(new RSColumn("a1", ColumnType.INT));
            columns.add(new RSColumn("a2", ColumnType.INT));
            tables.add(new RSTable("A", columns));
        }
        {
            List<RSColumn> columns = new ArrayList<>();
            columns.add(new RSColumn("b1", ColumnType.INT));
            columns.add(new RSColumn("b2", ColumnType.INT));
            tables.add(new RSTable("B", columns));
        }
        return new RelationalSchema(tables);
    }

    /**
     * @return the following relational schema
     *
     *         <pre>
     * C(c1: int, c2: int)
     * D(d1: int, d2: int)
     *         </pre>
     */
    public static RelationalSchema buildRelTgtSchema4() {
        List<RSTable> tables = new ArrayList<>();
        {
            List<RSColumn> columns = new ArrayList<>();
            columns.add(new RSColumn("c1", ColumnType.INT));
            columns.add(new RSColumn("c2", ColumnType.INT));
            tables.add(new RSTable("C", columns));
        }
        {
            List<RSColumn> columns = new ArrayList<>();
            columns.add(new RSColumn("d1", ColumnType.INT));
            columns.add(new RSColumn("d2", ColumnType.INT));
            tables.add(new RSTable("D", columns));
        }
        return new RelationalSchema(tables);
    }

    /**
     * @return the following relational instance
     *
     *         <pre>
     * A(a1: int, a2: int)
     * 10 100
     * 20 200
     * B(b1: int, b2: int)
     * 10 100
     * 20 200
     * 30 300
     *         </pre>
     */
    private static RelationalInstance buildRelSrcInstance4() {
        RelationalSchema schema = buildRelSrcSchema4();
        List<RITable> tables = new ArrayList<>();
        {
            RSTable tableSchema = schema.tables.get(0);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(10), new RIIntValue(100)
            })));
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(20), new RIIntValue(200)
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        {
            RSTable tableSchema = schema.tables.get(1);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(10), new RIIntValue(100)
            })));
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(20), new RIIntValue(200)
            })));
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(30), new RIIntValue(300)
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        return new RelationalInstance(tables);
    }

    /**
     * @return the following relational instance
     *
     *         <pre>
     * C(c1: int, c2: int)
     * 100 10
     * 200 20
     * D(d1: int, d2: int)
     * 100 10
     * 200 20
     * 300 30
     *         </pre>
     */
    private static RelationalInstance buildRelTgtInstance4() {
        RelationalSchema schema = buildRelTgtSchema4();
        List<RITable> tables = new ArrayList<>();
        {
            RSTable tableSchema = schema.tables.get(0);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(100), new RIIntValue(10)
            })));
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(200), new RIIntValue(20)
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        {
            RSTable tableSchema = schema.tables.get(1);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(100), new RIIntValue(10)
            })));
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(200), new RIIntValue(20)
            })));
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(300), new RIIntValue(30)
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        return new RelationalInstance(tables);
    }

    /**
     * @see {@link #buildRelSrcInstance4()} and {@link #buildRelTgtInstance4()}
     */
    public static InstanceExample buildExample4() {
        RelationalInstance srcInst = buildRelSrcInstance4();
        RelationalInstance tgtInst = buildRelTgtInstance4();
        return new InstanceExample(srcInst, tgtInst);
    }

    /**
     * @return the following Datalog program
     *
     *         <pre>
     * .type IntAttr
     * .type StrAttr
     * .type Rel
     * .decl A(a1: IntAttr, a2: IntAttr)
     * .decl B(b1: IntAttr, b2: IntAttr)
     * .decl C(c1: IntAttr, c2: IntAttr)
     * .output C(delimiter="\", \"")
     * .decl D(d1: IntAttr, d2: IntAttr)
     * .output D(delimiter="\", \"")
     *
     * C(v_C?c1, v_C?c2) :-
     *     A(v_C?c2, v_C?c1).
     * D(v_D?d1, v_D?d2) :-
     *    B(v_D?d2, v_D?d1).
     *
     * A("10", "100").
     * A("20", "200").
     * B("10", "100").
     * B("20", "200").
     * B("30", "300").
     *         </pre>
     */
    public static DatalogProgram buildDatalogProgram4() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(TypeDeclaration.intAttrTypeDecl());
            decls.add(TypeDeclaration.strAttrTypeDecl());
            decls.add(TypeDeclaration.relTypeDecl());
            decls.add(new RelationDeclaration("A", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a1", "IntAttr"), new TypedAttribute("a2", "IntAttr")
            })));
            decls.add(new RelationDeclaration("B", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("b1", "IntAttr"), new TypedAttribute("b2", "IntAttr")
            })));
            decls.add(new RelationDeclaration("C", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("c1", "IntAttr"), new TypedAttribute("c2", "IntAttr")
            })));
            decls.add(new OutputDeclaration("C", DatalogAstUtils.buildDelimiterParamMap()));
            decls.add(new RelationDeclaration("D", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("d1", "IntAttr"), new TypedAttribute("d2", "IntAttr")
            })));
            decls.add(new OutputDeclaration("D", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("C", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_C?c1"), new Identifier("v_C?c2")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_C?c2"), new Identifier("v_C?c1")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("D", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_D?d1"), new Identifier("v_D?d2")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("B", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_D?d2"), new Identifier("v_D?d1")
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("10"), new StringLiteral("100")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("20"), new StringLiteral("200")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("B", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("10"), new StringLiteral("100")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("B", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("20"), new StringLiteral("200")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("B", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("30"), new StringLiteral("300")
            }))));
        }
        return new DatalogProgram(decls, rules, facts);
    }

    // ------------ Other Test Utilities ------------
    /**
     * @return the following Datalog sketch
     *
     *         <pre>
     * .type IntAttr
     * .type StrAttr
     * .type Rel
     * .decl Person(id: IntAttr, name: StrAttr, friendship: Rel)
     * .decl Person?friendship(__id: Rel, fid: IntAttr, years: IntAttr)
     * .decl Friend(n1: StrAttr, n2: StrAttr, y: IntAttr)
     * .output Friend(delimiter="\", \"")
     * Friend(n1, n2, y) :-
     *     Person(?h1 {id1, id2, fid1, fid2}, ?h2 {n1, n2, name1, name2}, v1),
     *     Person?friendship(v1, ?h3 {id1, id2, fid1, fid2}, ?h4 {y, years1, years2}),
     *     Person(?h5 {id1, id2, fid1, fid2}, ?h6 {n1, n2, name1, name2}, v2),
     *     Person?friendship(v2, ?h7 {id1, id2, fid1, fid2}, ?h8 {y, years1, years2}).
     * Person(1, "Alice", "1-Alice").
     * Person?friendship("1-Alice", 1, 20).
     * Person?friendship("1-Alice", 2, 3).
     * Person?friendship("1-Alice", 4, 4).
     * Person(2, "Bob", "2-Bob").
     * Person?friendship("2-Bob", 2, 19).
     * Person?friendship("2-Bob", 3, 2).
     * Person?friendship("2-Bob", 1, 3).
     * Person(3, "Clair", "3-Clair").
     * Person?friendship("3-Clair", 3, 20).
     * Person?friendship("3-Clair", 2, 2).
     * Person(4, "David", "4-David").
     * Person?friendship("4-David", 4, 19).
     * Person?friendship("4-David", 1, 4).
     *         </pre>
     */
    public static Sketch buildSketch0() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(TypeDeclaration.intAttrTypeDecl());
            decls.add(TypeDeclaration.strAttrTypeDecl());
            decls.add(TypeDeclaration.relTypeDecl());
            decls.add(new RelationDeclaration("Person", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("id", "IntAttr"), new TypedAttribute("name", "StrAttr"), new TypedAttribute("friendship", "Rel")
            })));
            decls.add(new RelationDeclaration("Person?friendship", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("__id", "Rel"), new TypedAttribute("fid", "IntAttr"), new TypedAttribute("years", "IntAttr")
            })));
            decls.add(new RelationDeclaration("Friend", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("n1", "StrAttr"), new TypedAttribute("n2", "StrAttr"), new TypedAttribute("y", "IntAttr")
            })));
            decls.add(new OutputDeclaration("Friend", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("Friend", Arrays.asList(new DatalogExpression[] {
                    new Identifier("n1"), new Identifier("n2"), new Identifier("y")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new ExpressionHole("h1", Arrays.asList(new DatalogExpression[] {
                            new Identifier("id1"), new Identifier("id2"),
                            new Identifier("fid1"), new Identifier("fid2")
                    })),
                    new ExpressionHole("h2", Arrays.asList(new DatalogExpression[] {
                            new Identifier("n1"), new Identifier("n2"),
                            new Identifier("name1"), new Identifier("name2"),
                    })),
                    new Identifier("v1")
            })));
            bodies.add(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v1"),
                    new ExpressionHole("h3", Arrays.asList(new DatalogExpression[] {
                            new Identifier("id1"), new Identifier("id2"),
                            new Identifier("fid1"), new Identifier("fid2")
                    })),
                    new ExpressionHole("h4", Arrays.asList(new DatalogExpression[] {
                            new Identifier("y"), new Identifier("years1"), new Identifier("years2")
                    }))
            })));
            bodies.add(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new ExpressionHole("h5", Arrays.asList(new DatalogExpression[] {
                            new Identifier("id1"), new Identifier("id2"),
                            new Identifier("fid1"), new Identifier("fid2")
                    })),
                    new ExpressionHole("h6", Arrays.asList(new DatalogExpression[] {
                            new Identifier("n1"), new Identifier("n2"),
                            new Identifier("name1"), new Identifier("name2"),
                    })),
                    new Identifier("v1")
            })));
            bodies.add(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v1"),
                    new ExpressionHole("h7", Arrays.asList(new DatalogExpression[] {
                            new Identifier("id1"), new Identifier("id2"),
                            new Identifier("fid1"), new Identifier("fid2")
                    })),
                    new ExpressionHole("h8", Arrays.asList(new DatalogExpression[] {
                            new Identifier("y"), new Identifier("years1"), new Identifier("years2")
                    }))
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new IntLiteral(1), new StringLiteral("Alice"), new StringLiteral("1-Alice")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1-Alice"), new IntLiteral(1), new IntLiteral(20)
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1-Alice"), new IntLiteral(2), new IntLiteral(3)
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1-Alice"), new IntLiteral(4), new IntLiteral(4)
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new IntLiteral(2), new StringLiteral("Bob"), new StringLiteral("2-Bob")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2-Bob"), new IntLiteral(2), new IntLiteral(19)
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2-Bob"), new IntLiteral(3), new IntLiteral(2)
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("2-Bob"), new IntLiteral(1), new IntLiteral(3)
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new IntLiteral(3), new StringLiteral("Clair"), new StringLiteral("3-Clair")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("3-Clair"), new IntLiteral(3), new IntLiteral(20)
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("3-Clair"), new IntLiteral(2), new IntLiteral(2)
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person", Arrays.asList(new DatalogExpression[] {
                    new IntLiteral(4), new StringLiteral("David"), new StringLiteral("4-David")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("4-David"), new IntLiteral(4), new IntLiteral(19)
            }))));
            facts.add(new DatalogFact(new RelationPredicate("Person?friendship", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("4-David"), new IntLiteral(1), new IntLiteral(4)
            }))));
        }
        DatalogProgram program = new DatalogProgram(decls, rules, facts);
        return new Sketch(program);
    }

    /**
     * @return the following encoding map
     *
     *         <pre>
     * 0 -> v_S?a_0
     * 1 -> v_S?b_0
     * 2 -> v_T?b
     * 3 -> v_T?a
     *         </pre>
     */
    public static EncodingMap buildEncodingMap01() {
        Map<DatalogExpression, Integer> identToValueMap = new HashMap<>();
        identToValueMap.put(new Identifier("v_S?a_0"), 0);
        identToValueMap.put(new Identifier("v_S?b_0"), 1);
        identToValueMap.put(new Identifier("v_T?b"), 2);
        identToValueMap.put(new Identifier("v_T?a"), 3);
        return new EncodingMap(identToValueMap);
    }

    /**
     * @return the following Datalog sketch
     *
     *         <pre>
     * .type IntAttr
     * .type StrAttr
     * .type Rel
     * .decl S(a: StrAttr, b: StrAttr)
     * .decl T(a: StrAttr, b: StrAttr)
     * .output T(delimiter="\", \"")
     * T(v_T?a, v_T?b) :-
     *     S(?h0 {v_S?a_0, v_S?b_0, v_T?b}, ?h1 {v_S?b_0, v_S?a_0, v_T?a}).
     * S("1", "1").
     * S("1", "2").
     * S("1", "3").
     *         </pre>
     */
    public static Sketch buildSketch01() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(TypeDeclaration.intAttrTypeDecl());
            decls.add(TypeDeclaration.strAttrTypeDecl());
            decls.add(TypeDeclaration.relTypeDecl());
            decls.add(new RelationDeclaration("S", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "StrAttr"), new TypedAttribute("b", "StrAttr")
            })));
            decls.add(new RelationDeclaration("T", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("a", "StrAttr"), new TypedAttribute("b", "StrAttr")
            })));
            decls.add(new OutputDeclaration("T", DatalogAstUtils.buildDelimiterParamMap()));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = new ArrayList<>();
            heads.add(new RelationPredicate("T", Arrays.asList(new DatalogExpression[] {
                    new Identifier("v_T?a"), new Identifier("v_T?b")
            })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            bodies.add(new RelationPredicate("S", Arrays.asList(new DatalogExpression[] {
                    new ExpressionHole("h0", Arrays.asList(new DatalogExpression[] {
                            new Identifier("v_S?a_0"), new Identifier("v_S?b_0"), new Identifier("v_T?b")
                    })),
                    new ExpressionHole("h1", Arrays.asList(new DatalogExpression[] {
                            new Identifier("v_S?b_0"), new Identifier("v_S?a_0"), new Identifier("v_T?a")
                    }))
            })));
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(new RelationPredicate("S", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("1")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("S", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("2")
            }))));
            facts.add(new DatalogFact(new RelationPredicate("S", Arrays.asList(new DatalogExpression[] {
                    new StringLiteral("1"), new StringLiteral("3")
            }))));
        }
        DatalogProgram program = new DatalogProgram(decls, rules, facts);
        return new Sketch(program);
    }

}

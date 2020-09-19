package dynamite.reldb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dynamite.docdb.DocDbTestUtils;
import dynamite.docdb.InstCanonicalNameVisitor;
import dynamite.docdb.SchemaCanonicalNameVisitor;
import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.reldb.ast.RIFloatValue;
import dynamite.reldb.ast.RIIntValue;
import dynamite.reldb.ast.RINullValue;
import dynamite.reldb.ast.RIRow;
import dynamite.reldb.ast.RIStrValue;
import dynamite.reldb.ast.RITable;
import dynamite.reldb.ast.RIValue;
import dynamite.reldb.ast.RSColumn;
import dynamite.reldb.ast.RSColumn.ColumnType;
import dynamite.reldb.ast.RSTable;
import dynamite.reldb.ast.RelationalInstance;
import dynamite.reldb.ast.RelationalSchema;

public class RelDbTestUtils {

    /**
     * @return the following relational schema
     *
     *         <pre>
     * R(a: Int, b: Int, c: String, d: String)
     *         </pre>
     */
    public static RelationalSchema buildRelationalSchema1() {
        List<RSTable> tables = new ArrayList<>();
        {
            List<RSColumn> columns = new ArrayList<>();
            columns.add(new RSColumn("a", ColumnType.INT));
            columns.add(new RSColumn("b", ColumnType.INT));
            columns.add(new RSColumn("c", ColumnType.STRING));
            columns.add(new RSColumn("d", ColumnType.STRING));
            tables.add(new RSTable("R", columns));
        }
        return new RelationalSchema(tables);
    }

    /**
     * @return the following document schema
     *
     *         <pre>
     * R: [{ a: Int, b: Int, c: String, d: String }]
     *         </pre>
     */
    public static DocumentSchema buildDocumentSchema1() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> attrs = new ArrayList<>();
            {
                attrs.add(new DSAtomicAttr("a", AttrType.INT));
                attrs.add(new DSAtomicAttr("b", AttrType.INT));
                attrs.add(new DSAtomicAttr("c", AttrType.STRING));
                attrs.add(new DSAtomicAttr("d", AttrType.STRING));
            }
            DSCollection bCollect = new DSCollection("R", new DSDocument(attrs));
            collections.add(bCollect);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * @return the following relational instance
     *
     *         <pre>
     * R(a: Int, b: Int, c: String, d: String)
     * 1, 2, "s1", "s2"
     * 3, 4, "s3", "s4"
     *         </pre>
     */
    public static RelationalInstance buildRelationalInstance1() {
        RelationalSchema schema = buildRelationalSchema1();
        RSTable tableSchema = schema.tables.get(0);
        List<RIRow> rows = new ArrayList<>();
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(1), new RIIntValue(2), new RIStrValue("s1"), new RIStrValue("s2")
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(3), new RIIntValue(4), new RIStrValue("s3"), new RIStrValue("s4")
        })));
        RITable table = new RITable(tableSchema, rows);
        return new RelationalInstance(Collections.singletonList(table));
    }

    /**
     * @return the following relational instance
     *
     *         <pre>
     * R(a: Int, b: Int, c: String, d: String)
     * 1, 2, "s1 and "quoted" nested string", "s2"
     * 3, 4, "s3", "s4"
     *         </pre>
     */
    public static RelationalInstance buildRelationalInstanceComma1() {
        RelationalSchema schema = buildRelationalSchema1();
        RSTable tableSchema = schema.tables.get(0);
        List<RIRow> rows = new ArrayList<>();
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(1), new RIIntValue(2), new RIStrValue("s1, and commas,,"), new RIStrValue("s2")
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(3), new RIIntValue(4), new RIStrValue("s3"), new RIStrValue("s4")
        })));
        RITable table = new RITable(tableSchema, rows);
        return new RelationalInstance(Collections.singletonList(table));
    }

    /**
     * @return the following relational instance
     * <p>
     * <pre>
     * R(a: Int, b: Int, c: String, d: String)
     * 1, 2, "s1", "s2"
     * 3, 4, null, "s4"
     *         </pre>
     */
    public static RelationalInstance buildRelationalInstanceNull2() {
        RelationalSchema schema = buildRelationalSchema1();
        RSTable tableSchema = schema.tables.get(0);
        List<RIRow> rows = new ArrayList<>();
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(1), new RIIntValue(2), new RIStrValue("s1"), new RIStrValue("s2")
        })));
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(3), new RIIntValue(4), RINullValue.getInstance(), new RIStrValue("s4")
        })));
        RITable table = new RITable(tableSchema, rows);
        return new RelationalInstance(Collections.singletonList(table));
    }

    /**
     * @return the following document instance
     *
     *         <pre>
     * A: [{ a: 1, b: 2, c: "s1", d: "s2" },
     *     { a: 3, b: 4, c: "s3", d: "s4" }]
     *         </pre>
     */
    public static DocumentInstance buildDocumentInstance1() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> rDocs = new ArrayList<>();
            {
                List<DIAttrValue> rAttrs = new ArrayList<>();
                {
                    rAttrs.add(new DIAtomicAttrValue("a", new DIIntValue(1)));
                    rAttrs.add(new DIAtomicAttrValue("b", new DIIntValue(2)));
                    rAttrs.add(new DIAtomicAttrValue("c", new DIStrValue("s1")));
                    rAttrs.add(new DIAtomicAttrValue("d", new DIStrValue("s2")));
                }
                rDocs.add(new DIDocument(rAttrs));
            }
            {
                List<DIAttrValue> rAttrs = new ArrayList<>();
                {
                    rAttrs.add(new DIAtomicAttrValue("a", new DIIntValue(3)));
                    rAttrs.add(new DIAtomicAttrValue("b", new DIIntValue(4)));
                    rAttrs.add(new DIAtomicAttrValue("c", new DIStrValue("s3")));
                    rAttrs.add(new DIAtomicAttrValue("d", new DIStrValue("s4")));
                }
                rDocs.add(new DIDocument(rAttrs));
            }
            collections.add(new DICollection("R", rDocs));
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * @return the following relational schema
     *
     *         <pre>
     * A(a1: Int, a2: Int)
     * B(b1: Int, b2: Int)
     *         </pre>
     */
    public static RelationalSchema buildRelationalSchema2() {
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
     * @return the following document schema
     *
     *         <pre>
     * A: [{ a1: Int, a2: Int }]
     * B: [{ b1: Int, b2: Int }]
     *         </pre>
     */
    public static DocumentSchema buildDocumentSchema2() {
        return DocDbTestUtils.buildDocumentSchema4();
    }

    /**
     * @return the following relational instance
     *
     *         <pre>
     * A(a1: Int, a2: Int)
     * 1, 2
     * 3, 4
     * B(b1: Int, b2: Int)
     * 5, 6
     * 7, 8
     *         </pre>
     */
    public static RelationalInstance buildRelationalInstance2() {
        RelationalSchema schema = buildRelationalSchema2();
        List<RITable> tables = new ArrayList<>();
        {
            RSTable tableSchema = schema.tables.get(0);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(1), new RIIntValue(2),
            })));
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(3), new RIIntValue(4),
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        {
            RSTable tableSchema = schema.tables.get(1);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(5), new RIIntValue(6),
            })));
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(7), new RIIntValue(8),
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        return new RelationalInstance(tables);
    }

    /**
     * @return the following document instance
     *
     *         <pre>
     * A: [{ a1: 1, a2: 2 }, { a1: 3, a2: 4 }]
     * B: [{ b1: 5, b2: 6 }, { b1: 7, b2: 8 }]
     *         </pre>
     */
    public static DocumentInstance buildDocumentInstance2() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> aDocs = new ArrayList<>();
            {
                List<DIAttrValue> aAttrs = new ArrayList<>();
                {
                    aAttrs.add(new DIAtomicAttrValue("a1", new DIIntValue(1)));
                    aAttrs.add(new DIAtomicAttrValue("a2", new DIIntValue(2)));
                }
                aDocs.add(new DIDocument(aAttrs));
            }
            {
                List<DIAttrValue> aAttrs = new ArrayList<>();
                {
                    aAttrs.add(new DIAtomicAttrValue("a1", new DIIntValue(3)));
                    aAttrs.add(new DIAtomicAttrValue("a2", new DIIntValue(4)));
                }
                aDocs.add(new DIDocument(aAttrs));
            }
            collections.add(new DICollection("A", aDocs));
        }
        {
            List<DIDocument> bDocs = new ArrayList<>();
            {
                List<DIAttrValue> bAttrs = new ArrayList<>();
                {
                    bAttrs.add(new DIAtomicAttrValue("b1", new DIIntValue(5)));
                    bAttrs.add(new DIAtomicAttrValue("b2", new DIIntValue(6)));
                }
                bDocs.add(new DIDocument(bAttrs));
            }
            {
                List<DIAttrValue> bAttrs = new ArrayList<>();
                {
                    bAttrs.add(new DIAtomicAttrValue("b1", new DIIntValue(7)));
                    bAttrs.add(new DIAtomicAttrValue("b2", new DIIntValue(8)));
                }
                bDocs.add(new DIDocument(bAttrs));
            }
            collections.add(new DICollection("B", bDocs));
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * @return the following relational schema
     *
     *         <pre>
     * A(a: Float)
     *         </pre>
     */
    public static RelationalSchema buildRelationalSchemaFloat1() {
        List<RSTable> tables = new ArrayList<>();
        {
            List<RSColumn> columns = new ArrayList<>();
            columns.add(new RSColumn("a", ColumnType.FLOAT));
            tables.add(new RSTable("A", columns));
        }
        return new RelationalSchema(tables);
    }

    /**
     * @see {@link DocDbTestUtils#buildDocumentSchemaFloat1()}
     */
    public static DocumentSchema buildDocumentSchemaFloat1() {
        return DocDbTestUtils.buildDocumentSchemaFloat1();
    }

    /**
     * @return the following relational instance
     *
     *         <pre>
     * A(a: Float)
     * 100.5
     *         </pre>
     */
    public static RelationalInstance buildRelationalInstanceFloat1() {
        RelationalSchema schema = buildRelationalSchemaFloat1();
        RSTable tableSchema = schema.tables.get(0);
        List<RIRow> rows = new ArrayList<>();
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIFloatValue("100.5")
        })));
        RITable table = new RITable(tableSchema, rows);
        return new RelationalInstance(Collections.singletonList(table));
    }

    /**
     * @see {@link DocDbTestUtils#buildDocumentInstanceFloat1()}
     */
    public static DocumentInstance buildDocumentInstanceFloat1() {
        return DocDbTestUtils.buildDocumentInstanceFloat1();
    }

    /**
     * @return the following relational schema
     *
     *         <pre>
     * A(a: Int, b: String)
     *         </pre>
     */
    public static RelationalSchema buildRelationalSchemaNull1() {
        List<RSTable> tables = new ArrayList<>();
        {
            List<RSColumn> columns = new ArrayList<>();
            columns.add(new RSColumn("a", ColumnType.INT));
            columns.add(new RSColumn("b", ColumnType.STRING));
            tables.add(new RSTable("A", columns));
        }
        return new RelationalSchema(tables);
    }

    /**
     * @see {@link DocDbTestUtils#buildDocumentSchemaNull1()}
     */
    public static DocumentSchema buildDocumentSchemaNull1() {
        return DocDbTestUtils.buildDocumentSchemaNull1();
    }

    /**
     * @return the following relational instance
     *
     *         <pre>
     * A(a: Int, b: String)
     * 1, null
     *         </pre>
     */
    public static RelationalInstance buildRelationalInstanceNull1() {
        RelationalSchema schema = buildRelationalSchemaNull1();
        RSTable tableSchema = schema.tables.get(0);
        List<RIRow> rows = new ArrayList<>();
        rows.add(new RIRow(Arrays.asList(new RIValue[] {
                new RIIntValue(1), RINullValue.getInstance()
        })));
        RITable table = new RITable(tableSchema, rows);
        return new RelationalInstance(Collections.singletonList(table));
    }

    /**
     * @see {@link DocDbTestUtils#buildDocumentInstanceNull1()}
     */
    public static DocumentInstance buildDocumentInstanceNull1() {
        return DocDbTestUtils.buildDocumentInstanceNull1();
    }

}

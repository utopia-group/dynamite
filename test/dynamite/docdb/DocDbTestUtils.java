package dynamite.docdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIFloatValue;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DINullValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;

public class DocDbTestUtils {

    /**
     * Computers: [{ cid: 100, name: "C1", manufacturer: "M1", catalog: "C2", parts: [{value: 101}, {value: 102}] }]
     */
    public static DocumentInstance buildDocumentInstance1() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("cid", new DIIntValue(100)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("C1")));
                    attributes.add(new DIAtomicAttrValue("manufacturer", new DIStrValue("M1")));
                    attributes.add(new DIAtomicAttrValue("catalog", new DIStrValue("C2")));
                    // parts
                    {
                        List<DIDocument> partsDocs = new ArrayList<>();
                        {
                            List<DIAttrValue> value101 = Collections.singletonList(new DIAtomicAttrValue("value", new DIIntValue(101)));
                            partsDocs.add(new DIDocument(value101));
                            List<DIAttrValue> value102 = Collections.singletonList(new DIAtomicAttrValue("value", new DIIntValue(102)));
                            partsDocs.add(new DIDocument(value102));
                        }
                        DICollection parts = new DICollection("parts", partsDocs);
                        attributes.add(new DICollectionAttrValue(parts));
                    }
                }
                documents.add(new DIDocument(attributes));
            }
            DICollection computers = new DICollection("Computers", documents);
            collections.add(computers);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * Computers: [{ cid: 100, name: "C1", parts: [{value: 101}, {value: 102}], catalog: "C2", manufacturer: "M1" }]
     */
    public static DocumentInstance buildDocumentInstance2() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attributes = new ArrayList<>();
                {
                    attributes.add(new DIAtomicAttrValue("cid", new DIIntValue(100)));
                    attributes.add(new DIAtomicAttrValue("name", new DIStrValue("C1")));
                    // parts
                    {
                        List<DIDocument> partsDocs = new ArrayList<>();
                        {
                            List<DIAttrValue> value101 = Collections.singletonList(new DIAtomicAttrValue("value", new DIIntValue(101)));
                            partsDocs.add(new DIDocument(value101));
                            List<DIAttrValue> value102 = Collections.singletonList(new DIAtomicAttrValue("value", new DIIntValue(102)));
                            partsDocs.add(new DIDocument(value102));
                        }
                        DICollection parts = new DICollection("parts", partsDocs);
                        attributes.add(new DICollectionAttrValue(parts));
                    }
                    attributes.add(new DIAtomicAttrValue("catalog", new DIStrValue("C2")));
                    attributes.add(new DIAtomicAttrValue("manufacturer", new DIStrValue("M1")));
                }
                documents.add(new DIDocument(attributes));
            }
            DICollection computers = new DICollection("Computers", documents);
            collections.add(computers);
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * A: [{ a1: 100, B: [{ b1: 200, C: [{ c1: 300, c2: 400 }] }] }]
     */
    public static DocumentInstance buildDocumentInstance3() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> aDocs = new ArrayList<>();
            {
                List<DIAttrValue> aAttrs = new ArrayList<>();
                {
                    aAttrs.add(new DIAtomicAttrValue("a1", new DIIntValue(100)));
                    // B
                    {
                        List<DIDocument> bDocs = new ArrayList<>();
                        {
                            List<DIAttrValue> bAttrs = new ArrayList<>();
                            bAttrs.add(new DIAtomicAttrValue("b1", new DIIntValue(200)));
                            // C
                            {
                                List<DIDocument> cDocs = new ArrayList<>();
                                {
                                    List<DIAttrValue> cAttrs = new ArrayList<>();
                                    {
                                        cAttrs.add(new DIAtomicAttrValue("c1", new DIIntValue(300)));
                                        cAttrs.add(new DIAtomicAttrValue("c2", new DIIntValue(400)));
                                    }
                                    cDocs.add(new DIDocument(cAttrs));
                                }
                                DICollection cCollect = new DICollection("C", cDocs);
                                bAttrs.add(new DICollectionAttrValue(cCollect));
                            }
                            bDocs.add(new DIDocument(bAttrs));
                        }
                        DICollection bCollect = new DICollection("B", bDocs);
                        aAttrs.add(new DICollectionAttrValue(bCollect));
                    }
                }
                aDocs.add(new DIDocument(aAttrs));
            }
            collections.add(new DICollection("A", aDocs));
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * A: [{ a1: 1000, a2: 2000 }]
     * B: [{ b1: 3000, b2: 4000 }]
     */
    public static DocumentInstance buildDocumentInstance4() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> aDocs = new ArrayList<>();
            {
                List<DIAttrValue> aAttrs = new ArrayList<>();
                {
                    aAttrs.add(new DIAtomicAttrValue("a1", new DIIntValue(1000)));
                    aAttrs.add(new DIAtomicAttrValue("a2", new DIIntValue(2000)));
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
                    bAttrs.add(new DIAtomicAttrValue("b1", new DIIntValue(3000)));
                    bAttrs.add(new DIAtomicAttrValue("b2", new DIIntValue(4000)));
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
     * A: [{ a1: 100, B: [{ b1: 200, C: [{ c1: 300, c2: 400 }] }] },
     * { a1: 100, B: [{ b1: 200, C: [{ c1: 300, c2: 500 }] }] }]
     */
    public static DocumentInstance buildDocumentInstance5() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> aDocs = new ArrayList<>();
            {
                List<DIAttrValue> aAttrs = new ArrayList<>();
                {
                    aAttrs.add(new DIAtomicAttrValue("a1", new DIIntValue(100)));
                    // B
                    {
                        List<DIDocument> bDocs = new ArrayList<>();
                        {
                            List<DIAttrValue> bAttrs = new ArrayList<>();
                            bAttrs.add(new DIAtomicAttrValue("b1", new DIIntValue(200)));
                            // C
                            {
                                List<DIDocument> cDocs = new ArrayList<>();
                                {
                                    List<DIAttrValue> cAttrs = new ArrayList<>();
                                    {
                                        cAttrs.add(new DIAtomicAttrValue("c1", new DIIntValue(300)));
                                        cAttrs.add(new DIAtomicAttrValue("c2", new DIIntValue(400)));
                                    }
                                    cDocs.add(new DIDocument(cAttrs));
                                }
                                DICollection cCollect = new DICollection("C", cDocs);
                                bAttrs.add(new DICollectionAttrValue(cCollect));
                            }
                            bDocs.add(new DIDocument(bAttrs));
                        }
                        DICollection bCollect = new DICollection("B", bDocs);
                        aAttrs.add(new DICollectionAttrValue(bCollect));
                    }
                }
                aDocs.add(new DIDocument(aAttrs));

                List<DIAttrValue> aAttrs2 = new ArrayList<>();
                {
                    aAttrs2.add(new DIAtomicAttrValue("a1", new DIIntValue(100)));
                    // B
                    {
                        List<DIDocument> bDocs = new ArrayList<>();
                        {
                            List<DIAttrValue> bAttrs = new ArrayList<>();
                            bAttrs.add(new DIAtomicAttrValue("b1", new DIIntValue(200)));
                            // C
                            {
                                List<DIDocument> cDocs = new ArrayList<>();
                                {
                                    List<DIAttrValue> cAttrs = new ArrayList<>();
                                    {
                                        cAttrs.add(new DIAtomicAttrValue("c1", new DIIntValue(300)));
                                        cAttrs.add(new DIAtomicAttrValue("c2", new DIIntValue(500)));
                                    }
                                    cDocs.add(new DIDocument(cAttrs));
                                }
                                DICollection cCollect = new DICollection("C", cDocs);
                                bAttrs.add(new DICollectionAttrValue(cCollect));
                            }
                            bDocs.add(new DIDocument(bAttrs));
                        }
                        DICollection bCollect = new DICollection("B", bDocs);
                        aAttrs2.add(new DICollectionAttrValue(bCollect));
                    }
                }
                aDocs.add(new DIDocument(aAttrs2));
            }
            collections.add(new DICollection("A", aDocs));
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * A: [{ a1: 100, nestedContent: 200 }]
     */
    public static DocumentInstance buildDocumentInstance6() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> aDocs = new ArrayList<>();
            {
                List<DIAttrValue> aAttrs = new ArrayList<>();
                {
                    aAttrs.add(new DIAtomicAttrValue("a1", new DIIntValue(100)));
                    // B
                    {
                        List<DIDocument> bDocs = new ArrayList<>();
                        {
                            List<DIAttrValue> bAttrs = new ArrayList<>();
                            bAttrs.add(new DIAtomicAttrValue("b1", new DIIntValue(200)));
                            bAttrs.add(new DIAtomicAttrValue("nestedContent", new DIIntValue(300)));
                            bDocs.add(new DIDocument(bAttrs));
                        }
                        DICollection bCollect = new DICollection("B", bDocs);
                        aAttrs.add(new DICollectionAttrValue(bCollect));
                    }
                }
                aDocs.add(new DIDocument(aAttrs));
            }
            collections.add(new DICollection("A", aDocs));
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * A: [{ a: 100.5 }]
     */
    public static DocumentInstance buildDocumentInstanceFloat1() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attrs = new ArrayList<>();
                {
                    attrs.add(new DIAtomicAttrValue("a", new DIFloatValue("100.5")));
                }
                documents.add(new DIDocument(attrs));
            }
            collections.add(new DICollection("A", documents));
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * A: [{ a: 1.0 }]
     */
    public static DocumentInstance buildDocumentInstanceFloat2() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attrs = new ArrayList<>();
                {
                    attrs.add(new DIAtomicAttrValue("a", new DIFloatValue("1")));
                }
                documents.add(new DIDocument(attrs));
            }
            collections.add(new DICollection("A", documents));
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * A: [{ a: 1, b: null }]
     */
    public static DocumentInstance buildDocumentInstanceNull1() {
        List<DICollection> collections = new ArrayList<>();
        {
            List<DIDocument> documents = new ArrayList<>();
            {
                List<DIAttrValue> attrs = new ArrayList<>();
                {
                    attrs.add(new DIAtomicAttrValue("a", new DIIntValue(1)));
                    attrs.add(new DIAtomicAttrValue("b", DINullValue.getInstance()));
                }
                documents.add(new DIDocument(attrs));
            }
            collections.add(new DICollection("A", documents));
        }
        DocumentInstance instance = new DocumentInstance(collections);
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    /**
     * Computers: [{ cid: Int, name: String, manufacturer: String, catalog: String, parts: [{ value: Int }] }]
     */
    public static DocumentSchema buildDocumentSchema1() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> computersAttrs = new ArrayList<>();
            {
                computersAttrs.add(new DSAtomicAttr("cid", AttrType.INT));
                computersAttrs.add(new DSAtomicAttr("name", AttrType.STRING));
                computersAttrs.add(new DSAtomicAttr("manufacturer", AttrType.STRING));
                computersAttrs.add(new DSAtomicAttr("catalog", AttrType.STRING));
                // parts
                {
                    List<DSAttribute> partsAttrs = new ArrayList<>();
                    {
                        partsAttrs.add(new DSAtomicAttr("value", AttrType.INT));
                    }
                    DSCollection parts = new DSCollection("parts", new DSDocument(partsAttrs));
                    computersAttrs.add(new DSCollectionAttr(parts));
                }
            }
            DSCollection computers = new DSCollection("Computers", new DSDocument(computersAttrs));
            collections.add(computers);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * Computers: [{ cid: Int, name: String, parts: [{ value: Int }], catalog: String, manufacturer: String }]
     */
    public static DocumentSchema buildDocumentSchema2() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> computersAttrs = new ArrayList<>();
            {
                computersAttrs.add(new DSAtomicAttr("cid", AttrType.INT));
                computersAttrs.add(new DSAtomicAttr("name", AttrType.STRING));
                // parts
                {
                    List<DSAttribute> partsAttrs = new ArrayList<>();
                    {
                        partsAttrs.add(new DSAtomicAttr("value", AttrType.INT));
                    }
                    DSCollection parts = new DSCollection("parts", new DSDocument(partsAttrs));
                    computersAttrs.add(new DSCollectionAttr(parts));
                }
                computersAttrs.add(new DSAtomicAttr("catalog", AttrType.STRING));
                computersAttrs.add(new DSAtomicAttr("manufacturer", AttrType.STRING));
            }
            DSCollection computers = new DSCollection("Computers", new DSDocument(computersAttrs));
            collections.add(computers);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * A: [{ a1: Int, B: [{ b1: Int, C: [{ c1: Int, c2: Int }] }] }]
     */
    public static DocumentSchema buildDocumentSchema3() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> aAttrs = new ArrayList<>();
            {
                aAttrs.add(new DSAtomicAttr("a1", AttrType.INT));
                // B
                {
                    List<DSAttribute> bAttrs = new ArrayList<>();
                    {
                        bAttrs.add(new DSAtomicAttr("b1", AttrType.INT));
                        // C
                        {
                            List<DSAttribute> cAttrs = new ArrayList<>();
                            {
                                cAttrs.add(new DSAtomicAttr("c1", AttrType.INT));
                                cAttrs.add(new DSAtomicAttr("c2", AttrType.INT));
                            }
                            DSCollection cCollect = new DSCollection("C", new DSDocument(cAttrs));
                            bAttrs.add(new DSCollectionAttr(cCollect));
                        }
                    }
                    DSCollection bCollect = new DSCollection("B", new DSDocument(bAttrs));
                    aAttrs.add(new DSCollectionAttr(bCollect));
                }
            }
            DSCollection aCollect = new DSCollection("A", new DSDocument(aAttrs));
            collections.add(aCollect);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * A: [{ a1: Int, a2: Int }]
     * B: [{ b1: Int, b2: Int }]
     */
    public static DocumentSchema buildDocumentSchema4() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> aAttrs = new ArrayList<>();
            {
                aAttrs.add(new DSAtomicAttr("a1", AttrType.INT));
                aAttrs.add(new DSAtomicAttr("a2", AttrType.INT));
            }
            DSCollection aCollect = new DSCollection("A", new DSDocument(aAttrs));
            collections.add(aCollect);
        }
        {
            List<DSAttribute> bAttrs = new ArrayList<>();
            {
                bAttrs.add(new DSAtomicAttr("b1", AttrType.INT));
                bAttrs.add(new DSAtomicAttr("b2", AttrType.INT));
            }
            DSCollection bCollect = new DSCollection("B", new DSDocument(bAttrs));
            collections.add(bCollect);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * A: [{ a1: Int, B: [{b1: Int, nestedContent: Int}] }]
     */
    public static DocumentSchema buildDocumentSchema6() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> aAttrs = new ArrayList<>();
            {
                aAttrs.add(new DSAtomicAttr("a1", AttrType.INT));
                // B
                {
                    List<DSAttribute> bAttrs = new ArrayList<>();
                    {
                        bAttrs.add(new DSAtomicAttr("b1", AttrType.INT));
                        bAttrs.add(new DSAtomicAttr("nestedContent", AttrType.INT));
                    }
                    DSCollection bCollect = new DSCollection("B", new DSDocument(bAttrs));
                    aAttrs.add(new DSCollectionAttr(bCollect));
                }
            }
            DSCollection aCollect = new DSCollection("A", new DSDocument(aAttrs));
            collections.add(aCollect);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * A: [{ a1: Int, B: [{b1: Int}] }]
     */
    public static DocumentSchema buildDocumentSchema7() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> aAttrs = new ArrayList<>();
            {
                aAttrs.add(new DSAtomicAttr("a1", AttrType.INT));
                // B
                {
                    List<DSAttribute> bAttrs = new ArrayList<>();
                    {
                        bAttrs.add(new DSAtomicAttr("b1", AttrType.INT));
                    }
                    DSCollection bCollect = new DSCollection("B", new DSDocument(bAttrs));
                    aAttrs.add(new DSCollectionAttr(bCollect));
                }
            }
            DSCollection aCollect = new DSCollection("A", new DSDocument(aAttrs));
            collections.add(aCollect);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * A: [{ a: Float }]
     */
    public static DocumentSchema buildDocumentSchemaFloat1() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> aAttrs = new ArrayList<>();
            {
                aAttrs.add(new DSAtomicAttr("a", AttrType.FLOAT));
            }
            DSCollection aCollect = new DSCollection("A", new DSDocument(aAttrs));
            collections.add(aCollect);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    /**
     * A: [{ a: Int, b: String }]
     */
    public static DocumentSchema buildDocumentSchemaNull1() {
        List<DSCollection> collections = new ArrayList<>();
        {
            List<DSAttribute> aAttrs = new ArrayList<>();
            {
                aAttrs.add(new DSAtomicAttr("a", AttrType.INT));
                aAttrs.add(new DSAtomicAttr("b", AttrType.STRING));
            }
            DSCollection aCollect = new DSCollection("A", new DSDocument(aAttrs));
            collections.add(aCollect);
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

}

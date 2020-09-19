package dynamite.docdb;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DocumentSchema;

public class DocSchemaCanonicalNameVisitorTest {

    @Test
    public void testSchema1() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        DSCollection collectionComputers = schema.collections.get(0);
        Assert.assertEquals("Computers", collectionComputers.getCanonicalName());
        List<DSAttribute> computersAttrs = collectionComputers.document.attributes;
        Assert.assertEquals("Computers?cid", computersAttrs.get(0).getCanonicalName());
        Assert.assertEquals("Computers?name", computersAttrs.get(1).getCanonicalName());
        Assert.assertEquals("Computers?manufacturer", computersAttrs.get(2).getCanonicalName());
        Assert.assertEquals("Computers?catalog", computersAttrs.get(3).getCanonicalName());
        Assert.assertEquals("Computers?parts", computersAttrs.get(4).getCanonicalName());
        DSCollectionAttr partsCollectionAttr = (DSCollectionAttr) computersAttrs.get(4);
        DSCollection collectionParts = partsCollectionAttr.collection;
        Assert.assertEquals("Computers?parts", collectionParts.getCanonicalName());
        List<DSAttribute> partsAttrs = collectionParts.document.attributes;
        Assert.assertEquals("Computers?parts?value", partsAttrs.get(0).getCanonicalName());
    }

    @Test
    public void testSchema2() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        DSCollection collectionComputers = schema.collections.get(0);
        Assert.assertEquals("Computers", collectionComputers.getCanonicalName());
        List<DSAttribute> computersAttrs = collectionComputers.document.attributes;
        Assert.assertEquals("Computers?cid", computersAttrs.get(0).getCanonicalName());
        Assert.assertEquals("Computers?name", computersAttrs.get(1).getCanonicalName());
        Assert.assertEquals("Computers?parts", computersAttrs.get(2).getCanonicalName());
        Assert.assertEquals("Computers?catalog", computersAttrs.get(3).getCanonicalName());
        Assert.assertEquals("Computers?manufacturer", computersAttrs.get(4).getCanonicalName());
        DSCollectionAttr partsCollectionAttr = (DSCollectionAttr) computersAttrs.get(2);
        DSCollection collectionParts = partsCollectionAttr.collection;
        Assert.assertEquals("Computers?parts", collectionParts.getCanonicalName());
        List<DSAttribute> partsAttrs = collectionParts.document.attributes;
        Assert.assertEquals("Computers?parts?value", partsAttrs.get(0).getCanonicalName());
    }

    @Test
    public void testSchema3() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        DSCollection collectionA = schema.collections.get(0);
        Assert.assertEquals("A", collectionA.getCanonicalName());
        List<DSAttribute> aAttrs = collectionA.document.attributes;
        Assert.assertEquals("A?a1", aAttrs.get(0).getCanonicalName());
        Assert.assertEquals("A?B", aAttrs.get(1).getCanonicalName());
        DSCollectionAttr bCollectionAttr = (DSCollectionAttr) aAttrs.get(1);
        DSCollection collectionB = bCollectionAttr.collection;
        Assert.assertEquals("A?B", collectionB.getCanonicalName());
        List<DSAttribute> bAttrs = collectionB.document.attributes;
        Assert.assertEquals("A?B?b1", bAttrs.get(0).getCanonicalName());
        Assert.assertEquals("A?B?C", bAttrs.get(1).getCanonicalName());
        DSCollectionAttr cCollectionAttr = (DSCollectionAttr) bAttrs.get(1);
        DSCollection collectionC = cCollectionAttr.collection;
        Assert.assertEquals("A?B?C", collectionC.getCanonicalName());
        List<DSAttribute> cAttrs = collectionC.document.attributes;
        Assert.assertEquals("A?B?C?c1", cAttrs.get(0).getCanonicalName());
        Assert.assertEquals("A?B?C?c2", cAttrs.get(1).getCanonicalName());
    }

    @Test
    public void testSchema4() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        DSCollection collectionA = schema.collections.get(0);
        Assert.assertEquals("A", collectionA.getCanonicalName());
        List<DSAttribute> aAttrs = collectionA.document.attributes;
        Assert.assertEquals("A?a1", aAttrs.get(0).getCanonicalName());
        Assert.assertEquals("A?a2", aAttrs.get(1).getCanonicalName());
        DSCollection collectionB = schema.collections.get(1);
        Assert.assertEquals("B", collectionB.getCanonicalName());
        List<DSAttribute> bAttrs = collectionB.document.attributes;
        Assert.assertEquals("B?b1", bAttrs.get(0).getCanonicalName());
        Assert.assertEquals("B?b2", bAttrs.get(1).getCanonicalName());
    }
}

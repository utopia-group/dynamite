package dynamite.docdb;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DocumentInstance;

public class DocInstCanonicalNameVisitorTest {

    @Test
    public void testInstance1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance1();
        DICollection collectionComputers = instance.collections.get(0);
        Assert.assertEquals("Computers", collectionComputers.getCanonicalName());
        List<DIAttrValue> computersAttrs = collectionComputers.documents.get(0).attributes;
        Assert.assertEquals("Computers?cid", computersAttrs.get(0).getCanonicalName());
        Assert.assertEquals("Computers?name", computersAttrs.get(1).getCanonicalName());
        Assert.assertEquals("Computers?manufacturer", computersAttrs.get(2).getCanonicalName());
        Assert.assertEquals("Computers?catalog", computersAttrs.get(3).getCanonicalName());
        Assert.assertEquals("Computers?parts", computersAttrs.get(4).getCanonicalName());
        DICollectionAttrValue partsCollectionAttr = (DICollectionAttrValue) computersAttrs.get(4);
        DICollection collectionParts = partsCollectionAttr.collection;
        Assert.assertEquals("Computers?parts", collectionParts.getCanonicalName());
        List<DIAttrValue> partsAttrs = collectionParts.documents.get(0).attributes;
        Assert.assertEquals("Computers?parts?value", partsAttrs.get(0).getCanonicalName());
    }

    @Test
    public void testInstance2() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance2();
        DICollection collectionComputers = instance.collections.get(0);
        Assert.assertEquals("Computers", collectionComputers.getCanonicalName());
        List<DIAttrValue> computersAttrs = collectionComputers.documents.get(0).attributes;
        Assert.assertEquals("Computers?cid", computersAttrs.get(0).getCanonicalName());
        Assert.assertEquals("Computers?name", computersAttrs.get(1).getCanonicalName());
        Assert.assertEquals("Computers?parts", computersAttrs.get(2).getCanonicalName());
        Assert.assertEquals("Computers?catalog", computersAttrs.get(3).getCanonicalName());
        Assert.assertEquals("Computers?manufacturer", computersAttrs.get(4).getCanonicalName());
        DICollectionAttrValue partsCollectionAttr = (DICollectionAttrValue) computersAttrs.get(2);
        DICollection collectionParts = partsCollectionAttr.collection;
        Assert.assertEquals("Computers?parts", collectionParts.getCanonicalName());
        List<DIAttrValue> partsAttrs = collectionParts.documents.get(0).attributes;
        Assert.assertEquals("Computers?parts?value", partsAttrs.get(0).getCanonicalName());
    }

    @Test
    public void testInstance3() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance3();
        DICollection collectionA = instance.collections.get(0);
        Assert.assertEquals("A", collectionA.getCanonicalName());
        List<DIAttrValue> aAttrs = collectionA.documents.get(0).attributes;
        Assert.assertEquals("A?a1", aAttrs.get(0).getCanonicalName());
        Assert.assertEquals("A?B", aAttrs.get(1).getCanonicalName());
        DICollectionAttrValue bCollectionAttr = (DICollectionAttrValue) aAttrs.get(1);
        DICollection collectionB = bCollectionAttr.collection;
        Assert.assertEquals("A?B", collectionB.getCanonicalName());
        List<DIAttrValue> bAttrs = collectionB.documents.get(0).attributes;
        Assert.assertEquals("A?B?b1", bAttrs.get(0).getCanonicalName());
        Assert.assertEquals("A?B?C", bAttrs.get(1).getCanonicalName());
        DICollectionAttrValue cCollectionAttr = (DICollectionAttrValue) bAttrs.get(1);
        DICollection collectionC = cCollectionAttr.collection;
        Assert.assertEquals("A?B?C", collectionC.getCanonicalName());
        List<DIAttrValue> cAttrs = collectionC.documents.get(0).attributes;
        Assert.assertEquals("A?B?C?c1", cAttrs.get(0).getCanonicalName());
        Assert.assertEquals("A?B?C?c2", cAttrs.get(1).getCanonicalName());
    }

    @Test
    public void testInstance4() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance4();
        DICollection collectionA = instance.collections.get(0);
        Assert.assertEquals("A", collectionA.getCanonicalName());
        List<DIAttrValue> aAttrs = collectionA.documents.get(0).attributes;
        Assert.assertEquals("A?a1", aAttrs.get(0).getCanonicalName());
        Assert.assertEquals("A?a2", aAttrs.get(1).getCanonicalName());
        DICollection collectionB = instance.collections.get(1);
        Assert.assertEquals("B", collectionB.getCanonicalName());
        List<DIAttrValue> bAttrs = collectionB.documents.get(0).attributes;
        Assert.assertEquals("B?b1", bAttrs.get(0).getCanonicalName());
        Assert.assertEquals("B?b2", bAttrs.get(1).getCanonicalName());
    }
}

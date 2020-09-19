package dynamite.docdb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DocumentInstance;

public class DocInstSamplingVisitorTest {

    private static DocumentInstance buildExpectedDocumentInstance1() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance5();
        List<DICollection> collections = new ArrayList<>();
        {
            DICollection collection = instance.collections.get(0);
            List<DIDocument> documents = new ArrayList<>();
            {
                documents.add(collection.documents.get(0));
            }
            collections.add(new DICollection(collection.name, documents));
        }
        return new DocumentInstance(collections);
    }

    private static DocumentInstance buildExpectedDocumentInstance2() {
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance5();
        List<DICollection> collections = new ArrayList<>();
        {
            DICollection collection = instance.collections.get(0);
            List<DIDocument> documents = new ArrayList<>();
            {
                documents.add(collection.documents.get(1));
            }
            collections.add(new DICollection(collection.name, documents));
        }
        return new DocumentInstance(collections);
    }

    private static DocumentInstance buildExpectedDocumentInstance3() {
        return DocDbTestUtils.buildDocumentInstance5();
    }

    @Test
    public void testSample() {
        int maxDocCount = 2;
        DocumentInstance instance = DocDbTestUtils.buildDocumentInstance5();
        BitSet indices1 = BitSet.valueOf(new long[] { 1 });
        DocumentInstance actual1 = instance.accept(new DocInstSamplingVisitor(indices1, maxDocCount));
        DocumentInstance expected1 = buildExpectedDocumentInstance1();
        Assert.assertEquals(expected1, actual1);
        BitSet indices2 = BitSet.valueOf(new long[] { 2 });
        DocumentInstance actual2 = instance.accept(new DocInstSamplingVisitor(indices2, maxDocCount));
        DocumentInstance expected2 = buildExpectedDocumentInstance2();
        BitSet indices3 = BitSet.valueOf(new long[] { 3 });
        Assert.assertEquals(expected2, actual2);
        DocumentInstance actual3 = instance.accept(new DocInstSamplingVisitor(indices3, maxDocCount));
        DocumentInstance expected3 = buildExpectedDocumentInstance3();
        Assert.assertEquals(expected3, actual3);
    }

}

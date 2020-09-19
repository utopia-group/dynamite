package dynamite.core;

import org.junit.Assert;
import org.junit.Test;

import dynamite.docdb.ast.DICollection;

public class ValueCorrEstimatorTest {

    @Test
    public void test1() {
        InstanceExample example = CoreTestUtils.buildExample1();
        IInstance srcInst = example.sourceInstance;
        DICollection tgtCollection = example.targetInstance.toDocumentInstance().collections.get(0);
        ValueCorr actual = ValueCorrEstimator.infer(srcInst, tgtCollection);
        ValueCorr expected = CoreTestUtils.buildValueCorr1();
        Assert.assertEquals(expected, actual);
    }

}

package dynamite.core;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.DatalogOutput;
import dynamite.datalog.SouffleEvaluator;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.reldb.ast.RelationalSchema;

public class MDPSynthesizerTest {

    @Test
    public void test1() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema1();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema1();
        InstanceExample example = CoreTestUtils.buildExample1();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram1());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema2();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema2();
        InstanceExample example = CoreTestUtils.buildExample2();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram2());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2a() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema2();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema2();
        InstanceExample example = CoreTestUtils.buildExample2a();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram2a());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2b() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema2();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema2();
        InstanceExample example = CoreTestUtils.buildExample2b();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram2b());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2c() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema2();
        RelationalSchema tgtSchema = CoreTestUtils.buildRelTgtSchema2();
        InstanceExample example = CoreTestUtils.buildExample2c();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram2c());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2d() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema2();
        RelationalSchema tgtSchema = CoreTestUtils.buildRelTgtSchema2();
        InstanceExample example = CoreTestUtils.buildExample2d();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram2d());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema3();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema3();
        InstanceExample example = CoreTestUtils.buildExample3();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram3());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3a() {
        RelationalSchema srcSchema = CoreTestUtils.buildRelSrcSchema3();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema3();
        InstanceExample example = CoreTestUtils.buildExample3a();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram3a());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test4() {
        RelationalSchema srcSchema = CoreTestUtils.buildRelSrcSchema4();
        RelationalSchema tgtSchema = CoreTestUtils.buildRelTgtSchema4();
        InstanceExample example = CoreTestUtils.buildExample4();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram4());
        Assert.assertEquals(expected, actual);
    }

}

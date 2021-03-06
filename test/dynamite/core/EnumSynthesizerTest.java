package dynamite.core;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.DatalogOutput;
import dynamite.datalog.SouffleEvaluator;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.reldb.ast.RelationalSchema;

public class EnumSynthesizerTest {

    @Test
    public void test1() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema1();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema1();
        InstanceExample example = CoreTestUtils.buildExample1();
        EnumSynthesizer synthesizer = new EnumSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram1());
        Assert.assertEquals(expected, actual);
    }

    /**
     * This is a flaky test, because the actual program is not syntactically unique.
     * The actual program depends on the model returned by the SMT solver.
     * Ignored in the test suite.
     */
    public void flakyTest2() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema2();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema2();
        InstanceExample example = CoreTestUtils.buildExample2();
        EnumSynthesizer synthesizer = new EnumSynthesizer();
        SchemaMapping actual = synthesizer.synthesize(srcSchema, tgtSchema, example);
        SchemaMapping expected = new SchemaMapping(CoreTestUtils.buildDatalogProgram2());
        Assert.assertEquals(expected.program.toSouffle(), actual.program.toSouffle());
    }

    @Test
    public void test2() {
        DocumentSchema srcSchema = CoreTestUtils.buildDocSrcSchema2();
        DocumentSchema tgtSchema = CoreTestUtils.buildDocTgtSchema2();
        InstanceExample example = CoreTestUtils.buildExample2();
        EnumSynthesizer synthesizer = new EnumSynthesizer();
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
        EnumSynthesizer synthesizer = new EnumSynthesizer();
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
        EnumSynthesizer synthesizer = new EnumSynthesizer();
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
        EnumSynthesizer synthesizer = new EnumSynthesizer();
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
        EnumSynthesizer synthesizer = new EnumSynthesizer();
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
        EnumSynthesizer synthesizer = new EnumSynthesizer();
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
        EnumSynthesizer synthesizer = new EnumSynthesizer();
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
        EnumSynthesizer synthesizer = new EnumSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        DatalogOutput actual = new SouffleEvaluator().evaluate(schemaMapping.program);
        DatalogOutput expected = new SouffleEvaluator().evaluate(CoreTestUtils.buildDatalogProgram4());
        Assert.assertEquals(expected, actual);
    }

}

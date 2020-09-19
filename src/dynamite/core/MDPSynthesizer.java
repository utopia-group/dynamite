package dynamite.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import dynamite.LoggerWrapper;
import dynamite.datalog.DatalogOutput;
import dynamite.datalog.IDatalogEvaluator;
import dynamite.datalog.SearchSpaceSizeVisitor;
import dynamite.datalog.SouffleEvaluator;
import dynamite.datalog.UniverseGenVisitor;
import dynamite.datalog.UniverseRemovalVisitor;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.smt.SmtModel;
import dynamite.smt.SmtSolver;
import dynamite.smt.SmtSolver.Status;
import dynamite.smt.ast.Constraint;

public class MDPSynthesizer implements ISynthesizer {

    protected static final Logger LOGGER = LoggerWrapper.getInstance();

    @Override
    public SchemaMapping synthesize(ISchema srcSchema, ISchema tgtSchema, InstanceExample example) {
        DocumentSchema srcDocSchema = srcSchema.toDocumentSchema();
        DocumentSchema tgtDocSchema = tgtSchema.toDocumentSchema();
        IInstance srcInst = example.sourceInstance;
        DocumentInstance tgtDocInst = example.targetInstance.toDocumentInstance();
        List<DatalogProgram> programs = new ArrayList<>();
        for (DSCollection schemaCollection : tgtDocSchema.collections) {
            DICollection tgtCollection = tgtDocInst.findTopLevelCollectionByName(schemaCollection.name);
            DatalogProgram program = synthOneCollection(srcDocSchema, schemaCollection, srcInst, tgtCollection);
            if (program == null) {
                return null;
            }
            programs.add(program);
        }
        SchemaMapping schemaMapping = SchemaMapping.fromDatalogPrograms(programs);
        return schemaMapping;
    }

    /**
     * Synthesize a Datalog program as schema mapping for one document collection in the target schema.
     *
     * @param srcDocSchema        source document schema
     * @param tgtCollectionSchema target document schema for one collection
     * @param srcInst             source instance
     * @param tgtCollection       target document collection
     * @return
     */
    private DatalogProgram synthOneCollection(
            DocumentSchema srcDocSchema,
            DSCollection tgtCollectionSchema,
            IInstance srcInst,
            DICollection tgtCollection) {
        long startTime = System.currentTimeMillis();
        LOGGER.info("=== Target: " + tgtCollection.name);

        IDatalogEvaluator evaluator = new SouffleEvaluator();
        DatalogOutput expectedOutput = normalizeOutput(tgtCollection);
        LOGGER.fine("--- Expected Output:\n" + expectedOutput);

        ValueCorr valueCorr = ValueCorrEstimator.infer(srcInst, tgtCollection);
        LOGGER.fine("--- Attribute Mapping:\n" + valueCorr);
        Sketch sketch = SketchGenerator.generate(valueCorr, srcInst, srcDocSchema, tgtCollectionSchema);
        LOGGER.fine("--- Sketch:\n" + sketch);
        LOGGER.info("=== Search Space: " + sketch.program.accept(new SearchSpaceSizeVisitor()));
        SketchEncoder encoder = new SketchEncoder(sketch);
        EncodingMap encodingMap = encoder.getEncodingMap();
        LOGGER.fine("--- Encoding Map:\n" + encodingMap);
        Constraint cstr = encoder.encode();
        LOGGER.fine("--- Constraint:\n" + cstr);
        SmtSolver solver = new SmtSolver();
        solver.addConstraint(cstr);
        long iter = 0;
        while (solver.checkSAT() == Status.SAT) {
            ++iter;
            LOGGER.fine("--- Iter: " + iter);
            LOGGER.fine("--- Epoch: " + System.currentTimeMillis());
            SmtModel model = solver.getModel();
            DatalogProgram program = buildProgram(sketch, model, encodingMap);
            LOGGER.fine("--- Candidate Program:\n" + program.toSouffle());
            DatalogProgram progWithUniv = program.accept(new UniverseGenVisitor());
            DatalogOutput actualOutput = evaluator.evaluate(progWithUniv);
            LOGGER.fine("--- Actual Output:\n" + actualOutput);
            if (actualOutput.equals(expectedOutput)) {
                DatalogProgram progWithoutUniv = progWithUniv.accept(new UniverseRemovalVisitor());
                LOGGER.info("=== Iters: " + iter);
                LOGGER.info("=== Time: " + (System.currentTimeMillis() - startTime));
                LOGGER.fine("--- Success with Program:\n" + progWithoutUniv.toSouffle());
                return progWithoutUniv;
            }
            Constraint blockingCstr = analyzeMistake(model, encodingMap, sketch, actualOutput, expectedOutput);
            LOGGER.fine("--- Blocking Constraint:\n" + blockingCstr);
            solver.addConstraint(blockingCstr);
        }
        LOGGER.info("=== Iters: " + iter);
        LOGGER.info("=== Time: " + (System.currentTimeMillis() - startTime));
        LOGGER.fine("--- Synthesis Failure");
        return null;
    }

    private DatalogOutput normalizeOutput(DICollection collection) {
        DocumentInstance instance = new DocumentInstance(Collections.singletonList(collection));
        return instance.toDatalogOutput();
    }

    private DatalogProgram buildProgram(Sketch sketch, SmtModel model, EncodingMap encodingMap) {
        return sketch.program.accept(new DatalogSubstituter(model, encodingMap));
    }

    protected Constraint analyzeMistake(SmtModel model, EncodingMap encMap, Sketch sketch, DatalogOutput actual, DatalogOutput expected) {
        return new MistakeAnalyzer().analyze(model, encMap, sketch, actual, expected);
    }

}

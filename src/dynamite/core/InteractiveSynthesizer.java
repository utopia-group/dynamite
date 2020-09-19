package dynamite.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import dynamite.InteractiveSchemaMapper;
import dynamite.datalog.DatalogOutput;
import dynamite.datalog.SimplificationVisitor;
import dynamite.datalog.SouffleEvaluator;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.interact.DatabaseSampler;
import dynamite.interact.DatalogDifferentiator;
import dynamite.smt.SmtModel;
import dynamite.smt.SmtSolver;
import dynamite.smt.SmtSolver.Status;
import dynamite.smt.ast.Constraint;
import dynamite.smt.ast.LAnd;
import dynamite.smt.ast.LBinPred;
import dynamite.smt.ast.LBinPred.Lop;
import dynamite.smt.ast.LConst;
import dynamite.smt.ast.LFormula;
import dynamite.smt.ast.LIntLiteral;
import dynamite.smt.ast.LNot;
import dynamite.util.DatalogAstUtils;
import dynamite.util.InstanceUtils;

public final class InteractiveSynthesizer {

    public static enum SynthStatus {
        SUCCEED,
        FAIL,
        NEED_INPUT,
        REACH_LIMIT,
    }

    public static final Logger LOGGER = InteractiveSchemaMapper.LOGGER;

    // maximum rounds of user interaction
    public static final int MAX_ROUNDS = 5;
    // maximum number of consistent programs to check
    public static final int MAX_PROGRAM_COUNT = 100;
    // maximum number of iterations per program
    public static final int MAX_ITERATIONS = 1000;
    // random seed used to generate the first source example
    public static long RANDOM_SEED;

    // sample source instance
    private final IInstance srcInst;
    // the collection of interest in the target document schema
    private final DSCollection tgtCollection;
    // source document schema
    private final DocumentSchema srcDocSchema;
    // all synthesized Datalog Programs that are syntactically unique
    private final List<DatalogProgram> programs = new ArrayList<>();
    // Souffle string of synthesized Datalog programs
    private final Set<String> programTexts = new HashSet<>();
    // all source examples
    private final List<IInstance> srcExamples = new ArrayList<>();
    // all target examples
    private final List<IInstance> tgtExamples = new ArrayList<>();
    // current round of user interaction
    private int currentRound = 0;
    // current number of consistent program checked
    private int programCount = 0;
    // current number of iterations for the current program
    private int currentIter = 0;

    public InteractiveSynthesizer(ISchema srcSchema, IInstance srcInst, DSCollection tgtCollection, long randomSeed) {
        Objects.requireNonNull(srcSchema);
        Objects.requireNonNull(srcInst);
        Objects.requireNonNull(tgtCollection);
        this.srcDocSchema = srcSchema.toDocumentSchema();
        this.srcInst = srcInst;
        this.tgtCollection = tgtCollection;
        RANDOM_SEED = randomSeed;
    }

    public IInstance sampleInitialInstance() {
        if (!srcExamples.isEmpty()) throw new IllegalStateException();
        Random random = new Random();
        random.setSeed(RANDOM_SEED);
        int initIndex = random.nextInt(InstanceUtils.computeInstanceSize(srcInst));
        BitSet bitSet = new BitSet();
        bitSet.set(initIndex);
        IInstance srcExample = DatabaseSampler.sample(srcInst, bitSet);
        srcExamples.add(srcExample);
        currentRound = 1;
        return srcExample;
    }

    public void loadTargetExample(IInstance tgtExample) {
        tgtExamples.add(tgtExample);
    }

    public IInstance getCurrSourceExample() {
        if (srcExamples.isEmpty()) throw new IllegalStateException();
        return srcExamples.get(srcExamples.size() - 1);
    }

    public IInstance getCurrTargetExample() {
        if (tgtExamples.isEmpty()) throw new IllegalStateException();
        return tgtExamples.get(tgtExamples.size() - 1);
    }

    public DatalogProgram getCurrentProgram() {
        if (programs.isEmpty()) throw new IllegalStateException();
        return programs.get(programs.size() - 1);
    }

    public DatalogProgram getBestProgram() {
        if (programs.isEmpty()) throw new IllegalStateException();
        DocumentInstance srcExample = buildExampleInstance(srcExamples);
        DocumentInstance tgtExample = buildExampleInstance(tgtExamples);
        DICollection tgtExCollection = tgtExample.findTopLevelCollectionByName(tgtCollection.name);
        DatalogOutput expectedOutput = normalizeOutput(tgtExCollection);
        DatalogProgram bestProg = programs.get(0);
        int minBodyPredCount = DatalogAstUtils.countBodyPredicates(bestProg);
        for (int i = 1; i < programs.size(); ++i) {
            DatalogProgram program = programs.get(i);
            if (SouffleEvaluator.evaluateOnInstance(program, srcExample).equals(expectedOutput)) {
                int bodyPredCount = DatalogAstUtils.countBodyPredicates(program);
                if (bodyPredCount < minBodyPredCount) {
                    bestProg = program;
                    minBodyPredCount = bodyPredCount;
                }
            }
        }
        return bestProg;
    }

    private DocumentInstance buildExampleInstance(List<IInstance> examples) {
        Map<String, DICollection> nameToCollectionMap = new LinkedHashMap<>();
        for (IInstance example : examples) {
            DocumentInstance instance = example.toDocumentInstance();
            for (DICollection collection : instance.collections) {
                String name = collection.name;
                if (nameToCollectionMap.containsKey(name)) {
                    DICollection prevCollection = nameToCollectionMap.get(name);
                    nameToCollectionMap.put(name, InstanceUtils.mergeCollection(prevCollection, collection));
                } else {
                    nameToCollectionMap.put(name, collection);
                }
            }
        }
        List<DICollection> collections = new ArrayList<>();
        for (Map.Entry<String, DICollection> entry : nameToCollectionMap.entrySet()) {
            collections.add(entry.getValue());
        }
        return new DocumentInstance(collections);
    }

    // sketch
    private Sketch sketch = null;
    // encoding map
    private EncodingMap encodingMap = null;
    // current SMT solver with constraints
    private final SmtSolver solver = new SmtSolver();

    private void initialize(DocumentInstance srcExample, DICollection tgtExCollection) {
        ValueCorr valueCorr = ValueCorrEstimator.infer(srcExample, tgtExCollection);
        sketch = SketchGenerator.generate(valueCorr, srcExample, srcDocSchema, tgtCollection);
        SketchEncoder encoder = new SketchEncoder(sketch);
        encodingMap = encoder.getEncodingMap();
        Constraint cstr = encoder.encode();
        solver.addConstraint(cstr);
    }

    public SynthStatus synthesizeNext() {
        DocumentInstance srcExample = buildExampleInstance(srcExamples);
        DocumentInstance tgtExample = buildExampleInstance(tgtExamples);
        DICollection tgtExCollection = tgtExample.findTopLevelCollectionByName(tgtCollection.name);
        DatalogOutput expectedOutput = normalizeOutput(tgtExCollection);

        if (sketch == null) {
            initialize(srcExample, tgtExCollection);
        }

        currentIter = 0;
        while (solver.checkSAT() == Status.SAT) {
            ++currentIter;
            if (currentIter > MAX_ITERATIONS) {
                LOGGER.info("=== Reached maximum number of iterations");
                return programs.isEmpty() ? SynthStatus.FAIL : SynthStatus.REACH_LIMIT;
            }
            SmtModel model = solver.getModel();
            DatalogProgram program = buildProgram(sketch, model, encodingMap);
            DatalogOutput actualOutput = SouffleEvaluator.evaluateOnInstance(program, srcExample);
            if (actualOutput.equals(expectedOutput)) {
                DatalogProgram simplified = program.accept(new SimplificationVisitor());
                storeDatalogProgram(simplified);
                LOGGER.info("=== Time: " + new Timestamp(System.currentTimeMillis()));
                LOGGER.info("=== Source Example: " + getCurrSourceExample().toInstanceString());
                LOGGER.info("=== Target Example: " + getCurrTargetExample().toInstanceString());
                LOGGER.info("=== Combined Source Example: " + srcExample.toInstanceString());
                LOGGER.info("=== Combined Target Example: " + tgtExample.toInstanceString());
                LOGGER.info("=== Program:\n" + new DatalogProgram(simplified.declarations, simplified.rules, Collections.emptyList()).toSouffle());
                // find the a different program that is also consistent with the current examples
                return findNextProgram(simplified, srcExample, model, expectedOutput);
            }
            Constraint blockingCstr = analyzeMistake(model, encodingMap, sketch, actualOutput, expectedOutput);
            solver.addConstraint(blockingCstr);
        }
        return SynthStatus.FAIL;
    }

    private SynthStatus findNextProgram(DatalogProgram currProg, IInstance srcExample, SmtModel currModel, DatalogOutput expectedOutput) {
        solver.addConstraint(genConstraintForModel(currModel));
        while (solver.checkSAT() == Status.SAT) {
            ++programCount;
            if (programCount > MAX_PROGRAM_COUNT) return SynthStatus.REACH_LIMIT;
            LOGGER.info("=== Program Count: " + programCount);
            SmtModel model = solver.getModel();
            DatalogProgram program = buildProgram(sketch, model, encodingMap);
            DatalogOutput actualOutput = SouffleEvaluator.evaluateOnInstance(program, srcExample);
            if (actualOutput.equals(expectedOutput)) {
                DatalogProgram simplified = program.accept(new SimplificationVisitor());
                if (isExistingProgram(simplified)) {
                    solver.addConstraint(genConstraintForModel(model));
                    continue;
                } else { // found a syntactically new program
                    storeDatalogProgram(simplified);
                    IInstance newSrcEx = DatalogDifferentiator.checkDifference(currProg, simplified, srcInst);
                    if (newSrcEx == null) {
                        solver.addConstraint(genConstraintForModel(model));
                        continue;
                    } else {
                        srcExamples.add(newSrcEx);
                        ++currentRound;
                        return currentRound <= MAX_ROUNDS ? SynthStatus.NEED_INPUT : SynthStatus.REACH_LIMIT;
                    }
                }
            } else {
                Constraint blockingCstr = analyzeMistake(model, encodingMap, sketch, actualOutput, expectedOutput);
                solver.addConstraint(blockingCstr);
            }
        }
        return SynthStatus.SUCCEED;
    }

    private void storeDatalogProgram(DatalogProgram program) {
        String programText = program.toSouffle();
        if (!programTexts.contains(programText)) {
            programs.add(program);
            programTexts.add(programText);
        }
    }

    private boolean isExistingProgram(DatalogProgram program) {
        return programTexts.contains(program.toSouffle());
    }

    private DatalogOutput normalizeOutput(DICollection collection) {
        DocumentInstance instance = new DocumentInstance(Collections.singletonList(collection));
        return instance.toDatalogOutput();
    }

    private DatalogProgram buildProgram(Sketch sketch, SmtModel model, EncodingMap encodingMap) {
        return sketch.program.accept(new DatalogSubstituter(model, encodingMap));
    }

    private Constraint analyzeMistake(SmtModel model, EncodingMap encMap, Sketch sketch, DatalogOutput actual, DatalogOutput expected) {
        return new MistakeAnalyzer().analyze(model, encMap, sketch, actual, expected);
    }

    private Constraint genConstraintForModel(SmtModel model) {
        List<LFormula> conjuncts = new ArrayList<>();
        for (String key : model.keySet()) {
            conjuncts.add(new LBinPred(Lop.EQ, new LConst(key), new LIntLiteral(model.getValue(key))));
        }
        LNot blockingClause = new LNot(new LAnd(conjuncts));
        return new Constraint(Collections.singletonList(blockingClause));
    }

}

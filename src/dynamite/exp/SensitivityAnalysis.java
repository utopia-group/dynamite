package dynamite.exp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dynamite.core.IInstance;
import dynamite.core.ISchema;
import dynamite.core.InstanceExample;
import dynamite.core.MDPSynthesizer;
import dynamite.core.SchemaMapping;
import dynamite.datalog.DatalogOutput;
import dynamite.datalog.SafeSimplifier;
import dynamite.datalog.SouffleEvaluator;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.InputDeclaration;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.docdb.OutputToDocInstVisitor;
import dynamite.docdb.RelationDeclGenVisitor;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;

public class SensitivityAnalysis {

    public static SchemaMapping mapSchema(
            Logger logger,
            String benchmark,
            ISchema srcSchema,
            ISchema tgtSchema,
            IntegrityConstraint constraint,
            DatalogProgram goldenProgram,
            int size,
            long randomSeed) {

        long instGenStartTime = System.currentTimeMillis();
        IInstance srcInst = genInstAfterFilter(benchmark, srcSchema, constraint, size, randomSeed);
        DatalogProgram program = updateProgramDecls(goldenProgram, srcSchema);
        DatalogOutput goldenOutput = SouffleEvaluator.evaluateOnInstance(program, srcInst);
        DocumentSchema tgtDocSchema = tgtSchema.toDocumentSchema();
        DocumentInstance tgtDocInst = genDocInst(tgtDocSchema, goldenOutput);
        InstanceExample example = new InstanceExample(srcInst, tgtDocInst);
        long instGenEndTime = System.currentTimeMillis();
        logger.info("=== InstGen Time: " + (instGenEndTime - instGenStartTime));

        long synthStartTime = System.currentTimeMillis();
        MDPSynthesizer synthesizer = new MDPSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtDocSchema, example);
        if (schemaMapping == null) return null;
        // simplify the Datalog program
        SchemaMapping simplified = new SchemaMapping(SafeSimplifier.simplify(schemaMapping.program, goldenOutput));
        long synthEndTime = System.currentTimeMillis();
        logger.info("=== Synthesis Time: " + (synthEndTime - synthStartTime));

        return simplified;
    }

    private static IInstance genInstAfterFilter(String benchmark, ISchema schema, IntegrityConstraint constraint, int size, long seed) {
        final int maxTrial = 1000;
        IInstance instance = RandomInstGenerator.randomInstance(schema, constraint, size, seed);
        int trial = 0;
        for (; trial < maxTrial; ++trial) {
            if (InstanceFilter.isSatisfactory(benchmark, instance)) break;
            long randomSeed = seed * maxTrial + trial;
            instance = RandomInstGenerator.randomInstance(schema, constraint, size, randomSeed);
        }
        if (trial > 0) {
            System.out.println("Replaced random instance " + (trial + 1));
        }
        return instance;
    }

    private static DocumentInstance genDocInst(DocumentSchema schema, DatalogOutput output) {
        return schema.accept(new OutputToDocInstVisitor(output));
    }

    private static DatalogProgram updateProgramDecls(DatalogProgram golden, ISchema srcSchema) {
        Set<String> outputRelations = golden.declarations.stream()
                .filter(OutputDeclaration.class::isInstance)
                .map(OutputDeclaration.class::cast)
                .map(outputDecl -> outputDecl.relation)
                .collect(Collectors.toSet());
        List<DatalogStatement> decls = new ArrayList<>();
        for (DatalogStatement decl : golden.declarations) {
            if (decl instanceof InputDeclaration) continue;
            if (decl instanceof RelationDeclaration) {
                RelationDeclaration relDecl = (RelationDeclaration) decl;
                if (!outputRelations.contains(relDecl.relation)) continue;
            }
            decls.add(decl);
        }
        decls.addAll(srcSchema.toDocumentSchema().accept(new RelationDeclGenVisitor()));
        return new DatalogProgram(decls, golden.rules, golden.facts);
    }

}

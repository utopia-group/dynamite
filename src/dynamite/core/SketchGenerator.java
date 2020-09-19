package dynamite.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.TypeDeclaration;
import dynamite.docdb.OutputDeclGenVisitor;
import dynamite.docdb.RelationDeclGenVisitor;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DocumentSchema;

public final class SketchGenerator {

    /**
     * Generate a Datalog program sketch given a value correspondence and an example.
     *
     * @param valueCorr     value correspondence
     * @param srcInst       example source instance
     * @param srcSchema     source schema
     * @param tgtCollection one collection in the target schema
     * @return the Datalog program sketch
     */
    public static Sketch generate(ValueCorr valueCorr, IInstance srcInst, DocumentSchema srcSchema, DSCollection tgtCollection) {
        return new SketchGenerator(valueCorr, srcInst, srcSchema, tgtCollection).genSketch();
    }

    // value correspondence
    private final ValueCorr valueCorr;
    // sample source instance
    private final IInstance srcInst;
    // source document schema
    private final DocumentSchema srcSchema;
    // one collection in the target document schema
    private final DSCollection tgtCollection;

    private SketchGenerator(ValueCorr valueCorr, IInstance srcInst, DocumentSchema srcSchema, DSCollection tgtCollection) {
        this.valueCorr = valueCorr;
        this.srcInst = srcInst;
        this.srcSchema = srcSchema;
        this.tgtCollection = tgtCollection;
    }

    private Sketch genSketch() {
        List<DatalogStatement> decls = genDeclarations();
        List<DatalogRule> rules = genRuleSketch();
        List<DatalogFact> facts = srcInst.toDatalogFacts();
        return new Sketch(new DatalogProgram(decls, rules, facts));
    }

    private List<DatalogStatement> genDeclarations() {
        List<DatalogStatement> decls = new ArrayList<>();
        decls.add(TypeDeclaration.intAttrTypeDecl());
        decls.add(TypeDeclaration.strAttrTypeDecl());
        decls.add(TypeDeclaration.relTypeDecl());
        decls.addAll(srcSchema.accept(new RelationDeclGenVisitor()));
        decls.addAll(buildRelDecls(tgtCollection));
        decls.addAll(tgtCollection.accept(new OutputDeclGenVisitor()));
        return decls;
    }

    private List<DatalogRule> genRuleSketch() {
        return Collections.singletonList(genRuleSketchForCollection(tgtCollection));
    }

    private DatalogRule genRuleSketchForCollection(DSCollection collection) {
        SketchHeadGenerator headGenerator = new SketchHeadGenerator();
        collection.accept(headGenerator);
        List<RelationPredicate> heads = headGenerator.getHeads();
        List<DSAtomicAttr> tgtAtomicAttrs = headGenerator.getAtomicAttributes();
        SketchBodyGenerator bodyGenerator = new SketchBodyGenerator(valueCorr, srcSchema, tgtAtomicAttrs);
        List<DatalogPredicate> bodies = bodyGenerator.genBodies();
        bodies.addAll(headGenerator.getInjectivePredicates());
        return new DatalogRule(heads, bodies);
    }

    private List<RelationDeclaration> buildRelDecls(DSCollection collection) {
        DocumentSchema schema = new DocumentSchema(Collections.singletonList(collection));
        return schema.accept(new RelationDeclGenVisitor());
    }

}

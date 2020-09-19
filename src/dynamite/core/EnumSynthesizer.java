package dynamite.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dynamite.datalog.DatalogOutput;
import dynamite.smt.SmtModel;
import dynamite.smt.ast.Constraint;
import dynamite.smt.ast.LAnd;
import dynamite.smt.ast.LBinPred;
import dynamite.smt.ast.LBinPred.Lop;
import dynamite.smt.ast.LConst;
import dynamite.smt.ast.LFormula;
import dynamite.smt.ast.LIntLiteral;
import dynamite.smt.ast.LNot;

/**
 * Schema mapping synthesizer that is based on symbolic enumeration.
 */
public final class EnumSynthesizer extends MDPSynthesizer {

    @Override
    protected Constraint analyzeMistake(SmtModel model, EncodingMap encMap, Sketch sketch, DatalogOutput actual, DatalogOutput expected) {
        List<LFormula> conjuncts = new ArrayList<>();
        for (String key : model.keySet()) {
            conjuncts.add(new LBinPred(Lop.EQ, new LConst(key), new LIntLiteral(model.getValue(key))));
        }
        LNot blockingClause = new LNot(new LAnd(conjuncts));
        return new Constraint(Collections.singletonList(blockingClause));
    }

}

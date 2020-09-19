package dynamite.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dynamite.datalog.HeadVarExtractionVisitor;
import dynamite.datalog.HoleExtractionVisitor;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.ExpressionHole;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.IntLiteral;
import dynamite.datalog.ast.StringLiteral;
import dynamite.smt.ast.Constraint;
import dynamite.smt.ast.LBinPred;
import dynamite.smt.ast.LBinPred.Lop;
import dynamite.smt.ast.LBoolFunc;
import dynamite.smt.ast.LConst;
import dynamite.smt.ast.LFormula;
import dynamite.smt.ast.LIntLiteral;
import dynamite.smt.ast.LNot;
import dynamite.smt.ast.LOr;
import dynamite.util.ListUtils;

public final class SketchEncoder {

    // name of the `isVar' predicate
    private static final String IS_VAR_PRED_NAME = "isVar";
    // list of holes in the sketch
    private final List<ExpressionHole> holes;
    // list of head variables in the sketch
    private final List<Identifier> headVars;
    // encoding map that stores the integers assigned to each identifier in Datalog holes
    private final EncodingMap encodingMap;

    public SketchEncoder(Sketch sketch) {
        this.holes = sketch.program.accept(new HoleExtractionVisitor());
        this.headVars = sketch.program.accept(new HeadVarExtractionVisitor());
        this.encodingMap = buildEncodingMap(holes);
    }

    /**
     * Get the encoding map that keeps track of which integer value maps to which identifier.
     *
     * @return the encoding map
     */
    public EncodingMap getEncodingMap() {
        return encodingMap;
    }

    /**
     * Encode the possible completions of a sketch as an SMT constraint.
     *
     * @param sketch the sketch to encode
     * @return the SMT constraint
     */
    public Constraint encode() {
        return Constraint.merge(encDomain(), encIsVarPreds(), encHeadOccurrence());
    }

    private Constraint encDomain() {
        List<LFormula> formulas = new ArrayList<>();
        for (ExpressionHole hole : holes) {
            List<LFormula> disjuncts = new ArrayList<>();
            for (DatalogExpression expr : hole.domain) {
                disjuncts.add(new LBinPred(Lop.EQ,
                        new LConst(hole.name),
                        new LIntLiteral(encodingMap.getIntegerByExpr(expr))));
            }
            formulas.add(new LOr(disjuncts));
        }
        return new Constraint(formulas);
    }

    private Constraint encIsVarPreds() {
        List<LFormula> formulas = new ArrayList<>();
        for (ExpressionHole hole : holes) {
            List<LFormula> isVarDisjuncts = new ArrayList<>();
            // negation here because isVar -> assignments = not(isVar) or assignments
            isVarDisjuncts.add(new LNot(getIsVarPred(hole.name)));
            List<LFormula> isNotVarDisjuncts = new ArrayList<>();
            // no negation here because not(isVar) -> assignments = isVar or assignments
            isNotVarDisjuncts.add(getIsVarPred(hole.name));
            for (DatalogExpression expr : hole.domain) {
                LBinPred assignment = new LBinPred(Lop.EQ,
                        new LConst(hole.name),
                        new LIntLiteral(encodingMap.getIntegerByExpr(expr)));
                if (isVar(expr)) {
                    isVarDisjuncts.add(assignment);
                } else {
                    isNotVarDisjuncts.add(assignment);
                }
            }
            formulas.add(new LOr(isVarDisjuncts));
            formulas.add(new LOr(isNotVarDisjuncts));
        }
        return new Constraint(formulas);
    }

    private static LBoolFunc getIsVarPred(String key) {
        return new LBoolFunc(IS_VAR_PRED_NAME, Collections.singletonList(new LConst(key)));
    }

    private boolean isVar(DatalogExpression expr) {
        return expr instanceof Identifier;
    }

    private Constraint encHeadOccurrence() {
        List<LFormula> formulas = new ArrayList<>();
        List<Identifier> occurOnceHeadVars = ListUtils.occurExactlyOnce(headVars);
        for (Identifier ident : occurOnceHeadVars) {
            List<LFormula> disjuncts = new ArrayList<>();
            for (ExpressionHole hole : holes) {
                if (hole.domain.contains(ident)) {
                    disjuncts.add(new LBinPred(Lop.EQ,
                            new LConst(hole.name),
                            new LIntLiteral(encodingMap.getIntegerByExpr(ident))));
                }
            }
            formulas.add(new LOr(disjuncts));
        }
        return new Constraint(formulas);
    }

    private static EncodingMap buildEncodingMap(List<ExpressionHole> holes) {
        int index = -1;
        Map<DatalogExpression, Integer> exprToValueMap = new HashMap<>();
        for (ExpressionHole hole : holes) {
            for (DatalogExpression expr : hole.domain) {
                if (!(expr instanceof Identifier || expr instanceof StringLiteral || expr instanceof IntLiteral)) {
                    throw new IllegalArgumentException(expr + " cannot appear in hole " + hole);
                }
                if (!exprToValueMap.containsKey(expr)) {
                    exprToValueMap.put(expr, ++index);
                }
            }
        }
        return new EncodingMap(exprToValueMap);
    }

}

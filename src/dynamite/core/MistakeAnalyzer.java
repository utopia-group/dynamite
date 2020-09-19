package dynamite.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.Config;
import dynamite.datalog.DatalogOutput;
import dynamite.datalog.HeadVarExtractionVisitor;
import dynamite.datalog.RelationOutput;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.TypedAttribute;
import dynamite.smt.SmtModel;
import dynamite.smt.ast.Constraint;
import dynamite.smt.ast.LAnd;
import dynamite.smt.ast.LBinPred;
import dynamite.smt.ast.LBinPred.Lop;
import dynamite.smt.ast.LBoolFunc;
import dynamite.smt.ast.LConst;
import dynamite.smt.ast.LFormula;
import dynamite.smt.ast.LIntLiteral;
import dynamite.smt.ast.LNot;
import dynamite.util.ListUtils;

public final class MistakeAnalyzer {

    private static final String DELIMITER = "?";
    private static final String IS_VAR_PRED_NAME = "isVar";
    private static final int MAXIMUM_MDP_NUM = Config.MAXIMUM_MDP_NUM;

    /**
     * Analyze the Datalog program and its output to generate a good blocking constraint.
     *
     * @param model    SMT model
     * @param encMap   encoding map
     * @param sketch   sketch of the Datalog program
     * @param actual   actual Datalog output
     * @param expected expected Datalog output
     * @return the blocking clause
     */
    public Constraint analyze(SmtModel model, EncodingMap encMap, Sketch sketch, DatalogOutput actual, DatalogOutput expected) {
        List<Identifier> headVars = sketch.program.accept(new HeadVarExtractionVisitor());
        Map<Integer, Integer> headIndexToUnivIndexMap = buildHeadIndexToUnivIndexMap(headVars, sketch);
        LFormula symmetryClause = buildSymmetryClause(model, encMap);
        LFormula isVarClause = buildIsVarClause(model, encMap);
        List<LFormula> headVarClauses = buildHeadVarClauses(headVars, headIndexToUnivIndexMap, model, encMap, actual, expected);
        List<LFormula> formulas = headVarClauses.stream()
                .map(headVarClause -> genOneBlockingClause(symmetryClause, isVarClause, headVarClause))
                .collect(Collectors.toList());
        return new Constraint(formulas);
    }

    private static LFormula buildSymmetryClause(SmtModel model, EncodingMap encMap) {
        List<String> variableHoleNames = computeVariableHoleNames(model, encMap);
        List<String> literalHoleNames = computeLiteralHoleNames(model, encMap);
        List<LFormula> conjuncts = new ArrayList<>();
        // variable equality and disequality
        for (int i = 0; i < variableHoleNames.size(); ++i) {
            String outerName = variableHoleNames.get(i);
            int outerValue = model.getValue(outerName);
            for (int j = i + 1; j < variableHoleNames.size(); ++j) {
                String innerName = variableHoleNames.get(j);
                int innerValue = model.getValue(innerName);
                Lop operator = innerValue == outerValue ? Lop.EQ : Lop.NE;
                conjuncts.add(new LBinPred(operator, new LConst(outerName), new LConst(innerName)));
            }
        }
        // literal equality
        for (String name : literalHoleNames) {
            int value = model.getValue(name);
            conjuncts.add(new LBinPred(Lop.EQ, new LConst(name), new LIntLiteral(value)));
        }
        return new LAnd(conjuncts);
    }

    private static LFormula buildIsVarClause(SmtModel model, EncodingMap encMap) {
        List<String> variableHoleNames = computeVariableHoleNames(model, encMap);
        List<String> literalHoleNames = computeLiteralHoleNames(model, encMap);
        List<LFormula> conjuncts = new ArrayList<>();
        // isVar predicates
        for (String name : variableHoleNames) {
            conjuncts.add(getIsVarPred(name));
        }
        // not isVar predicates
        for (String name : literalHoleNames) {
            conjuncts.add(new LNot(getIsVarPred(name)));
        }
        return new LAnd(conjuncts);
    }

    private static LBoolFunc getIsVarPred(String key) {
        return new LBoolFunc(IS_VAR_PRED_NAME, Collections.singletonList(new LConst(key)));
    }

    /**
     * All hole names that are instantiated to Datalog variables by the model.
     */
    private static List<String> computeVariableHoleNames(SmtModel model, EncodingMap encMap) {
        return model.keySet().stream()
                .filter(key -> encMap.isVariableInteger(model.getValue(key)))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * All hole names that are instantiated to Datalog literals by the model.
     */
    private static List<String> computeLiteralHoleNames(SmtModel model, EncodingMap encMap) {
        return model.keySet().stream()
                .filter(key -> encMap.isLiteralInteger(model.getValue(key)))
                .sorted()
                .collect(Collectors.toList());
    }

    private static List<LFormula> buildHeadVarClauses(
            List<Identifier> headVars, Map<Integer, Integer> headIndexToUnivIndexMap,
            SmtModel model, EncodingMap encMap, DatalogOutput actual, DatalogOutput expected) {
        if (headVars.size() < 1) {
            throw new IllegalArgumentException("Empty head variables");
        }
        RelationOutput actualUniverse = actual.getUnveriseOutput();
        RelationOutput expectedUniverse = expected.getUnveriseOutput();

        List<Integer> colIndices = computeColumnIndices(headVars);
        List<Set<Integer>> conflicts = new ArrayList<>();
        List<LFormula> headClauses = new ArrayList<>();
        // use a worklist to simulate the lattice
        Queue<Set<Integer>> worklist = new LinkedList<>();
        Set<Set<Integer>> addedElements = initializeWorklist(worklist, colIndices);
        while (!worklist.isEmpty()) {
            Set<Integer> currColumns = worklist.poll();
            List<Integer> projColumnList = computeProjectIndices(currColumns, headIndexToUnivIndexMap);
            RelationOutput projActualRel = actualUniverse.project(projColumnList);
            RelationOutput projExpectedRel = expectedUniverse.project(projColumnList);
            if (projActualRel.equals(projExpectedRel)) {
                addNonConflictSuccessors(worklist, addedElements, currColumns, colIndices, conflicts);
            } else {
                removeAllConflictElements(worklist, currColumns);
                conflicts.add(new HashSet<>(currColumns));
                headClauses.add(genOneHeadVarClause(currColumns, headVars, model, encMap));
                // early return if we have enough MDPs
                if (headClauses.size() >= MAXIMUM_MDP_NUM) return headClauses;
            }
        }
        return headClauses;
    }

    private static Map<Integer, Integer> buildHeadIndexToUnivIndexMap(List<Identifier> headVars, Sketch sketch) {
        List<String> orderedRelNames = sortOutputRelationNames(sketch);
        Map<String, RelationDeclaration> nameToRelDeclMap = buildNameToRelDeclMap(sketch);
        // build a map from universe identifiers to their indices
        Map<String, Integer> univIdentToIndexMap = new HashMap<>();
        int index = -1;
        for (String relName : orderedRelNames) {
            RelationDeclaration relDecl = nameToRelDeclMap.get(relName);
            for (TypedAttribute typedAttr : relDecl.attributes) {
                String identName = "v_" + relName + DELIMITER + typedAttr.attrName;
                univIdentToIndexMap.put(identName, ++index);
            }
        }
        // build a map from head variables to their indices in the universe
        Map<Integer, Integer> ret = new HashMap<>();
        for (int i = 0; i < headVars.size(); ++i) {
            Identifier headVar = headVars.get(i);
            if (!headVar.name.matches("_v_\\d+")) {
                assert univIdentToIndexMap.containsKey(headVar.name) : headVar.name;
                ret.put(i, univIdentToIndexMap.get(headVar.name));
            }
        }
        return ret;
    }

    private static List<String> sortOutputRelationNames(Sketch sketch) {
        return sketch.program.declarations.stream()
                .filter(OutputDeclaration.class::isInstance)
                .map(OutputDeclaration.class::cast)
                .map(outputDecl -> outputDecl.relation)
                .sorted()
                .collect(Collectors.toList());
    }

    private static Map<String, RelationDeclaration> buildNameToRelDeclMap(Sketch sketch) {
        return sketch.program.declarations.stream()
                .filter(RelationDeclaration.class::isInstance)
                .map(RelationDeclaration.class::cast)
                .collect(Collectors.toMap(relDecl -> relDecl.relation, relDecl -> relDecl));
    }

    private static List<Integer> computeColumnIndices(List<Identifier> headVars) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < headVars.size(); ++i) {
            Identifier headVar = headVars.get(i);
            if (!headVar.name.matches("_v_\\d+")) {
                indices.add(i);
            }
        }
        return indices;
    }

    private static List<Integer> computeProjectIndices(Set<Integer> colIndices, Map<Integer, Integer> headToUnivIndexMap) {
        return colIndices.stream()
                .map(colIndex -> headToUnivIndexMap.get(colIndex))
                .sorted()
                .collect(Collectors.toList());
    }

    private static Set<Set<Integer>> initializeWorklist(Queue<Set<Integer>> worklist, List<Integer> colIndices) {
        Set<Set<Integer>> addedElements = new HashSet<>();
        for (int colIndex : colIndices) {
            Set<Integer> element = Collections.singleton(colIndex);
            worklist.offer(element);
            addedElements.add(element);
        }
        return addedElements;
    }

    private static void addNonConflictSuccessors(
            Queue<Set<Integer>> worklist,
            Set<Set<Integer>> addedElements,
            Set<Integer> colIndices,
            List<Integer> allColIndices,
            List<Set<Integer>> conflicts) {
        List<Integer> colDiffs = ListUtils.subtract(allColIndices, colIndices);
        for (int col : colDiffs) {
            Set<Integer> colSet = new HashSet<>(colIndices);
            colSet.add(col);
            if (!isConflict(colSet, conflicts) && !addedElements.contains(colSet)) {
                worklist.offer(colSet);
                addedElements.add(colSet);
            }
        }
    }

    private static boolean isConflict(Set<Integer> colIndices, List<Set<Integer>> conflicts) {
        return conflicts.stream().anyMatch(conflict -> colIndices.containsAll(conflict));
    }

    private static void removeAllConflictElements(Queue<Set<Integer>> worklist, Set<Integer> conflict) {
        for (Iterator<Set<Integer>> iter = worklist.iterator(); iter.hasNext();) {
            Set<Integer> element = iter.next();
            if (element.containsAll(conflict)) {
                iter.remove();
            }
        }
    }

    private static LFormula genOneHeadVarClause(Set<Integer> colIndices, List<Identifier> headVars, SmtModel model, EncodingMap encMap) {
        List<LFormula> conjuncts = new ArrayList<>();
        for (int colIndex : colIndices) {
            Identifier ident = headVars.get(colIndex);
            int identValue = encMap.getIntegerByExpr(ident);
            assert model.containsValue(identValue);
            List<String> holeNames = model.getKeysByValue(identValue);
            for (String holeName : holeNames) {
                conjuncts.add(new LBinPred(Lop.EQ, new LConst(holeName), new LIntLiteral(identValue)));
            }
        }
        return new LAnd(conjuncts);
    }

    private static LFormula genOneBlockingClause(LFormula symmetryClause, LFormula isVarPreds, LFormula headVarClause) {
        return new LNot(new LAnd(Arrays.asList(new LFormula[] {
                headVarClause,
                symmetryClause,
                isVarPreds })));
    }

}

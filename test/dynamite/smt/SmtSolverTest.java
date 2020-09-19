package dynamite.smt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.smt.SmtSolver.Status;
import dynamite.smt.ast.Constraint;
import dynamite.smt.ast.LBinPred;
import dynamite.smt.ast.LBinPred.Lop;
import dynamite.smt.ast.LBoolFunc;
import dynamite.smt.ast.LConst;
import dynamite.smt.ast.LFormula;
import dynamite.smt.ast.LIntLiteral;
import dynamite.smt.ast.LNot;
import dynamite.smt.ast.LRangePred;

public class SmtSolverTest {

    @Test
    public void testUnsat() {
        SmtSolver solver = new SmtSolver();
        List<LFormula> formulas = Arrays.asList(new LFormula[] {
                new LRangePred(new LConst("x"), new LIntLiteral(9), new LIntLiteral(8)) // 9 <= x <= 8
        });
        solver.addConstraint(new Constraint(formulas));
        Assert.assertEquals(Status.UNSAT, solver.checkSAT());
    }

    @Test
    public void testSat() {
        SmtSolver solver = new SmtSolver();
        List<LFormula> formulas = Arrays.asList(new LFormula[] {
                new LRangePred(new LConst("x"), new LIntLiteral(1), new LIntLiteral(2)), // 1 <= x <= 2
                new LBinPred(Lop.EQ, new LConst("x"), new LConst("y")), // x = y
                new LBinPred(Lop.NE, new LConst("y"), new LIntLiteral(2)) // y != 2
        });
        solver.addConstraint(new Constraint(formulas));
        Assert.assertEquals(Status.SAT, solver.checkSAT());
        SmtModel model = solver.getModel();
        Assert.assertEquals(2, model.size());
        Assert.assertEquals(1, model.getValue("x")); // x = 1
        Assert.assertEquals(1, model.getValue("y")); // y = 1
    }

    @Test
    public void testFunction() {
        SmtSolver solver = new SmtSolver();
        List<LFormula> formulas = Arrays.asList(new LFormula[] {
                new LRangePred(new LConst("x"), new LIntLiteral(1), new LIntLiteral(2)), // 1 <= x <= 2
                new LBoolFunc("f", Collections.singletonList(new LConst("x"))), // f(x)
                new LNot(new LBoolFunc("f", Collections.singletonList(new LConst("y")))), // !f(y)
                new LBinPred(Lop.EQ, new LConst("y"), new LIntLiteral(1)) // y = 1
        });
        solver.addConstraint(new Constraint(formulas));
        Assert.assertEquals(Status.SAT, solver.checkSAT());
        SmtModel model = solver.getModel();
        Assert.assertEquals(2, model.size());
        Assert.assertEquals(2, model.getValue("x")); // x = 2
        Assert.assertEquals(1, model.getValue("y")); // y = 1
    }

}

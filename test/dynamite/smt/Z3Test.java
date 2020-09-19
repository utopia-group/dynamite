package dynamite.smt;

import org.junit.Assert;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Z3Test {

    @Test
    public void testUnsat() {
        Context ctx = new Context();
        BoolExpr lb = ctx.mkGe(ctx.mkIntConst("x"), ctx.mkInt(3));
        BoolExpr ub = ctx.mkLe(ctx.mkIntConst("x"), ctx.mkInt(2));
        BoolExpr conj = ctx.mkAnd(lb, ub);
        Solver solver = ctx.mkSolver();
        solver.add(conj);
        Status status = solver.check();
        Assert.assertEquals(Status.UNSATISFIABLE, status);
        ctx.close();
    }

    @Test
    public void testSat() {
        Context ctx = new Context();
        BoolExpr lb = ctx.mkGe(ctx.mkIntConst("x"), ctx.mkInt(1));
        BoolExpr ub = ctx.mkLe(ctx.mkIntConst("x"), ctx.mkInt(1));
        BoolExpr conj = ctx.mkAnd(lb, ub);
        Solver solver = ctx.mkSolver();
        solver.add(conj);
        Status status = solver.check();
        Assert.assertEquals(Status.SATISFIABLE, status);
        Model model = solver.getModel();
        Expr assignment = model.getConstInterp(ctx.mkIntConst("x"));
        Assert.assertEquals(ctx.mkInt(1), assignment);
        ctx.close();
    }

    @Test
    public void testFunction() {
        Context ctx = new Context();
        FuncDecl f1 = ctx.mkFuncDecl("f", ctx.mkIntSort(), ctx.mkBoolSort());
        FuncDecl f2 = ctx.mkFuncDecl("f", ctx.mkIntSort(), ctx.mkBoolSort()); // f2 = f1
        BoolExpr c1 = (BoolExpr) ctx.mkApp(f1, ctx.mkIntConst("x"));
        BoolExpr c2 = ctx.mkNot((BoolExpr) ctx.mkApp(f2, ctx.mkIntConst("y")));
        BoolExpr c3 = ctx.mkEq(ctx.mkIntConst("x"), ctx.mkIntConst("y"));
        BoolExpr conj = ctx.mkAnd(c1, c2, c3);
        Solver solver = ctx.mkSolver();
        solver.add(conj);
        Status status = solver.check();
        Assert.assertEquals(Status.UNSATISFIABLE, status);
        ctx.close();
    }

}

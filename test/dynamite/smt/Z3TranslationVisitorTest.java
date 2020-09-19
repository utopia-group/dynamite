package dynamite.smt;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;

import dynamite.smt.ast.Constraint;
import dynamite.smt.ast.LAnd;
import dynamite.smt.ast.LBinPred;
import dynamite.smt.ast.LBinPred.Lop;
import dynamite.smt.ast.LBoolFunc;
import dynamite.smt.ast.LConst;
import dynamite.smt.ast.LFormula;
import dynamite.smt.ast.LIntLiteral;
import dynamite.smt.ast.LNot;
import dynamite.smt.ast.LOr;
import dynamite.smt.ast.LRangePred;

public class Z3TranslationVisitorTest {

    private Context z3Ctx;
    private Z3TranslationVisitor visitor;

    public Z3TranslationVisitorTest() {
        z3Ctx = new Context();
        visitor = new Z3TranslationVisitor(z3Ctx);
    }

    @Test
    public void testConst() {
        LConst lconst = new LConst("x");
        IntExpr expected = z3Ctx.mkIntConst("x");
        Assert.assertEquals(expected, lconst.accept(visitor));
    }

    @Test
    public void testIntLiteral() {
        LIntLiteral lit = new LIntLiteral(100);
        IntExpr expected = z3Ctx.mkInt(100);
        Assert.assertEquals(expected, lit.accept(visitor));
    }

    @Test
    public void testEqBinPred() {
        LBinPred pred = new LBinPred(Lop.EQ, new LConst("x"), new LConst("y"));
        BoolExpr expected = z3Ctx.mkEq(z3Ctx.mkIntConst("x"), z3Ctx.mkIntConst("y"));
        Assert.assertEquals(expected, pred.accept(visitor));
    }

    @Test
    public void testNeBinPred() {
        LBinPred pred = new LBinPred(Lop.NE, new LConst("x"), new LConst("y"));
        BoolExpr expected = z3Ctx.mkNot(z3Ctx.mkEq(z3Ctx.mkIntConst("x"), z3Ctx.mkIntConst("y")));
        Assert.assertEquals(expected, pred.accept(visitor));
    }

    @Test
    public void testRangePred() {
        LRangePred pred = new LRangePred(new LConst("x"), new LIntLiteral(1), new LIntLiteral(2));
        IntExpr x = z3Ctx.mkIntConst("x");
        BoolExpr expected = z3Ctx.mkAnd(z3Ctx.mkLe(z3Ctx.mkInt(1), x), z3Ctx.mkLe(x, z3Ctx.mkInt(2)));
        Assert.assertEquals(expected, pred.accept(visitor));
    }

    @Test
    public void testBoolFunc() {
        LBoolFunc pred = new LBoolFunc("f", Collections.singletonList(new LConst("x")));
        Expr expected = z3Ctx.mkApp(
                z3Ctx.mkFuncDecl("f", z3Ctx.mkIntSort(), z3Ctx.mkBoolSort()),
                z3Ctx.mkIntConst("x"));
        Assert.assertEquals(expected, pred.accept(visitor));
    }

    @Test
    public void testAnd() {
        LConst varX = new LConst("x");
        LConst varY = new LConst("y");
        LAnd land = new LAnd(Arrays.asList(new LFormula[] {
                new LBinPred(Lop.EQ, varX, varY),
                new LBinPred(Lop.NE, varX, varY)
        }));
        IntExpr x = z3Ctx.mkIntConst("x");
        IntExpr y = z3Ctx.mkIntConst("y");
        BoolExpr expected = z3Ctx.mkAnd(z3Ctx.mkEq(x, y), z3Ctx.mkNot(z3Ctx.mkEq(x, y)));
        Assert.assertEquals(expected, land.accept(visitor));
    }

    @Test
    public void testOr() {
        LConst varX = new LConst("x");
        LConst varY = new LConst("y");
        LOr lor = new LOr(Arrays.asList(new LFormula[] {
                new LBinPred(Lop.EQ, varX, varY),
                new LBinPred(Lop.NE, varX, varY)
        }));
        IntExpr x = z3Ctx.mkIntConst("x");
        IntExpr y = z3Ctx.mkIntConst("y");
        BoolExpr expected = z3Ctx.mkOr(z3Ctx.mkEq(x, y), z3Ctx.mkNot(z3Ctx.mkEq(x, y)));
        Assert.assertEquals(expected, lor.accept(visitor));
    }

    @Test
    public void testNot() {
        LConst varX = new LConst("x");
        LConst varY = new LConst("y");
        LNot lnot = new LNot(new LBinPred(Lop.EQ, varX, varY));
        IntExpr x = z3Ctx.mkIntConst("x");
        IntExpr y = z3Ctx.mkIntConst("y");
        BoolExpr expected = z3Ctx.mkNot(z3Ctx.mkEq(x, y));
        Assert.assertEquals(expected, lnot.accept(visitor));
    }

    @Test
    public void testConstraint() {
        LConst varX = new LConst("x");
        LConst varY = new LConst("y");
        Constraint cstr = new Constraint(Arrays.asList(new LFormula[] {
                new LBinPred(Lop.EQ, varX, varY),
                new LBinPred(Lop.NE, varX, varY)
        }));
        IntExpr x = z3Ctx.mkIntConst("x");
        IntExpr y = z3Ctx.mkIntConst("y");
        BoolExpr expected = z3Ctx.mkAnd(z3Ctx.mkEq(x, y), z3Ctx.mkNot(z3Ctx.mkEq(x, y)));
        Assert.assertEquals(expected, cstr.accept(visitor));
    }

}

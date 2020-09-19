package dynamite.smt;

import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Sort;

import dynamite.smt.ast.Constraint;
import dynamite.smt.ast.IConstraintVisitor;
import dynamite.smt.ast.ILExprVisitor;
import dynamite.smt.ast.ILFormulaVisitor;
import dynamite.smt.ast.LAnd;
import dynamite.smt.ast.LBinPred;
import dynamite.smt.ast.LBoolFunc;
import dynamite.smt.ast.LConst;
import dynamite.smt.ast.LFormula;
import dynamite.smt.ast.LIntLiteral;
import dynamite.smt.ast.LNot;
import dynamite.smt.ast.LOr;
import dynamite.smt.ast.LRangePred;

/**
 * A visitor that translates the constraint to Z3 SMT formulas.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class Z3TranslationVisitor implements
        IConstraintVisitor<BoolExpr>,
        ILFormulaVisitor<BoolExpr>,
        ILExprVisitor<IntExpr> {

    // Z3 context
    private final Context ctx;

    public Z3TranslationVisitor(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public IntExpr visit(LConst lconst) {
        return ctx.mkIntConst(lconst.name);
    }

    @Override
    public IntExpr visit(LIntLiteral lit) {
        return ctx.mkInt(lit.value);
    }

    @Override
    public BoolExpr visit(LBinPred pred) {
        IntExpr lhs = pred.lhs.accept(this);
        IntExpr rhs = pred.rhs.accept(this);
        switch (pred.op) {
        case EQ:
            return ctx.mkEq(lhs, rhs);
        case NE:
            return ctx.mkNot(ctx.mkEq(lhs, rhs));
        default:
            throw new RuntimeException("Unknown pred: " + pred.op.name());
        }
    }

    @Override
    public BoolExpr visit(LRangePred pred) {
        IntExpr expr = pred.expr.accept(this);
        IntExpr lower = pred.lowerBound.accept(this);
        IntExpr upper = pred.upperBound.accept(this);
        return ctx.mkAnd(ctx.mkLe(lower, expr), ctx.mkLe(expr, upper));
    }

    @Override
    public BoolExpr visit(LBoolFunc pred) {
        int argCount = pred.arguments.size();
        Sort[] argSorts = new Sort[argCount];
        for (int i = 0; i < argCount; ++i) {
            argSorts[i] = ctx.mkIntSort();
        }
        FuncDecl func = ctx.mkFuncDecl(pred.name, argSorts, ctx.mkBoolSort());
        Expr[] args = new Expr[argCount];
        for (int i = 0; i < argCount; ++i) {
            args[i] = pred.arguments.get(i).accept(this);
        }
        return (BoolExpr) ctx.mkApp(func, args);
    }

    @Override
    public BoolExpr visit(LAnd land) {
        List<LFormula> conjuncts = land.conjuncts;
        BoolExpr[] exprs = new BoolExpr[conjuncts.size()];
        for (int i = 0; i < conjuncts.size(); ++i) {
            exprs[i] = conjuncts.get(i).accept(this);
        }
        return ctx.mkAnd(exprs);
    }

    @Override
    public BoolExpr visit(LOr lor) {
        List<LFormula> disjuncts = lor.disjuncts;
        BoolExpr[] exprs = new BoolExpr[disjuncts.size()];
        for (int i = 0; i < disjuncts.size(); ++i) {
            exprs[i] = disjuncts.get(i).accept(this);
        }
        return ctx.mkOr(exprs);
    }

    @Override
    public BoolExpr visit(LNot lnot) {
        return ctx.mkNot(lnot.formula.accept(this));
    }

    @Override
    public BoolExpr visit(Constraint constraint) {
        List<LFormula> formulas = constraint.formulas;
        BoolExpr[] exprs = new BoolExpr[formulas.size()];
        for (int i = 0; i < formulas.size(); ++i) {
            exprs[i] = formulas.get(i).accept(this);
        }
        return ctx.mkAnd(exprs);
    }

}

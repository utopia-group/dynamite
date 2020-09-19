package dynamite.smt;

import java.util.LinkedHashMap;
import java.util.Map;

import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;

import dynamite.smt.ast.Constraint;

/**
 * A wrapper for the Z3 SMT solver.
 */
public final class SmtSolver {

    public enum Status {
        SAT, UNSAT, UNKNOWN
    }

    // Z3 context
    private final Context ctx;
    // Z3 Solver
    private final Solver solver;

    public SmtSolver() {
        ctx = new Context();
        solver = ctx.mkSolver();
    }

    public void addConstraint(Constraint cstr) {
        solver.add(cstr.accept(new Z3TranslationVisitor(ctx)));
    }

    public Status checkSAT() {
        com.microsoft.z3.Status status = solver.check();
        switch (status) {
        case SATISFIABLE:
            return Status.SAT;
        case UNSATISFIABLE:
            return Status.UNSAT;
        case UNKNOWN:
            return Status.UNKNOWN;
        default:
            throw new RuntimeException("Unknown status: " + status.name());
        }
    }

    public SmtModel getModel() {
        Map<String, Integer> symbolToValue = new LinkedHashMap<>();
        Model model = solver.getModel();
        assert model != null;
        FuncDecl[] decls = model.getConstDecls();
        for (FuncDecl decl : decls) {
            String name = decl.getName().toString();
            assert !symbolToValue.containsKey(name);
            int value = Integer.parseInt(model.getConstInterp(decl).toString());
            symbolToValue.put(name, value);
        }
        return new SmtModel(symbolToValue);
    }

}

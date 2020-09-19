package dynamite.datalog;

import dynamite.datalog.ast.DatalogProgram;

public final class SafeSimplifier {

    /**
     * Safely simplify a Datalog program (with facts) given its expected output.
     *
     * @param program  the Datalog program to simplify
     * @param expected expected output
     * @return the simplified Datalog program
     */
    public static DatalogProgram simplify(DatalogProgram program, DatalogOutput expected) {
        DatalogProgram aggressive = program.accept(new AggressiveSimplVisitor());
        SouffleEvaluator evaluator = new SouffleEvaluator();
        DatalogOutput actual = evaluator.evaluate(aggressive);
        return actual.equals(expected)
                ? aggressive
                : program.accept(new SimplificationVisitor());
    }

}

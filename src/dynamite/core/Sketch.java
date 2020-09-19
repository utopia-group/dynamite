package dynamite.core;

import dynamite.datalog.ast.DatalogProgram;

/**
 * Data structure for schema mapping sketches.
 */
public final class Sketch {

    // a sketch is essentially a Datalog program with holes
    public final DatalogProgram program;

    public Sketch(DatalogProgram program) {
        this.program = program;
    }

    @Override
    public String toString() {
        return program.toSouffle();
    }

}

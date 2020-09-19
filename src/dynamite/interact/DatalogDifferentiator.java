package dynamite.interact;

import java.util.BitSet;
import java.util.stream.IntStream;

import dynamite.core.IInstance;
import dynamite.datalog.DatalogOutput;
import dynamite.datalog.SouffleEvaluator;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.util.InstanceUtils;

public final class DatalogDifferentiator {

    public static final int MAX_INSTANCE_SIZE = 15;

    /**
     * Check the difference between two Datalog programs on a given database instance.
     *
     * @param prog1    the first Datalog program
     * @param prog2    the second Datalog program
     * @param instance the database instance
     * @return the smallest instance that can differentiate two provided Datalog programs, or
     *         {@code null} if the two programs always yield same results on the provided instance
     */
    public static IInstance checkDifference(DatalogProgram prog1, DatalogProgram prog2, IInstance instance) {
        // quick check
        if (syntacticIdentity(prog1, prog2)) return null;
        if (!areDifferent(prog1, prog2, instance)) return null;
        // enumerative testing
        int size = InstanceUtils.computeInstanceSize(instance);
        if (size > MAX_INSTANCE_SIZE) throw new IllegalArgumentException("Size of testing instance exceeds limit " + MAX_INSTANCE_SIZE);
        return IntStream.range(1, 1 << size).parallel()
                .boxed()
                .map(value -> intToBitSet(value))
                .map(bitSet -> DatabaseSampler.sample(instance, bitSet))
                .filter(sample -> areDifferent(prog1, prog2, sample))
                .findFirst().orElse(null);
    }

    // convert an integer to a bit vector
    static BitSet intToBitSet(int value) {
        return BitSet.valueOf(new long[] { value });
    }

    // check if two programs are syntactically identical
    static boolean syntacticIdentity(DatalogProgram prog1, DatalogProgram prog2) {
        return prog1.toSouffle().equals(prog2.toSouffle());
    }

    // check if two programs have different results on the instance
    static boolean areDifferent(DatalogProgram prog1, DatalogProgram prog2, IInstance instance) {
        DatalogOutput output1 = SouffleEvaluator.evaluateOnInstance(prog1, instance);
        DatalogOutput output2 = SouffleEvaluator.evaluateOnInstance(prog2, instance);
        return !output1.equals(output2);
    }

}

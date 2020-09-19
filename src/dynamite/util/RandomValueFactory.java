package dynamite.util;

import java.util.Random;

public final class RandomValueFactory {

    // exclusive bound for random integer values
    public static final int DEFAULT_BOUND = 1000;

    // random value generator
    private final Random random;

    public RandomValueFactory(long randomSeed) {
        this.random = new Random(randomSeed);
    }

    public int mkInt() {
        return random.nextInt(DEFAULT_BOUND);
    }

    /**
     * Generate a random integer from 0 (inclusive) to max (exclusive).
     *
     * @param max the bound
     * @return a random integer in [0, max)
     */
    public int mkBoundedInt(int max) {
        return random.nextInt(max);
    }

    public float mkFloat() {
        return random.nextFloat();
    }

    public double mkDouble() {
        return random.nextDouble();
    }

    public String mkFloatString() {
        return random.nextInt(DEFAULT_BOUND) + ".0";
    }

    public String mkString() {
        return "s" + random.nextInt(DEFAULT_BOUND);
    }

    public boolean mkBool() {
        return random.nextBoolean();
    }

}

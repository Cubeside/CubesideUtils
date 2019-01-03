package de.iani.cubesideutils;

import java.util.Random;

public class RandomUtil {
    private RandomUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static final Random SHARED_RANDOM = new Random();

    // The bound is exclusive and symmetrical, so the result lies within the interval (-bound, bound).
    // May take "some" time for low bounds...
    public static double pseudoGaussian(double bound, Random ran) {
        if (bound <= 0.0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        double result;
        do {
            result = ran.nextGaussian();
        } while (result <= -bound || result >= bound);

        return result;
    }

    public static double pseudoGaussian(double bound) {
        return pseudoGaussian(bound, SHARED_RANDOM);
    }

}

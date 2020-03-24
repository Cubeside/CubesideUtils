package de.iani.cubesideutils;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class RandomUtil {
    private RandomUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static final Random SHARED_RANDOM = new Random();
    public static final Random SHARED_SECURE_RANDOM = new SecureRandom();

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

    public static <T> T randomElement(List<T> list, Random ran) {
        return list.get(ran.nextInt(list.size()));
    }

    public static <T> T randomElement(List<T> list) {
        return randomElement(list, SHARED_RANDOM);
    }

    public static int randomInt(int bound) {
        return SHARED_RANDOM.nextInt(bound);
    }

    /**
     * Creates a random password consisting of the characters 0-9 and a-z.
     *
     * @param chars
     *            the amount of chars
     * @return the random password
     */
    public static String generateRandomPassword(int chars) {
        char[] charArray = new char[chars];
        for (int i = 0; i < chars; i++) {
            int v = SHARED_SECURE_RANDOM.nextInt(36);
            if (v < 10) {
                charArray[i] = (char) ('0' + v);
            } else {
                charArray[i] = (char) ('a' + (v - 10));
            }
        }
        return new String(charArray);
    }
}

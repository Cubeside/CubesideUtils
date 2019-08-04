package de.iani.cubesideutils;

public class MathUtil {
    private MathUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static double round(double arg, int digits) {
        double shifter = Math.pow(10, digits);
        arg *= shifter;
        arg = Math.round(arg);
        arg /= shifter;
        return arg;
    }

    public static float round(float arg, int digits) {
        float shifter = (float) Math.pow(10, digits);
        arg *= shifter;
        arg = Math.round(arg);
        arg /= shifter;
        return arg;
    }
}

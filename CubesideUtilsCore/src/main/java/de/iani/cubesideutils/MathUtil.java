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

    /**
     * Result is -180.0 (inclusive) to 180.0 (exclusive)
     *
     * @param degrees
     *            the input angle in degrees
     * @return
     *         the warped angle
     */
    public static double warpDegrees(double degrees) {
        double d = degrees % 360.0;
        if (d >= 180.0) {
            d -= 360.0;
        }
        if (d < -180.0) {
            d += 360.0;
        }
        return d;
    }
}

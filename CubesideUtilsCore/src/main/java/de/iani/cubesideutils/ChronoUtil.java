package de.iani.cubesideutils;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ChronoUtil {
    private ChronoUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static long roundTimespan(long ms, ChronoUnit unit) {
        long roundingDuration = unit.getDuration().toMillis();
        long remainder = ms % roundingDuration;
        long quotient = ms / roundingDuration;

        if (remainder > 0) {
            quotient++;
        }

        return quotient * roundingDuration;
    }

    public static long roundDate(long ms, ChronoUnit unit) {
        long roundingDuration = unit.getDuration().toMillis();
        long remainder = ms % roundingDuration;
        long quotient = ms / roundingDuration;

        // round down for days and longer, round down otherwise
        if (unit.isDateBased() && remainder < 0) {
            // days shall be rounded down, but negative values are roundet up (towards 0) by default
            quotient--;
        } else if (!unit.isDateBased() && remainder > 0) {
            // shorter than days shall be rounded up, but positive values are roundet down (towards 0) by default
            quotient++;
        }

        return quotient * roundingDuration;
    }

    public static Date roundDate(Date date, ChronoUnit unit) {
        return new Date(roundDate(date.getTime(), unit));
    }

}

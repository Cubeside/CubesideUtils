package de.iani.cubesideutils;

import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class StringUtilCore {
    public static final ToIntFunction<String> CASE_IGNORING_HASHER = s -> {
        if (s == null) {
            return 0;
        }

        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);
            hash = 31 * hash + Character.toLowerCase(c);
        }
        return hash;
    };

    public static final BiPredicate<String, String> CASE_IGNORING_EQUALITY = (s1, s2) -> s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
}

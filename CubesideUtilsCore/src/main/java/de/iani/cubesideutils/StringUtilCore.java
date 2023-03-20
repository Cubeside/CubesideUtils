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

    /**
     * Filters all incomplete surrogates from a string while keeping all other chars.
     * If no incomplete surrogates are found the input string is returned.
     *
     * @param input
     *            a string
     * @return a variant of the input string where all incomplete surrogates are removed.
     */
    public static String filterIncompleteSurrogatePairs(String input) {
        StringBuilder sb = null;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c < Character.MIN_SURROGATE || c > Character.MAX_SURROGATE) {
                // no surrogate, so always valid
                if (sb != null) {
                    sb.append(c);
                }
            } else {
                if (c >= Character.MIN_HIGH_SURROGATE && c <= Character.MAX_HIGH_SURROGATE && i + 1 < input.length()) {
                    char next = input.charAt(i + 1);
                    if (next >= Character.MIN_LOW_SURROGATE && next <= Character.MAX_LOW_SURROGATE) {
                        // valid surrogate pair
                        i++;
                        if (sb != null) {
                            sb.append(c);
                            sb.append(next);
                        }
                    } else {
                        // missing low surrogate, skip the high surrogate
                        if (sb == null) {
                            sb = new StringBuilder();
                            sb.append(input, 0, i);
                        }
                    }
                } else {
                    // low surrogate without a previous high surrogate or high surrogate at the end of the string, skip
                    if (sb == null) {
                        sb = new StringBuilder();
                        sb.append(input, 0, i);
                    }
                }
            }
        }
        return sb != null ? sb.toString() : input;
    }
}

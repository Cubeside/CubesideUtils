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
                        i++;
                        if (Character.isDefined(Character.toCodePoint(c, next))) {
                            // valid surrogate pair
                            if (sb != null) {
                                sb.append(c);
                                sb.append(next);
                            }
                        } else {
                            // undefined character, skip both
                            if (sb == null) {
                                sb = new StringBuilder();
                                sb.append(input, 0, i - 1);
                            }
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

    /**
     * Filters all control characters from a string
     *
     * @param input
     *            a string
     * @param allowNewline
     *            allow new lines in the string or filter them
     * @return a variant of the input string where all control characters are removed.
     */
    public static String filterControlCharacters(String input, boolean allowNewline) {
        StringBuilder sb = null;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c < 32 && (!allowNewline || c != '\n')) || (c >= 127 && c < 160)) {
                if (sb == null) {
                    sb = new StringBuilder();
                    sb.append(input, 0, i);
                }
            } else {
                if (sb != null) {
                    sb.append(c);
                }
            }
        }
        return sb != null ? sb.toString() : input;
    }

    public static int findMatchingBrace(String s) {
        int open = 1;
        for (int i = 1; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '{' -> open++;
                case '}' -> {
                    if (--open == 0) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}
